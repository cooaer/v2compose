package io.github.v2compose.ui.settings

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import io.github.v2compose.bean.AppSettings
import io.github.v2compose.bean.DarkMode
import io.github.v2compose.bean.ProxyInfo
import io.github.v2compose.bean.ProxyType
import io.github.v2compose.core.NotificationCenter
import io.github.v2compose.network.bean.Release
import io.github.v2compose.ui.common.*
import io.github.v2compose.ui.settings.compoables.*
import kotlinx.coroutines.launch

@Composable
fun SettingsScreenRoute(
    onBackClick: () -> Unit,
    openUri: (String) -> Unit,
    onLogoutSuccess: () -> Unit,
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
    val proxyInfo by viewModel.proxyInfo.collectAsStateWithLifecycle()
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    if (newRelease.isValid()) {
        NewReleaseDialog(release = newRelease, onIgnoreClick = {
            viewModel.ignoreRelease(newRelease)
            newRelease = Release.Empty
        }, onCancelClick = { newRelease = Release.Empty }, onOkClick = {
            openUri(newRelease.htmlUrl)
            newRelease = Release.Empty
        })
    }

    SettingsScreen(isLoggedIn = isLoggedIn,
        cacheSize = cacheSize,
        appSettings = appSettings,
        proxyInfo = proxyInfo,
        onBackClick = onBackClick,
        onClearCacheClick = viewModel::clearCache,
        onAutoCheckInChanged = viewModel::updateAutoCheckIn,
        onReplyWithFloorChanged = viewModel::updateReplyWithFloor,
        onOpenInBrowserChanged = viewModel::setOpenInInternalBrowser,
        onDarkModeChanged = viewModel::setDarkMode,
        onTopicTitleTwoLineMaxChanged = viewModel::setTopicTitleTwoLineMax,
        onHighlightOpReplyChanged = viewModel::toggleHighlightOpReply,
        onProxyChanged = viewModel::changeProxy,
        onSourceClick = openUri,
        onIssuesClick = openUri,
        onVersionClick = {},
        onCheckForUpdatesClick = {
            coroutineScope.launch {
                settingsScreenState.checkForUpdates(
                    checkForUpdates = { viewModel.checkForUpdates.invoke(true) },
                    onNewRelease = { newRelease = it },
                )
            }
        },
        onLogout = {
            coroutineScope.launch {
                viewModel.logout()
                onLogoutSuccess()
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    isLoggedIn: Boolean,
    cacheSize: Long,
    appSettings: AppSettings,
    proxyInfo: ProxyInfo,
    onBackClick: () -> Unit,
    onClearCacheClick: () -> Unit,
    onAutoCheckInChanged: (Boolean) -> Unit,
    onReplyWithFloorChanged: (Boolean) -> Unit,
    onOpenInBrowserChanged: (Boolean) -> Unit,
    onDarkModeChanged: (DarkMode) -> Unit,
    onTopicTitleTwoLineMaxChanged: (Boolean) -> Unit,
    onHighlightOpReplyChanged: (Boolean) -> Unit,
    onProxyChanged: (ProxyInfo) -> Unit,
    onSourceClick: (String) -> Unit,
    onIssuesClick: (String) -> Unit,
    onVersionClick: () -> Unit,
    onCheckForUpdatesClick: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        topBar = { SettingsTopBar(onBackClick = onBackClick) },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            PreferenceGroupTitle(title = stringResource(id = R.string.settings_common))
            ClearCachePreference(cacheSize, onClearCacheClick)
            AutoCheckInPreference(appSettings, onAutoCheckInChanged)
            SwitchPreference(
                title = stringResource(id = R.string.settings_reply_with_floor),
                summary = stringResource(id = R.string.settings_reply_with_floor_description),
                checked = appSettings.replyWithFloor,
                onCheckedChange = onReplyWithFloorChanged,
            )
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
            SwitchPreference(
                title = stringResource(id = R.string.settings_highlight_op_reply),
                summary = stringResource(id = R.string.settings_highlight_op_reply_summary),
                checked = appSettings.highlightOpReply,
                onCheckedChange = onHighlightOpReplyChanged,
            )
            PreferenceGroupTitle(title = stringResource(id = R.string.settings_advanced))
            ProxyPreference(
                title = stringResource(id = R.string.settings_proxy),
                proxyInfo = proxyInfo,
                onProxyChanged = onProxyChanged
            )
            PreferenceGroupTitle(title = stringResource(id = R.string.settings_other))
            ClickablePreference(title = stringResource(id = R.string.settings_open_source),
                summary = Constants.source,
                onPreferenceClick = { onSourceClick(Constants.source) })
            ClickablePreference(title = stringResource(id = R.string.settings_issues),
                summary = Constants.issues,
                onPreferenceClick = { onIssuesClick(Constants.issues) })
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

@Composable
private fun ClearCachePreference(cacheSize: Long, onClearCacheClick: () -> Unit) {
    var showClearCacheDialog by remember { mutableStateOf(false) }
    if (showClearCacheDialog) {
        TextAlertDialog(title = stringResource(id = R.string.settings_clear_cache),
            message = stringResource(id = R.string.clear_cache_tips),
            onConfirm = {
                showClearCacheDialog = false
                onClearCacheClick()
            },
            onDismiss = { showClearCacheDialog = false })
    }

    ClickablePreference(
        title = stringResource(id = R.string.settings_clear_cache),
        summary = stringResource(id = R.string.settings_clear_cache_summary, cacheSize),
        onPreferenceClick = { showClearCacheDialog = true },
    )
}

@Composable
private fun AutoCheckInPreference(
    appSettings: AppSettings, onAutoCheckInChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    var currentChecked by remember(appSettings.autoCheckIn) { mutableStateOf(appSettings.autoCheckIn) }

    var showRequestNotificationPermissionRationale by remember { mutableStateOf(false) }
    //自动签到 -> CoroutineWorker -> ForegroundNotification -> 通知权限(Android 13)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { result ->
        if (result) {
            onAutoCheckInChanged(true)
        } else {
            currentChecked = false
        }
    }

    val openAppNotificationSettingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) {
        if (NotificationCenter.isAutoCheckInChannelEnabled(context)) {
            onAutoCheckInChanged(true)
        } else {
            currentChecked = false
        }
    }

    if (showRequestNotificationPermissionRationale) {
        TextAlertDialog(
            title = stringResource(id = R.string.request_notification_permission),
            message = stringResource(id = R.string.request_notification_permission_message),
            onDismiss = {
                showRequestNotificationPermissionRationale = false
                currentChecked = false
            },
            onConfirm = {
                val intent = Intent().apply {
                    action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    putExtra("android.provider.extra.APP_PACKAGE", context.packageName)
                }
                openAppNotificationSettingsLauncher.launch(intent)
            },
        )
    }

    SwitchPreference(
        title = stringResource(R.string.settings_auto_check_in),
        summary = stringResource(R.string.settings_auto_check_in_description),
        checked = currentChecked,
        onCheckedChange = {
            currentChecked = it
            if (it) {
                checkAndRequestNotificationPermission(context,
                    notificationPermissionLauncher,
                    showRationale = { showRequestNotificationPermissionRationale = true },
                    onDenied = { showRequestNotificationPermissionRationale = true },
                    onGranted = { onAutoCheckInChanged(true) })
            } else {
                onAutoCheckInChanged(false)
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        navigationIcon = { BackIcon(onBackClick = onBackClick) },
        title = {
            Text(
                stringResource(R.string.settings), style = MaterialTheme.typography.titleLarge
            )
        },
    )
}

@Composable
private fun Logout(onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    if (showLogoutDialog) {
        AlertDialog(onDismissRequest = { showLogoutDialog = false },
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
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
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
    title: String, summary: String? = null, onPreferenceClick: (() -> Unit)? = null
) {
    Box(modifier = Modifier.clickable(enabled = onPreferenceClick != null) { onPreferenceClick?.invoke() }) {
        PreferenceContent(title, summary = summary)
        ListDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun PreferenceContent(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    contentColor: Color = LocalContentColor.current
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = contentColor.copy(alpha = ContentAlpha.high)
        )
        summary?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = ContentAlpha.medium)
            )
        }
    }
}


@Composable
private fun SwitchPreference(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.clickable { onCheckedChange(!checked) }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PreferenceContent(
                title = title, summary = summary, modifier = Modifier.weight(1.0f)
            )
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

@Composable
private fun ProxyPreference(
    title: String, proxyInfo: ProxyInfo, onProxyChanged: (ProxyInfo) -> Unit
) {
    val context = LocalContext.current
    var showSelectProxyDialog by remember { mutableStateOf(false) }
    var currentProxy by remember(proxyInfo) { mutableStateOf(proxyInfo) }

    val summary = remember(proxyInfo) {
        val typeText = context.getString(currentProxy.type.titleResId)
        val addressText =
            if (proxyInfo.type == ProxyType.Http || proxyInfo.type == ProxyType.Socks) {
                proxyInfo.address + ":" + proxyInfo.port
            } else ""
        "$typeText $addressText"
    }

    ClickablePreference(title = title, summary = summary) {
        showSelectProxyDialog = true
    }

    if (showSelectProxyDialog) {
        SelectProxyDialog(
            proxyInfo = currentProxy,
            onDismiss = { showSelectProxyDialog = false },
            onProxySelected = {
                showSelectProxyDialog = false
                currentProxy = it
                onProxyChanged(it)
            },
        )
    }

}