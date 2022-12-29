package io.github.v2compose.ui.main.mine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MineScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "mine", modifier = Modifier.align(Alignment.Center))
    }
}