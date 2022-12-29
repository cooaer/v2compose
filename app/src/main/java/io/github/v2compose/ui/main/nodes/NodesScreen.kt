package io.github.v2compose.ui.main.nodes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NodesScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "nodes", modifier = Modifier.align(Alignment.Center))
    }
}