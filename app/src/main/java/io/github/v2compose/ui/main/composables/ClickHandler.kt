package io.github.v2compose.ui.main.composables

import androidx.compose.runtime.*

@Composable
fun ClickHandler(enabled: Boolean, onClick: () -> Unit) {
    val currentOnClick by rememberUpdatedState(newValue = onClick)
    val clickCallback = remember {
        object : ClickDispatcher.OnClickCallback(enabled) {
            override fun handleClick() {
                currentOnClick()
            }
        }
    }
    SideEffect {
        clickCallback.isEnabled = enabled
    }
    val clickDispatcher = checkNotNull(LocalClickDispatcher.current) {
        "No ClickDispatcher was provided via LocalClickDispatcherOwner"
    }
    DisposableEffect(clickDispatcher) {
        clickDispatcher.addCallback(clickCallback)
        onDispose {
            clickDispatcher.removeCallback(clickCallback)
        }
    }
}

val LocalClickDispatcher = compositionLocalOf<ClickDispatcher?> { null }

class ClickDispatcher {

    private val callbacks = mutableSetOf<OnClickCallback>()

    fun addCallback(callback: OnClickCallback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: OnClickCallback) {
        callbacks.remove(callback)
    }

    fun dispatch() {
        callbacks.forEach { if (it.isEnabled) it.handleClick() }
    }

    abstract class OnClickCallback(var isEnabled: Boolean) {
        abstract fun handleClick()
    }

}

