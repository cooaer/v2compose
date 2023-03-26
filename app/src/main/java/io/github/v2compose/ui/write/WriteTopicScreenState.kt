package io.github.v2compose.ui.write

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.R
import io.github.v2compose.network.bean.TopicNode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberWriteTopicScreenState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): WriteTopicScreenState {
    return remember(context, coroutineScope, snackbarHostState) {
        WriteTopicScreenState(context, coroutineScope, snackbarHostState)
    }
}

@Stable
class WriteTopicScreenState(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    val snackbarHostState: SnackbarHostState
) {

    fun check(title: String, content: String, node: TopicNode?): Boolean {
        if (title.isEmpty()) {
            showMessage(R.string.topic_title_empty)
            return false
        }
        if (node?.name.isNullOrEmpty()) {
            showMessage(R.string.node_empty)
            return false
        }
        return true
    }

    fun showMessage(@StringRes messageResId: Int) {
        showMessage(context.getString(messageResId))
    }

    fun showMessage(message: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = message, duration = SnackbarDuration.Short)
        }
    }


}