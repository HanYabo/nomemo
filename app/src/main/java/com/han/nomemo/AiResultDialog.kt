package com.han.nomemo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AiResultDialog(
    preview: AiResultPreview,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (preview.title.isBlank()) "AI 分析结果" else preview.title,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                if (preview.summary.isNotBlank()) {
                    Text(text = preview.summary)
                }
                if (preview.memory.isNotBlank()) {
                    Text(
                        text = preview.memory,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                if (preview.analysis.isNotBlank() && preview.analysis != preview.memory) {
                    Text(
                        text = "分析：${preview.analysis}",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                if (preview.engine.isNotBlank()) {
                    Text(
                        text = "来源：${preview.engine}",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("知道了")
            }
        }
    )
}
