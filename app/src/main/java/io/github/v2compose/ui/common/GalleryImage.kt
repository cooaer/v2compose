package io.github.v2compose.ui.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transition.CrossfadeTransition
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import io.github.v2compose.LocalImageSaver
import io.github.v2compose.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "GalleryImage"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GalleryImage(
    imageUrl: String,
    onBackgroundClick: () -> Unit,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    var viewWidth by remember { mutableStateOf(0f) }
    var viewHeight by remember { mutableStateOf(0f) }

    var offsetX by rememberSaveable { mutableStateOf(0f) }
    var offsetY by rememberSaveable { mutableStateOf(0f) }

    var scale by rememberSaveable { mutableStateOf(1f) }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = minOf(maxOf(scale * zoomChange, 1f), 3f)

        val maxOffsetX = viewWidth * (scale - 1f) / 2
        val maxOffsetY = viewHeight * (scale - 1f) / 2
        offsetX = maxOf(minOf(offsetX + offsetChange.x, maxOffsetX), -maxOffsetX)
        offsetY = maxOf(minOf(offsetY + offsetChange.y, maxOffsetY), -maxOffsetY)
    }

    val visibleState = remember { MutableTransitionState(0.3f).apply { targetState = 1f } }
    val visibleTransition = updateTransition(transitionState = visibleState, label = "visible")
    val currentAlpha by visibleTransition.animateFloat(
        transitionSpec = { tween(durationMillis = 400) },
        label = "alpha"
    ) { it }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background.copy(alpha = ContentAlpha.medium * currentAlpha))
            .combinedClickable(interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    coroutineScope.launch {
                        visibleState.targetState = 0f
                        delay(400)
                        onBackgroundClick()
                    }
                },
                onDoubleClick = {
                    coroutineScope.launch {
                        val doubleClickAnim = TargetBasedAnimation(
                            animationSpec = tween(400),
                            typeConverter = Float.VectorConverter,
                            initialValue = 0f,
                            targetValue = 1f,
                        )
                        var playTime: Long
                        val startTime = withFrameNanos { it }
                        val startScale = scale
                        val startOffsetX = offsetX
                        val startOffsetY = offsetY

                        var endScale = scale
                        var endOffsetX = offsetX
                        var endOffsetY = offsetY
                        if (scale > 2) {
                            endScale = 1f
                            endOffsetX = 0f
                            endOffsetY = 0f
                        } else {
                            endScale += 1f
                        }

                        do {
                            playTime = withFrameNanos { it } - startTime
                            val progress = doubleClickAnim.getValueFromNanos(playTime)
                            scale = startScale + (endScale - startScale) * progress
                            offsetX = startOffsetX + (endOffsetX - startOffsetX) * progress
                            offsetY = startOffsetY + (endOffsetY - startOffsetY) * progress
                        } while (playTime < 400 * 1000 * 1000)
                    }
                })
            .transformable(transformableState)
    ) {
        LaunchedEffect(maxWidth, maxHeight) {
            viewWidth = with(density) { maxWidth.toPx() }
            viewHeight = with(density) { maxHeight.toPx() }
        }

        AsyncImage(
            model = ImageRequest.Builder(context).data(imageUrl)
                .transitionFactory(CrossfadeTransition.Factory()).build(),
            contentDescription = "current image",
            contentScale = ContentScale.Fit,
            alpha = currentAlpha,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY,
                    alpha = currentAlpha
                ),
        )

        val saveImage = LocalImageSaver.current
        Box(modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .clickable { saveImage(imageUrl) }
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(4.dp),
            )
            .defaultMinSize(minWidth = 72.dp, minHeight = 32.dp)
            .padding(horizontal = 8.dp)) {
            Text(
                stringResource(id = R.string.save_image),
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun LightSystemBarIcons() {
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController) {
        systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = false)
        onDispose {
            systemUiController.setStatusBarColor(color = Color.Transparent, darkIcons = true)
        }
    }
}