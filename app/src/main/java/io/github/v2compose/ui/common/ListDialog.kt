package io.github.v2compose.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.v2compose.R


@Composable
fun SingleChoiceListDialog(
    title: String? = null,
    entries: List<String>,
    selectedIndex: Int,
    onEntryClick: (Int) -> Unit,
    onCancel: (() -> Unit)
) {
    Dialog(onDismissRequest = onCancel) {
        SingleChoiceListDialogContent(
            title = title,
            entries = entries,
            selectedIndex = selectedIndex,
            onEntryClick = onEntryClick,
            onCancel = onCancel
        )
    }
}

@Composable
private fun SingleChoiceListDialogContent(
    title: String? = null,
    entries: List<String>,
    selectedIndex: Int,
    onEntryClick: (Int) -> Unit,
    onCancel: (() -> Unit)
) {
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (!title.isNullOrEmpty()) {
                Text(
                    title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
            }
            entries.forEachIndexed { index, entry ->
                Row(
                    modifier = Modifier.clickable { onEntryClick(index) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = index == selectedIndex,
                        onClick = { onEntryClick(index) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        entry,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                    )
                }
            }

            TextButton(modifier = Modifier.align(Alignment.End), onClick = { onCancel() }) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 440)
@Composable
fun SingleChoiceListDialogContentPreview() {
    SingleChoiceListDialogContent(
        title = "浏览器",
        entries = listOf("内置浏览器", "外置浏览器"),
        selectedIndex = 0,
        onEntryClick = {},
        onCancel = {})
}