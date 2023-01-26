package io.github.cooaer.htmltext

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

private const val TAG = "VideoPlayer"

@Composable
fun YouTubePlayer(videoId: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var startSeconds by rememberSaveable { mutableStateOf(0f) }

    Log.d(TAG, "YouTubePlayer, videoId = $videoId")

    val youtubePlayer = remember(context, videoId) {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
            initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoId, startSeconds)
                }

                override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                    startSeconds = second
                }

                override fun onStateChange(
                    youTubePlayer: YouTubePlayer,
                    state: PlayerConstants.PlayerState
                ) {
                    Log.d(TAG, "YouTubePlayer, videoId = $videoId, state = $state")
                }
            })
        }
    }

    DisposableEffect(lifecycleOwner, youtubePlayer) {
        lifecycleOwner.lifecycle.addObserver(youtubePlayer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(youtubePlayer)
        }
    }

    AndroidView(
        factory = { youtubePlayer },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    )
}

fun String.parseYouTubeVideoId(): String? {
    return toUri().pathSegments.let {
        if (it.getOrNull(0)?.lowercase() == "embed") it.getOrNull(1) else null
    }
}