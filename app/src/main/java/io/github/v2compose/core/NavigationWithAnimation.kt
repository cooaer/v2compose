package io.github.v2compose.core

import androidx.annotation.AnimRes
import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.*
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.R

fun NavController.navigateWithAnimation(
    route: String,
    @AnimRes enterAnim: Int = R.anim.slide_in_right,
    @AnimRes exitAnim: Int = R.anim.slide_out_left,
    @AnimRes popEnterAnim: Int = android.R.anim.slide_in_left,
    @AnimRes popExitAnim: Int = android.R.anim.slide_out_right,
) {
    navigate(
        route,
        NavOptions.Builder().apply {
            setEnterAnim(enterAnim)
            setExitAnim(exitAnim)
            setPopEnterAnim(popEnterAnim)
            setPopExitAnim(popExitAnim)
        }.build(),
    )
}

@OptIn(ExperimentalAnimationApi::class)
public fun NavGraphBuilder.composableWithAnimation(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { slideInHorizontally { it } },
    exitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { slideOutHorizontally { -it } },
    popEnterTransition: (AnimatedContentScope<NavBackStackEntry>.() -> EnterTransition?)? = { slideInHorizontally { -it } },
    popExitTransition: (AnimatedContentScope<NavBackStackEntry>.() -> ExitTransition?)? = { slideOutHorizontally { it } },
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )
}