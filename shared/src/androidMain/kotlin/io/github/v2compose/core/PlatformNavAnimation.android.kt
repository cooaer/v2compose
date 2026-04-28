package io.github.v2compose.core

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

actual fun navEnterTransition(): EnterTransition? = slideInHorizontally { it }
actual fun navExitTransition(): ExitTransition? = slideOutHorizontally { -it }
actual fun navPopEnterTransition(): EnterTransition? = slideInHorizontally { -it }
actual fun navPopExitTransition(): ExitTransition? = slideOutHorizontally { it }
