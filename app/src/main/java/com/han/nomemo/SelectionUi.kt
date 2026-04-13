package com.han.nomemo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    showArchiveAction: Boolean = true,
    archiveTextOverride: String? = null,
    modifier: Modifier = Modifier
) {
    val adaptive = rememberNoMemoAdaptiveSpec()
    val actionWidth = if (adaptive.isNarrow) 116.dp else 128.dp
    val archiveActionColor = Color(0xFF4A9DFF)
    val deleteActionColor = Color(0xFFFF4A43)
    val archiveLabel = archiveTextOverride ?: if (selectedRecords.all { it.isArchived }) {
        stringResource(R.string.action_unarchive)
    } else {
        stringResource(R.string.action_archive)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (showArchiveAction) Arrangement.SpaceBetween else Arrangement.End
    ) {
        if (showArchiveAction) {
            NoMemoSelectionTextActionButton(
                text = archiveLabel,
                onClick = onArchiveClick,
                modifier = Modifier.width(actionWidth),
                contentColor = archiveActionColor
            )
        }
        NoMemoSelectionTextActionButton(
            text = stringResource(R.string.action_delete),
            onClick = onDeleteClick,
            modifier = Modifier.width(actionWidth),
            contentColor = deleteActionColor
        )
    }
}

@Composable
private fun NoMemoSelectionTextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentColor: Color
) {
    NoMemoLiquidGlassCapsuleButton(
        text = text,
        textColor = contentColor,
        onClick = onClick,
        modifier = modifier,
        fontSize = 15.sp
    )
}
