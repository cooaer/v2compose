package io.github.v2compose.ui.node

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.core.share
import io.github.v2compose.ui.BaseScreenState
import io.github.v2compose.ui.BaseViewModel
import kotlinx.coroutines.CoroutineScope

@Composable
fun rememberNodeScreenState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): NodeScreenState {
    return remember(context, coroutineScope, snackbarHostState) {
        NodeScreenState(context, coroutineScope, snackbarHostState)
    }
}

@Stable
class NodeScreenState(
    context: Context,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState
):BaseScreenState(context, coroutineScope, snackbarHostState) {

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