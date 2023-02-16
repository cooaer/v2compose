package io.github.v2compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import io.github.v2compose.datasource.AppSettings
import io.github.v2compose.ui.login.google.googleLoginScreen
import io.github.v2compose.ui.login.google.navigateToGoogleLogin
import io.github.v2compose.ui.login.loginScreen
import io.github.v2compose.ui.login.navigateToLogin
import io.github.v2compose.ui.login.twostep.twoStepLoginScreen
import io.github.v2compose.ui.main.mainNavigationRoute
import io.github.v2compose.ui.main.mainScreen
import io.github.v2compose.ui.main.navigateToMain
import io.github.v2compose.ui.node.navigateToNode
import io.github.v2compose.ui.node.nodeScreen
import io.github.v2compose.ui.search.navigateToSearch
import io.github.v2compose.ui.search.searchScreen
import io.github.v2compose.ui.settings.navigateToSettings
import io.github.v2compose.ui.settings.settingsScreen
import io.github.v2compose.ui.topic.navigateToTopic
import io.github.v2compose.ui.topic.topicScreen
import io.github.v2compose.ui.user.navigateToUser
import io.github.v2compose.ui.user.userScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun V2AppNavGraph(
    navController: NavHostController,
    appState: V2AppState,
    appSettings: AppSettings,
    viewModel: V2AppViewModel,
) {
    val openUri = fun(uri: String) { appState.openUri(uri, appSettings.openInInternalBrowser) }
    val account by viewModel.account.collectAsStateWithLifecycle()

    AnimatedNavHost(navController = navController, startDestination = mainNavigationRoute) {
        mainScreen(
            onNewsItemClick = { navController.navigateToTopic(it.id) },
            onNodeClick = navController::navigateToNode,
            onUserAvatarClick = navController::navigateToUser,
            onSearchClick = navController::navigateToSearch,
            onLoginClick = navController::navigateToLogin,
            onMyHomePageClick = {
                if (account.isValid()) {
                    navController.navigateToUser(
                        userName = account.userName,
                        userAvatar = account.userAvatar
                    )
                }
            },
            onMyNodesClick = {},
            onMyTopicsClick = {},
            onMyFollowingClick = {},
            onCreateTopicClick = {},
            onSettingsClick = navController::navigateToSettings,
            openUri = openUri,
        )
        topicScreen(
            onBackClick = appState::back,
            onNodeClick = navController::navigateToNode,
            onUserAvatarClick = navController::navigateToUser,
            openUri = openUri
        )
        nodeScreen(
            onBackClick = appState::back,
            onTopicClick = { item -> navController.navigateToTopic(item.topicId) },
            onUserAvatarClick = navController::navigateToUser,
            openUri = openUri
        )
        searchScreen(
            goBack = appState::back,
            onTopicClick = { item -> navController.navigateToTopic(item.source.id) },
        )
        userScreen(
            onBackClick = appState::back,
            onTopicClick = openUri,
            onNodeClick = { nodePath, _ -> openUri(nodePath) },
            openUri = openUri
        )
        settingsScreen(
            onBackClick = appState::back,
            openUri = openUri,
        )
        loginScreen(
            onCloseClick = appState::back,
            onSignInWithGoogleClick = navController::navigateToGoogleLogin,
        )
        twoStepLoginScreen(
            onCloseClick = appState::back,
        )
        googleLoginScreen(
            onCloseClick = appState::back,
            onLoginSuccess = navController::navigateToMain
        )
    }
}