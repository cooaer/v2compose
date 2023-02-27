package io.github.v2compose.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Polyline
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.v2compose.R
import io.github.v2compose.bean.DraftTopic
import io.github.v2compose.network.bean.CreateTopicPageInfo
import io.github.v2compose.network.bean.TopicNode
import io.github.v2compose.ui.common.CloseButton
import io.github.v2compose.ui.common.HtmlAlertDialog
import io.github.v2compose.ui.common.ListDivider

@Composable
fun WriteTopicScreenRoute(
    onCloseClick: () -> Unit,
    openUri: (String) -> Unit,
    onCreateTopicSuccess: (topicId: String) -> Unit,
    viewModel: WriteTopicViewModel = hiltViewModel(),
    screenState: WriteTopicScreenState = rememberWriteTopicScreenState(),
) {
    val loadNodesState by viewModel.loadNodesState.collectAsStateWithLifecycle()
    val createTopicState by viewModel.createTopicState.collectAsStateWithLifecycle()
    val initialDraftTopic = remember { viewModel.draftTopic }

    HandleLoadNodesState(loadNodesState, screenState)

    HandleCreateTopicState(
        createTopicState = createTopicState,
        screenState = screenState,
        onUriClick = openUri,
        onCreateTopicSuccess = onCreateTopicSuccess
    )

    WriteTopicScreen(
        initialDraftTopic = initialDraftTopic,
        screenState = screenState,
        loadNodesState = loadNodesState,
        createTopicState = createTopicState,
        onCloseClick = onCloseClick,
        onTopicChanged = viewModel::saveDraftTopic,
        onSendClick = viewModel::createTopic,
        retryLoadingNodes = viewModel::loadNodes
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WriteTopicScreen(
    initialDraftTopic: DraftTopic,
    createTopicState: CreateTopicState,
    loadNodesState: LoadNodesState,
    screenState: WriteTopicScreenState,
    onCloseClick: () -> Unit,
    onTopicChanged: (String, String, TopicNode?) -> Unit,
    onSendClick: (title: String, content: String, nodeId: String) -> Unit,
    retryLoadingNodes: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(initialDraftTopic.title) }
    var content by rememberSaveable { mutableStateOf(initialDraftTopic.content) }
    var node by rememberSaveable() { mutableStateOf(initialDraftTopic.node) }

    Scaffold(
        topBar = {
            TopBar(
                createTopicState = createTopicState,
                onCloseClick = onCloseClick,
                onSendClick = {
                    if (screenState.check(title, content, node)) {
                        onSendClick(title, content.trim(), node!!.id)
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars.union(WindowInsets.ime),
        snackbarHost = { SnackbarHost(hostState = screenState.snackbarHostState) }
    ) { insets ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
        ) {
            Column {
                val titleFocusRequester = remember { FocusRequester() }
                val contentFocusRequester = remember { FocusRequester() }

                TopicTitleField(
                    title = title,
                    onTitleChanged = {
                        title = it
                        onTopicChanged(title, content, node)
                    },
                    onNextAction = { contentFocusRequester.requestFocus() },
                    modifier = Modifier.focusRequester(titleFocusRequester)
                )

                ListDivider()

                TopicContentField(
                    content = content,
                    onContentChanged = {
                        content = it
                        onTopicChanged(title, content, node)
                    },
                    modifier = Modifier
                        .focusRequester(contentFocusRequester)
                        .weight(1f)
                )

                TopicNodeField(
                    loadNodesState = loadNodesState,
                    node = node,
                    onNodeSelected = {
                        node = it
                        onTopicChanged(title, content, node)
                    },
                    retryLoadingNodes = retryLoadingNodes
                )

                LaunchedEffect(true) {
                    titleFocusRequester.requestFocus()
                }
            }
        }
    }

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopicTitleField(
    title: String,
    onTitleChanged: (String) -> Unit,
    onNextAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(title, TextRange(title.length)))
    }

    TextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onTitleChanged(it.text)
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 80.dp),
        keyboardActions = KeyboardActions(onNext = { onNextAction() }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        colors = TextFieldDefaults.textFieldColors(
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Transparent,
            placeholderColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        ),
        placeholder = { Text(stringResource(id = R.string.topic_title)) },
        textStyle = MaterialTheme.typography.bodyLarge,
    )

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicContentField(
    content: String,
    onContentChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(content, TextRange(content.length)))
    }

    TextField(
        value = textFieldValue,
        onValueChange = {
            textFieldValue = it
            onContentChanged(it.text)
        },
        modifier = modifier
            .fillMaxWidth(),
        colors = TextFieldDefaults.textFieldColors(
            errorIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            containerColor = Color.Transparent,
            placeholderColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium)
        ),
        placeholder = { Text(stringResource(R.string.topic_content_placeholder)) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 15.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.3.sp,
        ),
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    createTopicState: CreateTopicState,
    onCloseClick: () -> Unit,
    onSendClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.create_topic)) },
        navigationIcon = { CloseButton(onclick = onCloseClick) },
        actions = {
            SendButton(
                inProgress = createTopicState is CreateTopicState.Loading,
                onClick = onSendClick,
            )
        },
    )
}

