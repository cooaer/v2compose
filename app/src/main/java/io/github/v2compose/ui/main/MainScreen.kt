package io.github.v2compose.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringArrayResource
import io.github.v2compose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navBarItemNames = stringArrayResource(R.array.main_navigation_items)
    var navBarSelectedIndex by remember { mutableStateOf(0) }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(navBarItemNames[navBarSelectedIndex]) },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Rounded.Search, contentDescription = "search")
                }
            })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.weight(1f, fill = true)
            ) {
                MainContent()
            }
            MainBottomNavigation(navBarSelectedIndex) {
                navBarSelectedIndex = it
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Content")
    }
}

@Composable
fun MainBottomNavigation(selectedIndex: Int, onItemSelected: (Int) -> Unit){
    val itemNames = stringArrayResource(R.array.main_navigation_items)
    val itemIcons: List<ImageVector> = listOf(
        Icons.Rounded.Home,
        Icons.Rounded.ViewList,
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