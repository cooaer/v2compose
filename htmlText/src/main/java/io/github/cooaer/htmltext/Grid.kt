package io.github.cooaer.htmltext

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

class GridRowScope(private val columnCount: Int) {
    private val contents = mutableListOf<@androidx.compose.runtime.Composable () -> Unit>()

    fun item(content: @Composable () -> Unit) {
        contents.add(content)
    }

    fun <T> items(values: List<T>, content: @Composable (T) -> Unit) {
        for (value in values) {
            item {
                content(value)
            }
        }
    }

    fun <T> items(values: Array<T>, content: @Composable (T) -> Unit) {
        for (value in values) {
            item {
                content(value)
            }
        }
    }

    fun <T> itemsIndexed(values: List<T>, content: @Composable (Int, T) -> Unit) {
        for (index in 0..values.lastIndex) {
            item {
                content(index, values[index])
            }
        }
    }

    fun <T> itemsIndexed(values: Array<T>, content: @Composable (Int, T) -> Unit) {
        for (index in 0..values.lastIndex) {
            item {
                content(index, values[index])
            }
        }
    }

    @Composable
    internal fun Compose() {
        val lineColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
        val rowCount = contents.size / columnCount
        Column(
            modifier = Modifier.drawBehind {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
                val columnWidth = size.width / columnCount
                for (index in (0..columnCount)) {
                    val startX = columnWidth * index
                    drawLine(
                        color = lineColor,
                        start = Offset(startX, 0f),
                        end = Offset(startX, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            },
        ) {
            for (rowIndex in 0 until rowCount) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = lineColor,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        },
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (columnIndex in 0 until columnCount) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        ) {
                            contents[rowIndex * columnCount + columnIndex]()
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun Grid(
    columnCount: Int,
    modifier: Modifier = Modifier,
    content: GridRowScope.() -> Unit
) {
    val scope = GridRowScope(columnCount)
    scope.content()

    Column(modifier = modifier) {
        scope.Compose()
    }
}