@Composable
private fun SendButton(
    inProgress: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        if (inProgress) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = LocalContentColor.current,
                strokeWidth = 2.dp
            )
        } else {
            Icon(imageVector = Icons.Rounded.Send, contentDescription = "send")
        }
    }
}


@Composable
private fun HandleLoadNodesState(
    loadNodesState: LoadNodesState,
    screenState: WriteTopicScreenState
) {
    if (loadNodesState is LoadNodesState.Error) {
        LaunchedEffect(loadNodesState) {
            val message = loadNodesState.error?.message
            if (message != null) {
                screenState.showMessage(message)
            } else {
                screenState.showMessage(R.string.load_nodes_failure)
            }
        }
    }
}

@Composable
private fun HandleCreateTopicState(
    createTopicState: CreateTopicState,
    screenState: WriteTopicScreenState,
    onUriClick: (String) -> Unit,
    onCreateTopicSuccess: (topicId: String) -> Unit,
) {
    when (createTopicState) {
        is CreateTopicState.Error -> {
            LaunchedEffect(createTopicState) {
                val message = createTopicState.error?.message
                if (message != null) {
                    screenState.showMessage(message)
                } else {
                    screenState.showMessage(R.string.load_nodes_failure)
                }
            }
        }
        is CreateTopicState.Failure -> {
            val problem: CreateTopicPageInfo.Problem = createTopicState.pageInfo.problem ?: return
            if (problem.isEmpty) return
            HtmlAlertDialog(initialContent = problem.html, onUriClick = onUriClick)
        }
        is CreateTopicState.Success -> {
            LaunchedEffect(createTopicState) {
                onCreateTopicSuccess(createTopicState.topicId)
            }
        }
        else -> {}
    }
}

@Composable
private fun TopicNodeField(
    loadNodesState: LoadNodesState,
    node: TopicNode?,
    onNodeSelected: (TopicNode) -> Unit,
    retryLoadingNodes: () -> Unit,
) {
    var showNodes by remember { mutableStateOf(false) }
    val hasNodes = loadNodesState is LoadNodesState.Success && loadNodesState.data.isNotEmpty()

    if (showNodes && hasNodes) {
        val nodes = (loadNodesState as LoadNodesState.Success).data
        NodesDialog(
            nodes = nodes,
            onNodeClick = {
                showNodes = false
                onNodeSelected(it)
            },
            onDismiss = { showNodes = false },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(40.dp)
                .widthIn(min = 108.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable {
                    showNodes = true
                    if (loadNodesState !is LoadNodesState.Success) {
                        retryLoadingNodes()
                    }
                }
        ) {
            val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            Spacer(Modifier.width(10.dp))
            if (loadNodesState is LoadNodesState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Rounded.Polyline, contentDescription = "node", tint = contentColor)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if(node?.name.isNullOrEmpty()) stringResource(R.string.select_node) else node?.name!!,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(10.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NodesDialog(
    nodes: List<TopicNode>,
    onNodeClick: (TopicNode) -> Unit,
    onDismiss: () -> Unit,
) {
    var searchKey by remember { mutableStateOf("") }
    var currentNodes by remember(searchKey) { mutableStateOf(nodes) }

    LaunchedEffect(nodes, searchKey) {
        currentNodes = if (searchKey.isEmpty()) nodes else {
            nodes.filter { node ->
                node.name.contains(searchKey, true)
                        || node.id.contains(searchKey, true)
                        || node.aliases.any { it.contains(searchKey, true) }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 32.dp)
                .imePadding()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(vertical = 16.dp)
        ) {
            Column {
                val focusRequester = remember { FocusRequester() }
                TextField(
                    value = searchKey,
                    onValueChange = { searchKey = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {}),
//                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    placeholder = { Text(text = stringResource(id = R.string.search_all_nodes)) },
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn {
                    itemsIndexed(items = currentNodes, key = { _, item -> item.id }) { _, item ->
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

@Composable
private fun NodeListItem(
    item: TopicNode,
    onNodeClick: (TopicNode) -> Unit
) {
    Text(
        "${item.name} / ${item.id}",
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