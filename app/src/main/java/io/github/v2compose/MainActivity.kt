package io.github.v2compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.v2compose.network.V2exService
import io.github.v2compose.ui.main.MainScreen
import io.github.v2compose.ui.theme.V2composeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            V2composeTheme {
                MainScreen()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 440, heightDp = 880)
@Composable
fun DefaultPreview() {
    V2composeTheme {
        MainScreen()
    }
}