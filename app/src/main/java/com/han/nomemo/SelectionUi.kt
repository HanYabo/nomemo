package com.han.nomemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

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
    modifier: Modifier = Modifier
) {
    val palette = rememberNoMemoPalette()
    val archiveLabel = if (selectedRecords.all { it.isArchived }) {
        stringResource(R.string.action_unarchive)
    } else {
        stringResource(R.string.action_archive)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NoMemoWideActionButton(
            text = archiveLabel,
            iconRes = R.drawable.ic_sheet_calendar,
            onClick = onArchiveClick,
            modifier = Modifier.weight(1f),
            containerColor = palette.glassFill,
            contentColor = palette.textPrimary,
            borderColor = palette.glassStroke
        )
        NoMemoWideActionButton(
            text = stringResource(R.string.action_delete),
            iconRes = R.drawable.ic_nm_delete,
            onClick = onDeleteClick,
            modifier = Modifier.weight(1f),
            containerColor = palette.accent,
            contentColor = palette.onAccent,
            borderColor = palette.accent
        )
    }
}
