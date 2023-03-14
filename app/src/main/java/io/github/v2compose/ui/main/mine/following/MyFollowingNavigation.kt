package io.github.v2compose.ui.main.mine.following

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.github.v2compose.network.bean.MyFollowingInfo

const val myFollowingRoute = "/my/following"

fun NavController.navigateToMyFollowing() {
    navigate(myFollowingRoute)
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.myFollowingScreen(
    onBackClick: () -> Unit,
    onTopicClick: (MyFollowingInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
) {
    composable(myFollowingRoute) {
        MyFollowingScreenRoute(
            onBackClick = onBackClick,
            onTopicClick = onTopicClick,
            onNodeClick = onNodeClick,
            onUserAvatarClick = onUserAvatarClick,
        )
    }
}