package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : BaseComposeActivity() {
    companion object {
        private const val EXTRA_ARCHIVED_ONLY = "extra_archived_only"

        fun createIntent(context: Context): Intent = Intent(context, SearchActivity::class.java)

        fun createArchivedIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
                .putExtra(EXTRA_ARCHIVED_ONLY, true)
        }
    }

    private lateinit var memoryStore: MemoryStore
    private var records by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var hasHandledInitialResume = false
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null
    private val archivedOnly: Boolean by lazy {
        intent.getBooleanExtra(EXTRA_ARCHIVED_ONLY, false)
    }

    private val memoryChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            refreshRecords()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryStore = MemoryStore(this)
        setContent {
            SearchContent(
                records = records,
                hasLoadedRecords = hasLoadedRecords,
                archivedOnly = archivedOnly,
                onOpenDetail = { record -> openDetailPage(record.recordId) },
                onClose = { finish() }
            )
        }
        refreshRecords()
    }

    override fun onResume() {
        super.onResume()
        if (!hasHandledInitialResume) {
            hasHandledInitialResume = true
            return
        }
        refreshRecords()
    }

    override fun onStart() {
        super.onStart()
        registerMemoryChangeReceiver()
    }

    override fun onStop() {
        unregisterMemoryChangeReceiver()
        super.onStop()
    }

    private fun refreshRecords() {
        refreshJob?.cancel()
        refreshJob = lifecycleScope.launch {
            val loadedRecords = withContext(Dispatchers.IO) {
                val result = if (archivedOnly) {
                    memoryStore.loadArchivedRecords()
                } else {
                    memoryStore.loadActiveRecords()
                }
                prewarmMemoryThumbnailCache(applicationContext, result)
                result
            }
            records = loadedRecords
            hasLoadedRecords = true
        }
    }

    private fun registerMemoryChangeReceiver() {
        if (memoryChangeRegistered) {
            return
        }
        ContextCompat.registerReceiver(
            this,
            memoryChangeReceiver,
            IntentFilter(MemoryStoreNotifier.ACTION_RECORDS_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        memoryChangeRegistered = true
    }

    private fun unregisterMemoryChangeReceiver() {
        if (!memoryChangeRegistered) {
            return
        }
        unregisterReceiver(memoryChangeReceiver)
        memoryChangeRegistered = false
    }

    private fun openDetailPage(recordId: String) {
        startActivity(MemoryDetailActivity.createIntent(this, recordId))
    }

    @Composable
    private fun SearchContent(
        records: List<MemoryRecord>,
        hasLoadedRecords: Boolean,
        archivedOnly: Boolean,
        onOpenDetail: (MemoryRecord) -> Unit,
        onClose: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val listState = rememberLazyListState()
        var query by rememberSaveable { mutableStateOf("") }
        val searchFocusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        val filteredRecords = remember(records, query) {
            val normalizedQuery = query.trim().lowercase()
            if (normalizedQuery.isBlank()) {
                emptyList()
            } else {
                records.filter { record ->
                    listOf(
                        record.title,
                        record.summary,
                        record.memory,
                        record.sourceText,
                        record.analysis,
                        record.categoryName
                    ).joinToString("\n") { it.orEmpty() }
                        .lowercase()
                        .contains(normalizedQuery)
                }
            }
        }
        val hasQuery = query.trim().isNotEmpty()
        val showCenteredEmptyState = hasLoadedRecords && filteredRecords.isEmpty()

        LaunchedEffect(Unit) {
            delay(180)
            searchFocusRequester.requestFocus()
            delay(60)
            keyboardController?.show()
        }

        NoMemoBackground {
            ResponsiveContentFrame(spec = adaptive) { spec ->
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .padding(
                                start = spec.pageHorizontalPadding,
                                top = spec.pageTopPadding,
                            end = spec.pageHorizontalPadding,
                            bottom = 0.dp
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(spec.topActionButtonSize)
                                .padding(top = 2.dp)
                        ) {
                            GlassIconCircleButton(
                                iconRes = R.drawable.ic_sheet_back,
                                contentDescription = stringResource(R.string.back),
                                onClick = onClose,
                                modifier = Modifier.align(Alignment.CenterStart),
                                size = spec.topActionButtonSize
                            )
                            Text(
                                text = if (archivedOnly) {
                                    stringResource(R.string.archived_search_page_title)
                                } else {
                                    stringResource(R.string.search_page_title)
                                },
                                color = palette.textPrimary,
                                fontSize = if (spec.isNarrow) 20.sp else 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        SearchSheetStyleBar(
                            value = query,
                            onValueChange = { query = it },
                            focusRequester = searchFocusRequester
                        )

                        if (!hasLoadedRecords || filteredRecords.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                state = listState,
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                    top = 14.dp,
                                    bottom = 24.dp
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = filteredRecords,
                                    key = { it.recordId },
                                    contentType = {
                                        if (it.imageUri.isNullOrBlank()) "record_plain" else "record_image"
                                    }
                                ) { record ->
                                    RecordCard(
                                        record = record,
                                        palette = palette,
                                        adaptive = adaptive,
                                        allowImageLoading = true,
                                        showShadow = false,
                                        darkCardBackgroundOverride = androidx.compose.ui.graphics.Color(0xFF1A1A1C),
                                        onClick = { onOpenDetail(record) }
                                    )
                                }
                            }
                        }
                    }

                    if (showCenteredEmptyState) {
                        NoMemoEmptyState(
                            iconRes = R.drawable.ic_nm_search,
                            title = if (!hasQuery) {
                                if (archivedOnly) stringResource(R.string.archived_search_idle_title) else stringResource(R.string.search_idle_title)
                            } else {
                                stringResource(R.string.search_empty)
                            },
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = spec.pageHorizontalPadding)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun SearchSheetStyleBar(
        value: String,
        onValueChange: (String) -> Unit,
        focusRequester: FocusRequester
    ) {
        val palette = rememberNoMemoPalette()
        val isDark = androidx.compose.foundation.isSystemInDarkTheme()
        val searchSurface = if (isDark) Color(0xFF1A1A1C) else Color.White.copy(alpha = 0.995f)

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = noMemoG2RoundedShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = searchSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isDark) 52.dp else 54.dp)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_nm_search),
                    contentDescription = stringResource(R.string.action_search),
                    tint = palette.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = TextStyle(
                        color = palette.textPrimary,
                        fontSize = 15.sp
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                        .padding(start = 10.dp)
                ) { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isBlank()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                color = palette.textTertiary,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                }
                if (value.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = { onValueChange("") }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_sheet_close),
                            contentDescription = stringResource(R.string.cancel),
                            tint = palette.textTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
