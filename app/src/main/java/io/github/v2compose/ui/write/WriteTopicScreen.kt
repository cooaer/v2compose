package io.github.v2compose.ui.write

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.v2compose.R
import io.github.v2compose.bean.ContentFormat
import io.github.v2compose.bean.DraftTopic
import io.github.v2compose.network.bean.CreateTopicPageInfo
import io.github.v2compose.network.bean.TopicNode
import io.github.v2compose.ui.common.*
import io.github.v2compose.usecase.LoadNodesState

@Composable
fun WriteTopicScreenRoute(
    onCloseClick: () -> Unit,
    openUri: (String) -> Unit,
    onCreateTopicSuccess: (topicId: String) -> Unit,
    viewModel: WriteTopicViewModel = hiltViewModel(),
    screenState: WriteTopicScreenState = rememberWriteTopicScreenState(),
) {
    val loadNodesState by viewModel.loadNodes.state.collectAsStateWithLifecycle()
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
        loadNodesState = loadNodesState,
        createTopicState = createTopicState,
        snackbarHostState = screenState.snackbarHostState,
        onCloseClick = onCloseClick,
        onTopicChanged = viewModel::saveDraftTopic,
        onSendClick = { title, content, contentFormat, node ->
            if (screenState.check(title, content, node)) {
                viewModel.createTopic(title, content.trim(), contentFormat, node!!.name)
            }
        },
        retryLoadingNodes = viewModel::loadNodes,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WriteTopicScreen(
    initialDraftTopic: DraftTopic,
    createTopicState: CreateTopicState,
    loadNodesState: LoadNodesState,
    snackbarHostState: SnackbarHostState,
    onCloseClick: () -> Unit,
    onTopicChanged: (String, String, ContentFormat, TopicNode?) -> Unit,
    onSendClick: (title: String, content: String, contentFormat: ContentFormat, node: TopicNode?) -> Unit,
    retryLoadingNodes: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(initialDraftTopic.title) }
    var content by rememberSaveable { mutableStateOf(initialDraftTopic.content) }
    var contentFormat by rememberSaveable { mutableStateOf(initialDraftTopic.contentFormat) }
    var node by rememberSaveable() { mutableStateOf(initialDraftTopic.node) }

    var showNodes by remember { mutableStateOf(false) }
    val hasNodes = loadNodesState is LoadNodesState.Success && loadNodesState.data.isNotEmpty()

    BackHandler(enabled = showNodes) {
        showNodes = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopBar(
                    createTopicState = createTopicState,
                    onCloseClick = onCloseClick,
                    onSendClick = { onSendClick(title, content, contentFormat, node) },
                )
            },
            contentWindowInsets = WindowInsets.systemBars.union(WindowInsets.ime),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { insets ->
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
                            onTopicChanged(title, content, contentFormat, node)
                        },
                        onNextAction = { contentFocusRequester.requestFocus() },
                        modifier = Modifier.focusRequester(titleFocusRequester),
                    )

                    ListDivider()

                    Box(modifier = Modifier.weight(1f)) {
                        TextEditor(
                            content = content,
                            placeholder = stringResource(R.string.topic_content_placeholder),
                            contentFormat = contentFormat,
                            onContentChanged = {
                                content = it
                                onTopicChanged(title, content, contentFormat, node)
                            },
                            onContentFormatChanged = {
                                contentFormat = it
                                onTopicChanged(title, content, contentFormat, node)
                            },
                            contentFocusRequester = contentFocusRequester,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 40.dp),
                        )

                        TopicNodeField(
                            loadNodesState = loadNodesState,
                            node = node,
                            onNodeClick = {
                                showNodes = true
                                if (loadNodesState !is LoadNodesState.Success) {
                                    retryLoadingNodes()
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }

                    LaunchedEffect(showNodes) {
                        if (!showNodes) {
                            titleFocusRequester.requestFocus()
                        }
                    }
                }
            }
        }

        if (showNodes && hasNodes) {
            val nodes = (loadNodesState as LoadNodesState.Success).data
            SelectNode(
                nodes = nodes,
                onNodeClick = {
                    showNodes = false
                    node = it
                    onTopicChanged(title, content, contentFormat, node)
                },
                onDismiss = { showNodes = false },
            )
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
        modifier = modifier.fillMaxWidth(),
//        modifier = modifier
//            .fillMaxWidth()
//            .heightIn(min = 80.dp),
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
    content: String, onContentChanged: (String) -> Unit, modifier: Modifier = Modifier
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
        modifier = modifier.fillMaxWidth(),
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
    createTopicState: CreateTopicState, onCloseClick: () -> Unit, onSendClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.create_topic)) },
        navigationIcon = { CloseButton(onClick = onCloseClick) },
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
    loadNodesState: LoadNodesState, screenState: WriteTopicScreenState
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
            HtmlAlertDialog(content = problem.html, onUriClick = onUriClick)
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
    onNodeClick: (TopicNode?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
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
                .clickable { onNodeClick(node) },
        ) {
            val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            Spacer(Modifier.width(12.dp))
            if (loadNodesState is LoadNodesState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp), color = contentColor, strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Rounded.Polyline, contentDescription = "node", tint = contentColor)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                if (node?.title.isNullOrEmpty()) stringResource(R.string.select_node) else node?.title!!,
                color = contentColor,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(12.dp))
        }
    }
}

