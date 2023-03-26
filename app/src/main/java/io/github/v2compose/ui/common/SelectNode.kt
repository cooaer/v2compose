package io.github.v2compose.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.v2compose.R
import io.github.v2compose.network.bean.TopicNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectNode(
    nodes: List<TopicNode>,
    onNodeClick: (TopicNode) -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    var searchKey by remember { mutableStateOf("") }
    var currentNodes by remember(searchKey) { mutableStateOf(nodes) }

    LaunchedEffect(nodes, searchKey) {
        currentNodes = if (searchKey.isEmpty()) nodes else {
            nodes.filter { node ->
                node.title.contains(searchKey, true) || node.name.contains(
                    searchKey, true
                ) || node.aliases.any { it.contains(searchKey, true) }
            }
        }
    }

    Surface(
        color = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.surface),
    ) {
        Box(modifier = Modifier
            .clickable { onDismiss() }
            .background(color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = ContentAlpha.medium))
            .systemBarsPadding()
            .imePadding()
            .padding(32.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 16.dp)
            ) {
                Column {
                    Box {
                        TextField(
                            value = searchKey,
                            onValueChange = { searchKey = it },
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            placeholder = { Text(text = stringResource(id = R.string.search_all_nodes)) },
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                        )
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 16.dp)
                        ) {
                            Icon(Icons.Outlined.Close, contentDescription = "close")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        itemsIndexed(
                            items = currentNodes,
                            key = { _, item -> item.name }) { _, item ->
                            NodeListItem(item, onNodeClick)
                        }
                    }

                    LaunchedEffect(true) {
                        focusRequester.requestFocus()
                    }
                }
            }
        }
    }
}

@Composable
private fun NodeListItem(
    item: TopicNode, onNodeClick: (TopicNode) -> Unit
) {
    Text(
        "${item.title} / ${item.name}",
        modifier = Modifier
            .clickable { onNodeClick(item) }
            .fillMaxWidth()
            .height(40.dp)
            .padding(horizontal = 16.dp)
            .wrapContentHeight(align = Alignment.CenterVertically),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}