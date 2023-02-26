package io.github.v2compose.ui.main.mine

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.LocalSnackbarHostState
import io.github.v2compose.R
import io.github.v2compose.bean.Account
import io.github.v2compose.ui.BaseScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun rememberMineContentState(
    context: Context = LocalContext.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current
): MineContentState {
    return remember(context, coroutineScope, snackbarHostState) {
        MineContentState(context, coroutineScope, snackbarHostState)
    }
}

class MineContentState(
    context: Context,
    coroutineScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
) : BaseScreenState(context, coroutineScope, snackbarHostState) {

    fun notImplemented() {
        coroutineScope.launch {
            val message = context.getString(R.string.function_not_implemented)
            snackbarHostState.showSnackbar(message = message)
        }
    }

    fun doActionIfLoggedIn(account: Account, action:() -> Unit){
        if (account.isValid()) {
            action()
        } else {
            showMessage(R.string.login_first)
        }
    }

}