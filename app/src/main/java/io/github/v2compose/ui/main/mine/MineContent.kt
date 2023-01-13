package io.github.v2compose.ui.main.mine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.cooaer.htmltext.HtmlText

@Composable
fun MineContent() {
    Box(modifier = Modifier.fillMaxSize()) {
        HtmlText(
            html = TEST_HTML_TEXT + TEST_HTML_TEXT_2,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        )
    }
}