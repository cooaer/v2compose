package io.github.v2compose.ui.common

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,
) {
    Divider(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness),
        thickness = thickness,
        color = color
    )
}

@Composable
fun ListDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = DividerDefaults.Thickness,
    color: Color = DividerDefaults.color,) {

    Divider(
        modifier = modifier,
        thickness = Dp.Hairline,
        color = color
    )

}