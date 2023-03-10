package io.github.v2compose.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.v2compose.R


@Composable
fun SingleChoiceListDialog(
    title: String? = null,
    entries: List<String>,
    selectedIndex: Int,
    onEntryClick: (Int) -> Unit,
    onCancel: (() -> Unit)
) {
    AlertDialog(onDismissRequest = onCancel,
        confirmButton = {
            TextButton(onClick = { onCancel() }) {
                Text(stringResource(id = R.string.ok))
            }
        },
        title = {
            title?.let {
                Text(title)
            }
        },
        text = {
            Column {
                entries.forEachIndexed { index, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onEntryClick(index) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = index == selectedIndex,
                            onClick = { onEntryClick(index) })
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(entry)
                    }
                }
            }
        }
    )
}


@Preview(showBackground = true, widthDp = 440)
@Composable
fun SingleChoiceListDialogPreview() {
    SingleChoiceListDialog(
        title = "浏览器",
        entries = listOf("内置浏览器", "外置浏览器"),
        selectedIndex = 0,
        onEntryClick = {},
        onCancel = {})
}