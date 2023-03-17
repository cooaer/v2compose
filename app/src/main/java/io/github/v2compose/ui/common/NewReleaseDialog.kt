package io.github.v2compose.ui.common

import android.widget.ScrollView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.children
import io.github.v2compose.R
import io.github.v2compose.network.bean.Release


@Composable
fun NewReleaseDialog(
    release: Release,
    onIgnoreClick: () -> Unit,
    onCancelClick: () -> Unit,
    onOkClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelClick,
        confirmButton = { TextButton(onClick = onOkClick) { Text(stringResource(id = R.string.goto_update)) } },
        title = { Text(stringResource(id = R.string.has_new_updates)) },
        text = { NewReleaseBody(release.body ?: release.name ?: release.tagName) },
        dismissButton = {
            TextButton(onClick = onIgnoreClick) {
                Text(stringResource(id = R.string.ignore_this_release))
            }
        }
    )
}

@Composable
private fun NewReleaseBody(text: String) {
    val markwon = rememberMarkwon()
    val contentColor = LocalContentColor.current
    val bodyLineSpacing = with(LocalDensity.current) { 6.sp.toPx() }
    AndroidView(
        factory = { context ->
            val bodyView = TextView(context).apply {
                setTextColor(contentColor.toArgb())
                textSize = 15f
                setLineSpacing(bodyLineSpacing, 1f)
            }
            ScrollView(context).apply {
                addView(bodyView)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        update = {
            val bodyView = it.children.first() as TextView
            markwon.setMarkdown(bodyView, text)
        },
    )
}