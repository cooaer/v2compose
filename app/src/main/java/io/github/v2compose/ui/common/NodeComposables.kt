package io.github.v2compose.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NodeTag(
    nodeId: String,
    nodeName: String,
    onItemClick: ((String, String) -> Unit)? = null
) {
    Text(
        nodeName,
        modifier = Modifier
            .clickable(enabled = onItemClick != null) {
                onItemClick?.invoke(nodeId, nodeName)
            }
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
