package io.github.v2compose.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import io.github.v2compose.R


@Composable
fun HtmlAlertDialog(initialContent: String, onUriClick: ((uri: String) -> Unit)? = null) {
    var content by remember(initialContent) { mutableStateOf(initialContent) }

    if (content.isNotEmpty()) {
        AlertDialog(onDismissRequest = { content = "" }, title = {
            HtmlContent(content = content, onUriClick = onUriClick)
        }, confirmButton = {
            TextButton(onClick = { content = "" }) {
                Text(stringResource(id = R.string.ok))
            }
        })
    }
}
