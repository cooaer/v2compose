package io.github.v2compose.core

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

expect fun navEnterTransition(): EnterTransition?
expect fun navExitTransition(): ExitTransition?
expect fun navPopEnterTransition(): EnterTransition?
expect fun navPopExitTransition(): ExitTransition?
