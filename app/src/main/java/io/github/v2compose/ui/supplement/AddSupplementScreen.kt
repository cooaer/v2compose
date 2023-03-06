package io.github.v2compose.ui.supplement

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.v2compose.R
import io.github.v2compose.network.bean.AppendTopicPageInfo
import io.github.v2compose.ui.common.CloseButton
import io.github.v2compose.ui.common.HtmlAlertDialog
import io.github.v2compose.ui.common.TextAlertDialog

@Composable
fun AddSupplementScreenRoute(
    onCloseClick: () -> Unit,
    onAddSupplementSuccess: (String) -> Unit,
    openUri: (String) -> Unit,
    viewModel: AddSupplementViewModel = hiltViewModel(),
    screenState: AddSupplementScreenState = rememberAddSupplementScreenState(),
) {
    val topicId = viewModel.args.topicId
    val pageInfo by viewModel.pageInfo.collectAsStateWithLifecycle()
    val addSupplementState by viewModel.addSupplementState.collectAsStateWithLifecycle()

    HandleProblem(pageInfo, openUri)

    HandleAddSupplementState(screenState, addSupplementState, onAddSupplementSuccess, topicId)

    AddSupplementScreen(
        addSupplementState = addSupplementState,
        onCloseClick = onCloseClick,
        onAddSupplementClick = viewModel::addSupplement,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddSupplementScreen(
    addSupplementState: AddSupplementState,
    onCloseClick: () -> Unit,
    onAddSupplementClick: (String) -> Unit,
) {
    var supplement by rememberSaveable { mutableStateOf("") }

    AddSupplementBackHandler(supplement, onCloseClick)

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { CloseButton(onClick = onCloseClick) },
                title = { Text(stringResource(id = R.string.add_supplement)) },
                actions = {
                    AddSupplementButton(addSupplementState) { onAddSupplementClick(supplement) }
                }
            )
        },
        contentWindowInsets = WindowInsets.systemBars.union(WindowInsets.ime),
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            WriteSupplementField(
                supplement,
                onTextChanged = { supplement = it },
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun AddSupplementButton(
    addSupplementState: AddSupplementState,
    onAddSupplementClick: () -> Unit
) {
    IconButton(onClick = { onAddSupplementClick() }) {
        if (addSupplementState is AddSupplementState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
            )
        } else {
            Icon(Icons.Rounded.Send, "send")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteSupplementField(
    text: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    TextField(
        value = text,
        onValueChange = onTextChanged,
        modifier = modifier
            .fillMaxSize()
            .focusRequester(focusRequester),
        colors = TextFieldDefaults.textFieldColors(
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
        ),
        placeholder = {
            Text(
                stringResource(id = R.string.add_supplement_tips),
                color = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
            )
        },
    )
    LaunchedEffect(true) {
        focusRequester.requestFocus()
    }
}

@Composable
private fun AddSupplementBackHandler(supplement: String, onCloseClick: () -> Unit) {
    var showBackTips by remember { mutableStateOf(false) }

    BackHandler(enabled = supplement.isNotEmpty()) {
        showBackTips = true
    }

    if (showBackTips) {
        TextAlertDialog(
            message = stringResource(id = R.string.add_supplement_back_tips),
            onConfirm = { onCloseClick() },
            onDismiss = { showBackTips = false })
    }
}

@Composable
private fun HandleProblem(pageInfo: AppendTopicPageInfo?, onUriClick: (String) -> Unit) {
    pageInfo?.problem?.let {
        if (!it.isEmpty) {
            HtmlAlertDialog(content = it.html, onUriClick = onUriClick)
        }
    }
}

@Composable
private fun HandleAddSupplementState(
    screenState: AddSupplementScreenState,
    addSupplementState: AddSupplementState,
    onAddSupplementSuccess: (String) -> Unit,
    topicId: String
) {
    if (addSupplementState is AddSupplementState.Success) {
        LaunchedEffect(addSupplementState) {
            onAddSupplementSuccess(topicId)
        }
    } else if (addSupplementState is AddSupplementState.Error) {
        LaunchedEffect(addSupplementState) {
            addSupplementState.error?.message.let {
                if (it.isNullOrEmpty()) {
                    screenState.showMessage(R.string.add_supplement_fail_tips)
                } else {
                    screenState.showMessage(it)
                }
            }
        }
    }
}

