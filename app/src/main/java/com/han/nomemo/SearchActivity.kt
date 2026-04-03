package com.han.nomemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : BaseComposeActivity() {
    private lateinit var memoryStore: MemoryStore
    private var records by mutableStateOf<List<MemoryRecord>>(emptyList())
    private var hasLoadedRecords by mutableStateOf(false)
    private var hasHandledInitialResume = false
    private var memoryChangeRegistered = false
    private var refreshJob: Job? = null

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
                val result = memoryStore.loadActiveRecords()
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
        onOpenDetail: (MemoryRecord) -> Unit,
        onClose: () -> Unit
    ) {
        val adaptive = rememberNoMemoAdaptiveSpec()
        val palette = rememberNoMemoPalette()
        val listState = rememberLazyListState()
        var query by rememberSaveable { mutableStateOf("") }
        val filteredRecords = remember(records, query) {
            val normalizedQuery = query.trim().lowercase()
            if (normalizedQuery.isBlank()) {
                records
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
        val showCenteredEmptyState = hasLoadedRecords && filteredRecords.isEmpty()

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
                        Text(
                            text = stringResource(R.string.search_page_title),
                            color = palette.textPrimary,
                            fontSize = spec.titleSize,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        )
                        NoMemoSearchBarCard(
                            value = query,
                            onValueChange = { query = it },
                            onClose = onClose
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
                            iconRes = if (query.isBlank()) R.drawable.ic_nm_memory else R.drawable.ic_nm_search,
                            title = if (query.isBlank()) {
                                stringResource(R.string.no_records)
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

    companion object {
        fun createIntent(context: Context): Intent = Intent(context, SearchActivity::class.java)
    }
}
