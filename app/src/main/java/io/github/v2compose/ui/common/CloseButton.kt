package io.github.v2compose.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun CloseButton(onclick: () -> Unit) {
    IconButton(onClick = onclick) {
        Icon(Icons.Rounded.Close, "close")
    }
}