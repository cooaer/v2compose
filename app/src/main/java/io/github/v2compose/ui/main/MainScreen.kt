package io.github.v2compose.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import io.github.v2compose.R
import io.github.v2compose.ui.main.home.HomeScreen
import io.github.v2compose.ui.main.mine.MineScreen
import io.github.v2compose.ui.main.nodes.NodesScreen
import io.github.v2compose.ui.main.notifications.NotificationsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var navBarSelectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            val navBarItemNames = stringArrayResource(R.array.main_navigation_items)
            CenterAlignedTopAppBar(
                title = { Text(navBarItemNames[navBarSelectedIndex]) },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Rounded.Search, contentDescription = "search")
                    }
                })
        }, contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                MainContent(navBarSelectedIndex)
            }
            MainBottomNavigation(navBarSelectedIndex) {
                navBarSelectedIndex = it
            }
        }
    }
}

@Composable
fun MainContent(navBarSelectedIndex: Int) {
    when (navBarSelectedIndex) {
        0 -> HomeScreen()
        1 -> NodesScreen()
        2 -> NotificationsScreen()
        3 -> MineScreen()
    }
}

@Composable
fun MainBottomNavigation(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val itemNames = stringArrayResource(R.array.main_navigation_items)
    val itemIcons: List<ImageVector> = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.List,
        Icons.Rounded.Notifications,
        Icons.Rounded.Person
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