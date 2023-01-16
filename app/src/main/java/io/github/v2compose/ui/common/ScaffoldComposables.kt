package io.github.v2compose.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@Composable
fun BackIcon(onBackClick: () -> Unit) {
    IconButton(onClick = onBackClick) {
        Icon(
            Icons.Rounded.ArrowBack,
            contentDescription = "back"
        )
    }
}
