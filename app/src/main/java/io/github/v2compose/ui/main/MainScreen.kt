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
import io.github.v2compose.R
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.ui.main.home.HomeContent
import io.github.v2compose.ui.main.mine.MineContent
import io.github.v2compose.ui.main.nodes.NodesContent
import io.github.v2compose.ui.main.notifications.NotificationsContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNewsItemClick: (NewsInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                MainContent(
                    navBarSelectedIndex = navBarSelectedIndex,
                    onNewsItemClick = onNewsItemClick,
                    onNodeClick = onNodeClick,
                    onUserAvatarClick = onUserAvatarClick,
                )
            }
            MainBottomNavigation(navBarSelectedIndex) {
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
) {
    val saveableStateHolder = rememberSaveableStateHolder()
    saveableStateHolder.SaveableStateProvider(key = navBarSelectedIndex) {
        when (navBarSelectedIndex) {
            0 -> HomeContent(
                onNewsItemClick = onNewsItemClick,
                onNodeClick = onNodeClick,
                onUserAvatarClick = onUserAvatarClick,
            )
            1 -> NodesContent(onNodeClick = onNodeClick)
            2 -> NotificationsContent()
            3 -> MineContent()
        }
    }
}

@Composable
fun MainBottomNavigation(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val itemNames = stringArrayResource(R.array.main_navigation_items)
    val itemIcons: List<ImageVector> = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.List,
        Icons.Outlined.Notifications,
        Icons.Outlined.Person
    )
    NavigationBar {
        itemNames.forEachIndexed { index, name ->
            NavigationBarItem(icon = { Icon(itemIcons[index], contentDescription = null) },
                label = { Text(name) },
                selected = index == selectedIndex,
                onClick = { onItemSelected(index) })
        }
    }
}

@Preview(showBackground = true, widthDp = 440, heightDp = 880)
@Composable
fun MainScreenPreview() {
    MainScreen(
        onNewsItemClick = {},
        onNodeClick = { _, _ -> },
        onUserAvatarClick = {_,_ ->},
        onSearchClick = {},
        onSettingsClick = {},)
}