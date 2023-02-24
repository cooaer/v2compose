package io.github.v2compose.ui.node

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.core.share

@Composable
fun rememberNodeScreenState(context: Context = LocalContext.current): NodeScreenState {
    return remember(context) {
        NodeScreenState(context)
    }
}

@Stable
class NodeScreenState(private val context: Context) {

    fun share(nodeArgs: NodeArgs, nodeUiState: NodeUiState) {
        val title = if (nodeUiState is NodeUiState.Success) {
            "V2EX > " + nodeUiState.nodeInfo.name + "\n" + nodeUiState.nodeInfo.title
        } else {
            "V2EX > " + (nodeArgs.nodeName ?: "")
        }
        val url = "https://www.v2ex.com/g/${nodeArgs.nodeId}"
        context.share(title = title, url = url)
    }

}