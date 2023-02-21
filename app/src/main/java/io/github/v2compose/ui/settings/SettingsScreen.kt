package io.github.v2compose.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.v2compose.BuildConfig
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.bean.DarkMode
import io.github.v2compose.datasource.AppSettings
import io.github.v2compose.network.bean.Release
import io.github.v2compose.ui.common.BackIcon
import io.github.v2compose.ui.common.ListDivider
import io.github.v2compose.ui.common.NewReleaseDialog
import io.github.v2compose.ui.common.SingleChoiceListDialog
import kotlinx.coroutines.launch

@Composable
fun SettingsScreenRoute(
    onBackClick: () -> Unit,
    openUri: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    settingsScreenState: SettingsScreenState = rememberSettingsScreenState()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var newRelease by rememberSaveable(
        saver = mapSaver(
            save = { it.value.toMap() },
            restore = { mutableStateOf(Release.fromMap(it)) },
        )
    ) { mutableStateOf(Release.Empty) }

    val cacheSize by viewModel.cacheSize.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (newRelease.isValid()) {
        NewReleaseDialog(
            release = newRelease,
            onIgnoreClick = {
                viewModel.ignoreRelease(newRelease)
                newRelease = Release.Empty
            },
            onCancelClick = { newRelease = Release.Empty },
            onOkClick = {
                openUri(newRelease.htmlUrl)
                newRelease = Release.Empty
            }
        )
    }

    SettingsScreen(
        isLoggedIn = isLoggedIn,
        cacheSize = cacheSize,
        appSettings = appSettings,
        snackbarHostState = settingsScreenState.snackbarHostState,
        onBackClick = onBackClick,
        onClearCacheClick = viewModel::clearCache,
        onOpenInBrowserChanged = viewModel::setOpenInInternalBrowser,
        onDarkModeChanged = viewModel::setDarkMode,
        onTopicTitleTwoLineMaxChanged = viewModel::setTopicTitleTwoLineMax,
        onSourceClick = openUri,
        onVersionClick = {},
        onCheckForUpdatesClick = {
            coroutineScope.launch {
                settingsScreenState.checkForUpdates(
                    checkForUpdates = viewModel.checkForUpdates::invoke,
                    onNewRelease = {newRelease = it}
                )
            }
        },
        onLogout = {
            coroutineScope.launch {
                settingsScreenState.logout(logout = viewModel::logout)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    isLoggedIn: Boolean,
    cacheSize: Long,
    appSettings: AppSettings,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onClearCacheClick: () -> Unit,
    onOpenInBrowserChanged: (Boolean) -> Unit,
    onDarkModeChanged: (DarkMode) -> Unit,
    onTopicTitleTwoLineMaxChanged: (Boolean) -> Unit,
    onSourceClick: (String) -> Unit,
    onVersionClick: () -> Unit,
    onCheckForUpdatesClick: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        topBar = { SettingsTopBar(onBackClick = onBackClick) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroupTitle(title = stringResource(id = R.string.settings_common))
            ClickablePreference(
                title = stringResource(id = R.string.settings_clear_cache),
                summary = stringResource(id = R.string.settings_clear_cache_summary, cacheSize),
                onPreferenceClick = onClearCacheClick
            )
//            DropdownPreference(
//                title = stringResource(id = R.string.settings_open_in_browser),
//                entries = listOf(
//                    stringResource(id = R.string.settings_internal_browser),
//                    stringResource(id = R.string.settings_external_browser),
//                ),
//                selectedIndex = if (appSettings.openInInternalBrowser) 0 else 1,
//                onEntryClick = { selectedIndex -> onOpenInBrowserChanged(selectedIndex == 0) },
//            )
            PreferenceGroupTitle(title = stringResource(id = R.string.settings_appearance))
            DropdownPreference(
                title = stringResource(id = R.string.settings_dark_mode),
                entries = listOf(
                    stringResource(id = R.string.settings_dark_mode_follow_system),
                    stringResource(id = R.string.settings_dark_mode_off),
                    stringResource(id = R.string.settings_dark_mode_on),
                ),
                selectedIndex = appSettings.darkMode.ordinal,
                onEntryClick = { index -> onDarkModeChanged(DarkMode.values()[index]) },
            )
            SwitchPreference(
                title = stringResource(id = R.string.settings_topic_title_overview),
                summary = stringResource(id = R.string.settings_topic_title_overview_summary),
                checked = appSettings.topicTitleOverview,
                onCheckedChange = onTopicTitleTwoLineMaxChanged,
            )
            PreferenceGroupTitle(title = stringResource(id = R.string.settings_other))
            ClickablePreference(
                title = stringResource(id = R.string.settings_open_source),
                summary = Constants.source,
                onPreferenceClick = { onSourceClick(Constants.source) }
            )
            ClickablePreference(
                title = stringResource(id = R.string.settings_version),
                summary = BuildConfig.VERSION_NAME,
                onPreferenceClick = onVersionClick
            )
            ClickablePreference(
                title = stringResource(id = R.string.settings_check_for_updates),
                summary = stringResource(id = R.string.settings_check_for_updates_summary),
                onPreferenceClick = onCheckForUpdatesClick,
            )
            if (isLoggedIn) {
                Logout(onLogout = onLogout)
            }
            Spacer(Modifier.height(108.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = { BackIcon(onBackClick = onBackClick) },
        title = {
            Text(
                stringResource(R.string.settings),
                style = MaterialTheme.typography.titleLarge
            )
        },
    )
}

@Composable
private fun Logout(onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(stringResource(id = R.string.logout)) },
            text = { Text(stringResource(id = R.string.logout_tips)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text(stringResource(id = R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(id = R.string.cancel))
                }
            })
    }

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(72.dp)
        .clickable { showLogoutDialog = true }) {
        Text(
            stringResource(id = R.string.logout),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun PreferenceGroupTitle(title: String) {
    Box(modifier = Modifier.padding(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = ContentAlpha.medium)
        )
    }
}

@Composable
private fun ClickablePreference(
    title: String,
    summary: String,
    onPreferenceClick: (() -> Unit)? = null
) {
    Box(modifier = Modifier.clickable(enabled = onPreferenceClick != null) { onPreferenceClick?.invoke() }) {
        PreferenceContent(title, summary)
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun PreferenceContent(
    title: String,
    summary: String,
    modifier: Modifier = Modifier,
    contentColor: Color = LocalContentColor.current
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor.copy(alpha = ContentAlpha.high)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = summary,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor.copy(alpha = ContentAlpha.medium)
        )
    }
}


@Composable
private fun SwitchPreference(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Box {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PreferenceContent(title = title, summary = summary, modifier = Modifier.weight(1.0f))
            Switch(checked = checked, onCheckedChange = onCheckedChange)
            Spacer(Modifier.width(16.dp))
        }
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun DropdownPreference(
    title: String,
    entries: List<String>,
    selectedIndex: Int,
    onEntryClick: (Int) -> Unit,
) {
    var showDialog by rememberSaveable(stateSaver = autoSaver()) { mutableStateOf(false) }
    if (showDialog) {
        SingleChoiceListDialog(
            title = title,
            entries = entries,
            selectedIndex = selectedIndex,
            onEntryClick = {
                showDialog = false
                onEntryClick(it)
            },
            onCancel = { showDialog = false },
        )
    }

    ClickablePreference(title = title, summary = entries[selectedIndex]) {
        showDialog = true
    }
}
