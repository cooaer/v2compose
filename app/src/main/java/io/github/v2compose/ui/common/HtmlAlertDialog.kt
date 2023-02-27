package io.github.v2compose.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import io.github.v2compose.R


@Composable
fun HtmlAlertDialog(
    title: String? = null,
    content: String,
    onUriClick: ((uri: String) -> Unit)? = null
) {
    var showDialog by remember(content) { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(onDismissRequest = { showDialog = false },
            title = { title?.let { Text(title) } },
            text = {
                HtmlContent(content = content, onUriClick = onUriClick)
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(stringResource(id = R.string.ok))
                }
            })
    }
}
