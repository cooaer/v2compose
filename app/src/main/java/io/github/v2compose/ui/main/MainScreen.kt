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
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.v2compose.R
import io.github.v2compose.ui.main.home.HomeContent
import io.github.v2compose.ui.main.home.HomeViewModel
import io.github.v2compose.ui.main.mine.MineContent
import io.github.v2compose.ui.main.nodes.NodesContent
import io.github.v2compose.ui.main.nodes.NodesViewModel
import io.github.v2compose.ui.main.notifications.NotificationsContent
import io.github.v2compose.util.L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {
    var navBarSelectedIndex by remember { mutableStateOf(0) }
    val homeViewModel: HomeViewModel = viewModel()
    val nodesViewModel: NodesViewModel = viewModel()

    L.d("MainScreen, homeViewModel = $homeViewModel")

    Scaffold(
        topBar = { MainTopBar(navBarSelectedIndex) },
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
                val saveableStateHolder = rememberSaveableStateHolder()

                saveableStateHolder.SaveableStateProvider(key = navBarSelectedIndex) {
                    when (navBarSelectedIndex) {
                        0 -> HomeContent(viewModel = homeViewModel)
                        1 -> NodesContent(viewModel = nodesViewModel)
                        2 -> NotificationsContent()
                        3 -> MineContent()
                    }
                }
            }
            MainBottomNavigation(navBarSelectedIndex) {
                navBarSelectedIndex = it
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun MainTopBar(currentNavBarIndex: Int) {
    val navBarItemNames = stringArrayResource(R.array.main_navigation_items)
    CenterAlignedTopAppBar(
        title = { Text(navBarItemNames[currentNavBarIndex]) },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "search",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        })
}

@Composable
fun MainContent(navBarSelectedIndex: Int) {
    val saveableStateHolder = rememberSaveableStateHolder()
    val homeViewModel: HomeViewModel = viewModel()
    val nodesViewModel: NodesViewModel = viewModel()
    saveableStateHolder.SaveableStateProvider(key = navBarSelectedIndex) {
        when (navBarSelectedIndex) {
            0 -> HomeContent(viewModel = homeViewModel)
            1 -> NodesContent(viewModel = nodesViewModel)
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
    MainScreen()
}