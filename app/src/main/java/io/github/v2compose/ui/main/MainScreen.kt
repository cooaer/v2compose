package io.github.v2compose.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.v2compose.R
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.ui.HandleSnackbarMessage
import io.github.v2compose.ui.common.NewReleaseDialog
import io.github.v2compose.ui.common.OnHtmlImageClick
import io.github.v2compose.ui.main.home.HomeContent
import io.github.v2compose.ui.main.mine.MineContent
import io.github.v2compose.ui.main.nodes.NodesContent
import io.github.v2compose.ui.main.notifications.NotificationsContent

@Composable
fun MainScreenRoute(
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onSearchClick: () -> Unit,
    onLoginClick: () -> Unit,
    onMyHomePageClick: () -> Unit,
    onCreateTopicClick: () -> Unit,
    onMyNodesClick: () -> Unit,
    onMyTopicsClick: () -> Unit,
    onMyFollowingClick: () -> Unit,
    onSettingsClick: () -> Unit,
    openUri: (String) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
    viewModel: MainViewModel = hiltViewModel(),
    screenState: MainScreenState = rememberMainScreenState()
) {
    val unreadNotifications by viewModel.unreadNotifications.collectAsStateWithLifecycle()

    HandleSnackbarMessage(viewModel, screenState)

    val newRelease by viewModel.newRelease.collectAsStateWithLifecycle()
    if (newRelease.isValid()) {
        NewReleaseDialog(
            release = newRelease,
            onIgnoreClick = {
                viewModel.ignoreRelease(newRelease)
                viewModel.resetNewRelease()
            },
            onCancelClick = viewModel::resetNewRelease,
            onOkClick = {
                openUri(newRelease.htmlUrl)
                viewModel.resetNewRelease()
            },
        )
    }

    MainScreen(
        unreadNotifications = unreadNotifications,
        onSearchClick = onSearchClick,
        onSettingsClick = onSettingsClick,
        onNewsItemClick = onNewsItemClick,
        onNodeClick = onNodeClick,
        onUserAvatarClick = onUserAvatarClick,
        onLoginClick = onLoginClick,
        onMyHomePageClick = onMyHomePageClick,
        onCreateTopicClick = onCreateTopicClick,
        onMyNodesClick = onMyNodesClick,
        onMyTopicsClick = onMyTopicsClick,
        onMyFollowingClick = onMyFollowingClick,
        onUriClick = openUri,
        onHtmlImageClick = onHtmlImageClick,
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainScreen(
    unreadNotifications: Int,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onLoginClick: () -> Unit,
    onMyHomePageClick: () -> Unit,
    onCreateTopicClick: () -> Unit,
    onMyNodesClick: () -> Unit,
    onMyTopicsClick: () -> Unit,
    onMyFollowingClick: () -> Unit,
    onUriClick: (String) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    var navBarSelectedIndex by rememberSaveable(stateSaver = autoSaver()) { mutableStateOf(0) }

    Scaffold(
        topBar = {
            MainTopBar(
                currentNavBarIndex = navBarSelectedIndex,
                onMenuItemClick = {
                    when (it) {
                        MenuItem.search -> onSearchClick()
                        MenuItem.settings -> onSettingsClick()
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets(bottom = 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                MainContent(
                    navBarSelectedIndex = navBarSelectedIndex,
                    onNewsItemClick = onNewsItemClick,
                    onNodeClick = onNodeClick,
                    onUserAvatarClick = onUserAvatarClick,
                    onLoginClick = onLoginClick,
                    onMyHomePageClick = onMyHomePageClick,
                    onCreateTopicClick = onCreateTopicClick,
                    onMyNodesClick = onMyNodesClick,
                    onMyTopicsClick = onMyTopicsClick,
                    onMyFollowingClick = onMyFollowingClick,
                    onSettingsClick = onSettingsClick,
                    onUriClick = onUriClick,
                    onHtmlImageClick = onHtmlImageClick,
                )
            }
            MainBottomNavigation(navBarSelectedIndex, unreadNotifications) {
                navBarSelectedIndex = it
            }
        }
    }
}

private enum class MenuItem(val imageVector: ImageVector) {
    search(Icons.Rounded.Search), settings(Icons.Rounded.Settings)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainTopBar(currentNavBarIndex: Int, onMenuItemClick: (MenuItem) -> Unit) {
    val navBarItemNames = stringArrayResource(R.array.main_navigation_items)
    val menuItem = remember(currentNavBarIndex) {
        when (currentNavBarIndex) {
            3 -> MenuItem.settings
            else -> MenuItem.search
        }
    }
    CenterAlignedTopAppBar(
        title = { Text(navBarItemNames[currentNavBarIndex]) },
        actions = {
            IconButton(onClick = { onMenuItemClick(menuItem) }) {
                Icon(
                    menuItem.imageVector,
                    contentDescription = menuItem.name,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        })
}

@Composable
fun MainContent(
    navBarSelectedIndex: Int,
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onLoginClick: () -> Unit,
    onMyHomePageClick: () -> Unit,
    onCreateTopicClick: () -> Unit,
    onMyNodesClick: () -> Unit,
    onMyTopicsClick: () -> Unit,
    onMyFollowingClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onUriClick: (String) -> Unit,
    onHtmlImageClick: OnHtmlImageClick,
) {
    rememberSaveableStateHolder().SaveableStateProvider(key = navBarSelectedIndex) {
        when (navBarSelectedIndex) {
            0 -> HomeContent(
                onNewsItemClick = onNewsItemClick,
                onNodeClick = onNodeClick,
                onUserAvatarClick = onUserAvatarClick,
            )
            1 -> NodesContent(onNodeClick = onNodeClick)
            2 -> NotificationsContent(
                onLoginClick = onLoginClick,
                onUriClick = onUriClick,
                onUserAvatarClick = onUserAvatarClick,
                onHtmlImageClick = onHtmlImageClick,
            )
            3 -> MineContent(
                onLoginClick = onLoginClick,
                onMyHomePageClick = onMyHomePageClick,
                onCreateTopicClick = onCreateTopicClick,
                onMyNodesClick = onMyNodesClick,
                onMyTopicsClick = onMyTopicsClick,
                onMyFollowingClick = onMyFollowingClick,
                onSettingsClick = onSettingsClick,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainBottomNavigation(
    selectedIndex: Int,
    unreadNotifications: Int,
    onItemSelected: (Int) -> Unit
) {
    val itemNames = stringArrayResource(R.array.main_navigation_items)
    val itemIcons: List<ImageVector> = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.List,
        Icons.Outlined.Notifications,
        Icons.Outlined.Person
    )
    NavigationBar {
        itemNames.forEachIndexed { index, name ->
            NavigationBarItem(icon = {
                if (index == 2 && unreadNotifications > 0) {
                    BadgedBox(badge = { Badge { Text(unreadNotifications.toString()) } }) {
                        Icon(itemIcons[index], contentDescription = name)
                    }
                } else {
                    Icon(itemIcons[index], contentDescription = name)
                }
            },
                label = { Text(name) },
                selected = index == selectedIndex,
                onClick = { onItemSelected(index) })
        }
    }
}