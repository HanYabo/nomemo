package com.han.nomemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop

@Composable
fun NoMemoSelectionHeaderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NoMemoPillTextButton(
        text = text,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun NoMemoSelectionActionDock(
    selectedRecords: List<MemoryRecord>,
    onArchiveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    allSelected: Boolean = false,
    showArchiveAction: Boolean = true,
    archiveTextOverride: String? = null,
    backdrop: Backdrop = rememberLayerBackdrop(),
    modifier: Modifier = Modifier
) {
    val adaptive = rememberNoMemoAdaptiveSpec()
    val actionWidth = if (adaptive.isNarrow) 116.dp else 128.dp
    val archiveActionColor = if (isSystemInDarkTheme()) Color(0xFF2E8BFF) else Color(0xFF1677FF)
    val deleteActionColor = Color(0xFFFF4A43)
    val archiveLabel = archiveTextOverride ?: if (selectedRecords.all { it.isArchived }) {
        if (allSelected) "全部取消归档" else stringResource(R.string.action_unarchive)
    } else {
        if (allSelected) "全部归档" else stringResource(R.string.action_archive)
    }
    val deleteLabel = if (allSelected) "全部删除" else stringResource(R.string.action_delete)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (showArchiveAction) Arrangement.SpaceBetween else Arrangement.End
    ) {
        if (showArchiveAction) {
            NoMemoSelectionTextActionButton(
                text = archiveLabel,
                onClick = onArchiveClick,
                modifier = Modifier.width(actionWidth),
                contentColor = archiveActionColor,
                backdrop = backdrop
            )
        }
        NoMemoSelectionTextActionButton(
            text = deleteLabel,
            onClick = onDeleteClick,
            modifier = Modifier.width(actionWidth),
            contentColor = deleteActionColor,
            backdrop = backdrop
        )
    }
}

@Composable
private fun NoMemoSelectionTextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color,
    backdrop: Backdrop
) {
    NoMemoLiquidGlassCapsuleButton(
        text = text,
        textColor = contentColor,
        onClick = onClick,
        modifier = modifier,
        backdrop = backdrop,
        fontSize = 15.sp,
        matchCircleGlassStyle = true
    )
}
