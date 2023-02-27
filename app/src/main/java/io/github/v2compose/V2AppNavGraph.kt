package io.github.v2compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.google.accompanist.navigation.animation.AnimatedNavHost
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
import io.github.v2compose.ui.settings.settingsScreenRoute
import io.github.v2compose.ui.supplement.addSupplementScreen
import io.github.v2compose.ui.supplement.navigateToAddSupplement
import io.github.v2compose.ui.topic.navigateToTopic
import io.github.v2compose.ui.topic.topicScreen
import io.github.v2compose.ui.user.navigateToUser
import io.github.v2compose.ui.user.userScreen
import io.github.v2compose.ui.webview.navigateToWebView
import io.github.v2compose.ui.webview.webViewScreen
import io.github.v2compose.ui.write.navigateToWriteTopic
import io.github.v2compose.ui.write.writeTopicScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun V2AppNavGraph(
    navController: NavHostController,
    appState: V2AppState,
    viewModel: V2AppViewModel,
) {
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
            onMyNodesClick = { navController.navigateToWebView(V2exUri.myNodesUrl) },
            onMyTopicsClick = { navController.navigateToWebView(V2exUri.myTopicsUrl) },
            onMyFollowingClick = { navController.navigateToWebView(V2exUri.myFollowingUrl) },
            onCreateTopicClick = navController::navigateToWriteTopic,
            onSettingsClick = navController::navigateToSettings,
            openUri = appState::openUri,
        )
        topicScreen(
            onBackClick = appState::back,
            onNodeClick = navController::navigateToNode,
            onUserAvatarClick = navController::navigateToUser,
            openUri = appState::openUri,
            onAddSupplementClick = navController::navigateToAddSupplement
        )
        nodeScreen(
            onBackClick = appState::back,
            onTopicClick = { item -> navController.navigateToTopic(item.topicId) },
            onUserAvatarClick = navController::navigateToUser,
            openUri = appState::openUri
        )
        searchScreen(
            goBack = appState::back,
            onTopicClick = { item -> navController.navigateToTopic(item.source.id) },
        )
        userScreen(
            onBackClick = appState::back,
            onTopicClick = appState::openUri,
            onNodeClick = { nodePath, _ -> appState.openUri(nodePath) },
            openUri = appState::openUri
        )
        settingsScreen(
            onBackClick = appState::back,
            openUri = appState::openUri,
            onLogoutSuccess = {
                navController.navigateToLogin(navOptions = navOptions {
                    popUpTo(settingsScreenRoute) {
                        inclusive = true
                    }
                })
            }
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
        webViewScreen(
            onCloseClick = appState::back,
            openUri = appState::openUri
        )
        writeTopicScreen(
            onCloseClick = appState::back,
            openUri = appState::openUri,
            onCreateTopicSuccess = {
                navController.popBackStack()
                navController.navigateToTopic(it)
            },
        )
        addSupplementScreen(
            onCloseClick = appState::back,
            onAddSupplementSuccess = {
                navController.navigateToTopic(it, navOptions {
                    popUpTo(V2exUri.topicPath(it)) {
                        inclusive = true
                    }
                })
            },
            openUri = appState::openUri,
        )
    }
}