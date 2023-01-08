package io.github.v2compose.ui.main.mine

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.cooaer.htmltext.HtmlText
import io.github.cooaer.htmltext.TEST_HTML_TEXT
import io.github.cooaer.htmltext.TEST_HTML_TEXT_2

@Composable
fun MineContent() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        HtmlText(
            html = TEST_HTML_TEXT + TEST_HTML_TEXT_2,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}