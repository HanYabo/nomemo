package com.han.nomemo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddMemoryActivity : BaseComposeActivity() {
    private lateinit var memoryStore: MemoryStore
    private lateinit var aiMemoryService: AiMemoryService
    private var executorService: ExecutorService? = null

    private var inputText by mutableStateOf("")
    private var selectedImageUri by mutableStateOf<Uri?>(null)
    private var imageStatusText by mutableStateOf("")
    private var aiMode by mutableStateOf(false)
    private var selectedGroupCode by mutableStateOf(CategoryCatalog.GROUP_LIFE)
    private var selectedCategoryOption by mutableStateOf(CategoryCatalog.getLifeCategories().first())
    private var reminderEnabled by mutableStateOf(false)
    private var selectedReminderAt by mutableStateOf(0L)
    private var saving by mutableStateOf(false)

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                return@registerForActivityResult
            }
            selectedImageUri = uri
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {
                // Provider may not support persistable permissions.
            }
            imageStatusText = getString(R.string.image_selected, queryDisplayName(uri))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        aiMemoryService = AiMemoryService(this)
        executorService = Executors.newSingleThreadExecutor()
        imageStatusText = getString(R.string.image_not_selected)

        setContent {
            AddMemoryContent(
                onBack = { finishWithTransition() },
                onPickImage = { pickImageLauncher.launch(arrayOf("image/*")) },
                onPickReminderTime = { openDateTimePicker() },
                onSave = { saveRecord() }
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService?.shutdownNow()
        executorService = null
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit)
    }

    private fun finishWithTransition() {
        finish()
    }

    private fun updateGroup(groupCode: String) {
        selectedGroupCode = groupCode
        val options = CategoryCatalog.getCategoriesByGroup(groupCode)
        selectedCategoryOption = options.first()
        if (groupCode != CategoryCatalog.GROUP_WORK) {
            reminderEnabled = false
            selectedReminderAt = 0L
        }
    }

    private fun updateCategoryByIndex(index: Int) {
        val options = CategoryCatalog.getCategoriesByGroup(selectedGroupCode)
        if (index in options.indices) {
            selectedCategoryOption = options[index]
        }
    }

    private fun openDateTimePicker() {
        val calendar = Calendar.getInstance()
        if (selectedReminderAt > 0L) {
            calendar.timeInMillis = selectedReminderAt
        } else {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        calendar.set(Calendar.SECOND, 0)
                        selectedReminderAt = calendar.timeInMillis
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun reminderText(): String {
        if (selectedReminderAt <= 0L) {
            return getString(R.string.reminder_not_set)
        }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return getString(R.string.reminder_set_time, dateFormat.format(selectedReminderAt))
    }

    private fun saveRecord() {
        val input = inputText.trim()
        if (TextUtils.isEmpty(input) && selectedImageUri == null) {
            Toast.makeText(this, R.string.input_required, Toast.LENGTH_SHORT).show()
            return
        }
        if (aiMode) {
            createAiRecord(input)
        } else {
            createNormalRecord(input)
        }
    }

    private fun resolveReminderAt(
        groupCode: String,
        reminderEnabled: Boolean,
        reminderAt: Long
    ): Long {
        if (groupCode != CategoryCatalog.GROUP_WORK || !reminderEnabled) {
            return 0L
        }
        if (reminderAt > 0L) {
            return reminderAt
        }
        return System.currentTimeMillis() + 60L * 60L * 1000L
    }

    private fun createNormalRecord(input: String) {
        val imageUriText = selectedImageUri?.toString() ?: ""
        val reminderAt = resolveReminderAt(selectedGroupCode, reminderEnabled, selectedReminderAt)
        val memoryText = if (input.isBlank()) getString(R.string.memory_saved_screenshot) else input
        val record = MemoryRecord(
            System.currentTimeMillis(),
            MemoryRecord.MODE_NORMAL,
            input,
            imageUriText,
            "",
            memoryText,
            "manual",
            selectedGroupCode,
            selectedCategoryOption.categoryCode,
            selectedCategoryOption.categoryName,
            reminderAt,
            false
        )
        onRecordSaved(record, false)
    }

    private fun createAiRecord(input: String) {
        val localUri = selectedImageUri
        val imageUriText = localUri?.toString() ?: ""
        val groupCode = selectedGroupCode
        val category = selectedCategoryOption
        val reminderAt = resolveReminderAt(groupCode, reminderEnabled, selectedReminderAt)

        saving = true
        Toast.makeText(this, R.string.saving_ai, Toast.LENGTH_SHORT).show()
        executorService?.execute {
            try {
                val result = aiMemoryService.generateMemory(input, localUri)
                val record = MemoryRecord(
                    System.currentTimeMillis(),
                    MemoryRecord.MODE_AI,
                    input,
                    imageUriText,
                    result.analysis,
                    result.memory,
                    result.engine,
                    groupCode,
                    category.categoryCode,
                    category.categoryName,
                    reminderAt,
                    false
                )
                runOnUiThread { onRecordSaved(record, false) }
            } catch (_: Exception) {
                runOnUiThread {
                    val fallbackMemory = if (input.isBlank()) getString(R.string.memory_saved_screenshot) else input
                    val fallbackRecord = MemoryRecord(
                        System.currentTimeMillis(),
                        MemoryRecord.MODE_AI,
                        input,
                        imageUriText,
                        getString(R.string.memory_fallback_analysis),
                        fallbackMemory,
                        "local",
                        groupCode,
                        category.categoryCode,
                        category.categoryName,
                        reminderAt,
                        false
                    )
                    onRecordSaved(fallbackRecord, true)
                }
            }
        }
    }

    private fun onRecordSaved(record: MemoryRecord, usedFallback: Boolean) {
        memoryStore.prependRecord(record)
        setResult(RESULT_OK)
        saving = false
        if (usedFallback) {
            Toast.makeText(this, R.string.ai_error_fallback, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show()
        }
        finishWithTransition()
    }

    private fun queryDisplayName(uri: Uri): String {
        var cursor: Cursor? = null
        return try {
            cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) {
                    cursor.getString(index)
                } else {
                    uri.lastPathSegment ?: "image"
                }
            } else {
                uri.lastPathSegment ?: "image"
            }
        } catch (_: Exception) {
            uri.lastPathSegment ?: "image"
        } finally {
            cursor?.close()
        }
    }

    @Composable
    private fun AddMemoryContent(
        onBack: () -> Unit,
        onPickImage: () -> Unit,
        onPickReminderTime: () -> Unit,
        onSave: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val canReminder = selectedGroupCode == CategoryCatalog.GROUP_WORK
        val groups = listOf(
            GroupOption(CategoryCatalog.GROUP_LIFE, stringResource(R.string.group_life)),
            GroupOption(CategoryCatalog.GROUP_WORK, stringResource(R.string.group_work))
        )
        val categories = remember(selectedGroupCode) {
            CategoryCatalog.getCategoriesByGroup(selectedGroupCode)
        }

        LaunchedEffect(selectedGroupCode) {
            if (!categories.any { it.categoryCode == selectedCategoryOption.categoryCode }) {
                selectedCategoryOption = categories.first()
            }
        }

        var entered by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { entered = true }
        val panelAlpha by animateFloatAsState(
            targetValue = if (entered) 1f else 0f,
            animationSpec = tween(460),
            label = "panelAlpha"
        )
        val panelOffsetY by animateFloatAsState(
            targetValue = if (entered) 0f else 28f,
            animationSpec = tween(460),
            label = "panelOffset"
        )
        val panelScale by animateFloatAsState(
            targetValue = if (entered) 1f else 0.985f,
            animationSpec = tween(460),
            label = "panelScale"
        )

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(
                            start = spec.pageHorizontalPadding,
                            top = spec.pageTopPadding,
                            end = spec.pageHorizontalPadding,
                            bottom = 26.dp
                        )
                ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PressScaleBox(
                        onClick = onBack,
                        modifier = Modifier
                            .size(if (spec.isNarrow) 44.dp else 48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(palette.glassFill)
                    ) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_media_previous),
                            contentDescription = stringResource(R.string.back),
                            tint = palette.textPrimary,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.add_page_title),
                            color = palette.textPrimary,
                            fontSize = when {
                                spec.isNarrow -> 24.sp
                                spec.widthClass == NoMemoWidthClass.EXPANDED -> 30.sp
                                else -> 27.sp
                            },
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.add_page_subtitle),
                            color = palette.textSecondary,
                            fontSize = spec.subtitleSize
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .graphicsLayer {
                            alpha = panelAlpha
                            translationY = panelOffsetY
                            scaleX = panelScale
                            scaleY = panelScale
                        }
                        .clip(RoundedCornerShape(if (spec.isNarrow) 22.dp else 26.dp))
                        .background(palette.glassFill)
                        .border(1.dp, palette.glassStroke, RoundedCornerShape(if (spec.isNarrow) 22.dp else 26.dp))
                        .padding(if (spec.isNarrow) 12.dp else 14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.title_new_record),
                        color = palette.textPrimary,
                        fontSize = if (spec.widthClass == NoMemoWidthClass.EXPANDED) 17.sp else 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (spec.isNarrow) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = stringResource(R.string.category_group),
                                    color = palette.textSecondary,
                                    fontSize = 12.sp
                                )
                                ChoiceDropdown(
                                    modifier = Modifier.padding(top = 6.dp),
                                    selectedText = groups.first { it.code == selectedGroupCode }.name,
                                    options = groups.map { it.name },
                                    enabled = !saving,
                                    onSelect = { index -> updateGroup(groups[index].code) }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = stringResource(R.string.category_sub),
                                    color = palette.textSecondary,
                                    fontSize = 12.sp
                                )
                                ChoiceDropdown(
                                    modifier = Modifier.padding(top = 6.dp),
                                    selectedText = selectedCategoryOption.categoryName,
                                    options = categories.map { it.categoryName },
                                    enabled = !saving,
                                    onSelect = { index -> updateCategoryByIndex(index) }
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.category_group),
                                    color = palette.textSecondary,
                                    fontSize = 12.sp
                                )
                                ChoiceDropdown(
                                    modifier = Modifier.padding(top = 6.dp),
                                    selectedText = groups.first { it.code == selectedGroupCode }.name,
                                    options = groups.map { it.name },
                                    enabled = !saving,
                                    onSelect = { index -> updateGroup(groups[index].code) }
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.category_sub),
                                    color = palette.textSecondary,
                                    fontSize = 12.sp
                                )
                                ChoiceDropdown(
                                    modifier = Modifier.padding(top = 6.dp),
                                    selectedText = selectedCategoryOption.categoryName,
                                    options = categories.map { it.categoryName },
                                    enabled = !saving,
                                    onSelect = { index -> updateCategoryByIndex(index) }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .height(spec.addInputHeight),
                        enabled = !saving,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.hint_text_input),
                                color = palette.textTertiary,
                                fontSize = 14.sp
                            )
                        },
                        maxLines = 6,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            color = palette.textPrimary,
                            fontSize = 14.sp
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )

                    if (spec.isNarrow) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            SimpleButton(
                                text = stringResource(R.string.pick_screenshot),
                                enabled = !saving,
                                onClick = onPickImage,
                                buttonHeight = 38.dp,
                                textSize = 12.sp
                            )
                            Text(
                                text = imageStatusText,
                                color = palette.textSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SimpleButton(
                                text = stringResource(R.string.pick_screenshot),
                                enabled = !saving,
                                onClick = onPickImage,
                                buttonHeight = 40.dp,
                                textSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = imageStatusText,
                                color = palette.textSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (selectedImageUri != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        AndroidView(
                            factory = { ctx ->
                                ImageView(ctx).apply {
                                    scaleType = ImageView.ScaleType.CENTER_CROP
                                    clipToOutline = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(spec.addPreviewHeight)
                                .clip(RoundedCornerShape(22.dp))
                                .background(palette.glassFillSoft),
                            update = { imageView ->
                                try {
                                    imageView.setImageURI(selectedImageUri)
                                } catch (_: Exception) {
                                    imageView.setImageDrawable(null)
                                }
                            }
                        )
                    }

                    Text(
                        text = stringResource(R.string.record_mode_label),
                        color = palette.textSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            GlassChip(
                                text = stringResource(R.string.mode_normal),
                                selected = !aiMode,
                                onClick = { if (!saving) aiMode = false },
                                modifier = Modifier.fillMaxWidth(),
                                horizontalPadding = if (spec.isNarrow) 12.dp else 16.dp,
                                textStyle = TextStyle(fontSize = spec.chipTextSize, fontWeight = FontWeight.Bold)
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            GlassChip(
                                text = stringResource(R.string.mode_ai),
                                selected = aiMode,
                                onClick = { if (!saving) aiMode = true },
                                modifier = Modifier.fillMaxWidth(),
                                horizontalPadding = if (spec.isNarrow) 12.dp else 16.dp,
                                textStyle = TextStyle(fontSize = spec.chipTextSize, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    if (aiMode) {
                        Text(
                            text = stringResource(R.string.ai_hint),
                            color = palette.textTertiary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (canReminder) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.enable_reminder),
                                color = palette.textPrimary,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = {
                                    if (!saving) {
                                        reminderEnabled = it
                                        if (!it) {
                                            selectedReminderAt = 0L
                                        }
                                    }
                                },
                                enabled = !saving
                            )
                        }

                        if (reminderEnabled) {
                            if (spec.isNarrow) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    SimpleButton(
                                        text = stringResource(R.string.pick_reminder_time),
                                        enabled = !saving,
                                        onClick = onPickReminderTime,
                                        buttonHeight = 38.dp,
                                        textSize = 12.sp
                                    )
                                    Text(
                                        text = reminderText(),
                                        color = palette.textSecondary,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SimpleButton(
                                        text = stringResource(R.string.pick_reminder_time),
                                        enabled = !saving,
                                        onClick = onPickReminderTime,
                                        buttonHeight = 40.dp,
                                        textSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = reminderText(),
                                        color = palette.textSecondary,
                                        fontSize = 12.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    PressScaleBox(
                        onClick = { if (!saving) onSave() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (spec.isNarrow) 46.dp else 49.dp)
                            .padding(top = 14.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(if (saving) palette.glassFillSoft else palette.accent)
                    ) {
                        Text(
                            text = stringResource(R.string.add_now),
                            color = palette.onAccent,
                            fontSize = if (spec.isNarrow) 15.sp else 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
    }

    @Composable
    private fun ChoiceDropdown(
        selectedText: String,
        options: List<String>,
        enabled: Boolean,
        onSelect: (Int) -> Unit,
        modifier: Modifier = Modifier
    ) {
        val palette = rememberNoMemoPalette()
        var expanded by remember { mutableStateOf(false) }
        Box(modifier = modifier.fillMaxWidth()) {
            PressScaleBox(
                onClick = { if (enabled) expanded = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(palette.glassFillSoft)
                    .border(1.dp, palette.glassStroke, RoundedCornerShape(16.dp))
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = selectedText,
                    color = if (enabled) palette.textPrimary else palette.textTertiary,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Icon(
                    painter = painterResource(android.R.drawable.arrow_down_float),
                    contentDescription = null,
                    tint = palette.textSecondary,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(18.dp)
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(palette.memoBgStart)
            ) {
                options.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                color = palette.textPrimary,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            expanded = false
                            onSelect(index)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun SimpleButton(
        text: String,
        enabled: Boolean,
        onClick: () -> Unit,
        buttonHeight: androidx.compose.ui.unit.Dp,
        textSize: TextUnit
    ) {
        val palette = rememberNoMemoPalette()
        PressScaleBox(
            onClick = { if (enabled) onClick() },
            modifier = Modifier
                .height(buttonHeight)
                .clip(RoundedCornerShape(20.dp))
                .background(if (enabled) palette.glassFillSoft else palette.glassFill)
                .border(1.dp, palette.glassStroke, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp)
        ) {
            Text(
                text = text,
                color = if (enabled) palette.textPrimary else palette.textTertiary,
                fontSize = textSize,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    data class GroupOption(val code: String, val name: String)
}
