package com.han.nomemo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource

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
    val palette = rememberNoMemoPalette()
    val adaptive = rememberNoMemoAdaptiveSpec()
    val actionWidth = if (adaptive.isNarrow) 116.dp else 128.dp
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
                containerColor = palette.glassFill,
                contentColor = palette.textPrimary,
                borderColor = palette.glassStroke
            )
        }
        NoMemoSelectionTextActionButton(
            text = stringResource(R.string.action_delete),
            onClick = onDeleteClick,
            modifier = Modifier.width(actionWidth),
            containerColor = palette.accent,
            contentColor = palette.onAccent,
            borderColor = palette.accent
        )
    }
}

@Composable
private fun NoMemoSelectionTextActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    borderColor: androidx.compose.ui.graphics.Color
) {
    PressScaleBox(
        onClick = onClick,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            border = BorderStroke(1.dp, borderColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 12.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
