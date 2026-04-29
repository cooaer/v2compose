package io.github.v2compose.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.v2compose.Constants
import io.github.v2compose.AutoCheckInPrerequisiteState
import io.github.v2compose.LocalAppPlatformHandlers
import io.github.v2compose.PlatformCapabilities
import io.github.v2compose.network.bean.Release
import io.github.v2compose.shared.bean.AppSettings
import io.github.v2compose.shared.bean.DarkMode
import io.github.v2compose.shared.bean.ProxyInfo
import io.github.v2compose.shared.bean.ProxyType
import io.github.v2compose.ui.common.BackIcon
import io.github.v2compose.ui.common.ListDivider
import io.github.v2compose.ui.common.NewReleaseDialog
import io.github.v2compose.ui.common.SingleChoiceListDialog
import io.github.v2compose.ui.common.TextAlertDialog
import io.github.v2compose.ui.settings.composables.SelectProxyDialog
import io.github.v2compose.ui.settings.composables.checkAndRequestNotificationPermission
import io.github.v2compose.ui.settings.composables.titleRes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import v2compose.shared.generated.resources.Res
import v2compose.shared.generated.resources.cancel
import v2compose.shared.generated.resources.clear_cache_tips
import v2compose.shared.generated.resources.logout
import v2compose.shared.generated.resources.logout_tips
import v2compose.shared.generated.resources.ok
import v2compose.shared.generated.resources.settings
import v2compose.shared.generated.resources.settings_advanced
import v2compose.shared.generated.resources.settings_appearance
import v2compose.shared.generated.resources.settings_auto_check_in
import v2compose.shared.generated.resources.settings_auto_check_in_description
import v2compose.shared.generated.resources.settings_auto_check_in_description_ios
import v2compose.shared.generated.resources.settings_check_for_updates
import v2compose.shared.generated.resources.settings_check_for_updates_summary
import v2compose.shared.generated.resources.settings_clear_cache
import v2compose.shared.generated.resources.settings_clear_cache_summary
import v2compose.shared.generated.resources.settings_common
import v2compose.shared.generated.resources.settings_dark_mode
import v2compose.shared.generated.resources.settings_dark_mode_follow_system
import v2compose.shared.generated.resources.settings_dark_mode_off
import v2compose.shared.generated.resources.settings_dark_mode_on
import v2compose.shared.generated.resources.settings_highlight_op_reply
import v2compose.shared.generated.resources.settings_highlight_op_reply_summary
import v2compose.shared.generated.resources.settings_issues
import v2compose.shared.generated.resources.settings_open_source
import v2compose.shared.generated.resources.settings_other
import v2compose.shared.generated.resources.settings_proxy
import v2compose.shared.generated.resources.settings_proxy_ios_notice
import v2compose.shared.generated.resources.settings_reply_with_floor
import v2compose.shared.generated.resources.settings_reply_with_floor_description
import v2compose.shared.generated.resources.settings_topic_title_overview
import v2compose.shared.generated.resources.settings_topic_title_overview_summary
import v2compose.shared.generated.resources.settings_version

@Composable
fun SettingsScreenRoute(
    onBackClick: () -> Unit,
    openUri: (String) -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel(),
    settingsScreenState: SettingsScreenState = rememberSettingsScreenState()
) {
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

    SettingsScreen(
        isLoggedIn = isLoggedIn,
        cacheSize = cacheSize,
        appSettings = appSettings,
        proxyInfo = proxyInfo,
        onBackClick = onBackClick,
        onClearCacheClick = viewModel::clearCache,
        onAutoCheckInChanged = viewModel::updateAutoCheckIn,
        onReplyWithFloorChanged = viewModel::updateReplyWithFloor,
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
            val platformHandlers = LocalAppPlatformHandlers.current
            PreferenceGroupTitle(title = stringResource(Res.string.settings_common))
            ClearCachePreference(cacheSize, onClearCacheClick)
            if (platformHandlers.capabilities.supportsAutoCheckIn) {
                AutoCheckInPreference(appSettings, onAutoCheckInChanged)
            }
            SwitchPreference(
                title = stringResource(Res.string.settings_reply_with_floor),
                summary = stringResource(Res.string.settings_reply_with_floor_description),
                checked = appSettings.replyWithFloor,
                onCheckedChange = onReplyWithFloorChanged,
            )
            PreferenceGroupTitle(title = stringResource(Res.string.settings_appearance))
            DropdownPreference(
                title = stringResource(Res.string.settings_dark_mode),
                entries = listOf(
                    stringResource(Res.string.settings_dark_mode_follow_system),
                    stringResource(Res.string.settings_dark_mode_off),
                    stringResource(Res.string.settings_dark_mode_on),
                ),
                selectedIndex = appSettings.darkMode.ordinal,
                onEntryClick = { index -> onDarkModeChanged(DarkMode.entries[index]) },
            )
            SwitchPreference(
                title = stringResource(Res.string.settings_topic_title_overview),
                summary = stringResource(Res.string.settings_topic_title_overview_summary),
                checked = appSettings.topicTitleOverview,
                onCheckedChange = onTopicTitleTwoLineMaxChanged,
            )
            SwitchPreference(
                title = stringResource(Res.string.settings_highlight_op_reply),
                summary = stringResource(Res.string.settings_highlight_op_reply_summary),
                checked = appSettings.highlightOpReply,
                onCheckedChange = onHighlightOpReplyChanged,
            )
            PreferenceGroupTitle(title = stringResource(Res.string.settings_advanced))
            ProxyPreference(
                title = stringResource(Res.string.settings_proxy),
                proxyInfo = proxyInfo,
                onProxyChanged = onProxyChanged
            )
            PreferenceGroupTitle(title = stringResource(Res.string.settings_other))
            ClickablePreference(
                title = stringResource(Res.string.settings_open_source),
                summary = Constants.source,
                onPreferenceClick = { onSourceClick(Constants.source) })
            ClickablePreference(
                title = stringResource(Res.string.settings_issues),
                summary = Constants.issues,
                onPreferenceClick = { onIssuesClick(Constants.issues) })
            ClickablePreference(
                title = stringResource(Res.string.settings_version),
                summary = Constants.versionName,
                onPreferenceClick = onVersionClick
            )
            ClickablePreference(
                title = stringResource(Res.string.settings_check_for_updates),
                summary = stringResource(Res.string.settings_check_for_updates_summary),
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
        TextAlertDialog(
            title = stringResource(Res.string.settings_clear_cache),
            message = stringResource(Res.string.clear_cache_tips),
            onConfirm = {
                showClearCacheDialog = false
                onClearCacheClick()
            },
            onDismiss = { showClearCacheDialog = false })
    }

    ClickablePreference(
        title = stringResource(Res.string.settings_clear_cache),
        summary = stringResource(Res.string.settings_clear_cache_summary, cacheSize),
        onPreferenceClick = { showClearCacheDialog = true },
    )
}

@Composable
private fun AutoCheckInPreference(
    appSettings: AppSettings, onAutoCheckInChanged: (Boolean) -> Unit
) {
    val platformHandlers = LocalAppPlatformHandlers.current
    var currentChecked by remember(appSettings.autoCheckIn) { mutableStateOf(appSettings.autoCheckIn) }
    var shouldRequestNotificationPermission by remember { mutableStateOf(false) }
    var showRequestNotificationPermissionRationale by remember { mutableStateOf(false) }

    if (shouldRequestNotificationPermission) {
        checkAndRequestNotificationPermission(
            showRationale = { showRequestNotificationPermissionRationale = true },
            onDenied = {
                shouldRequestNotificationPermission = false
                currentChecked = false
            },
            onGranted = {
                shouldRequestNotificationPermission = false
                onAutoCheckInChanged(true)
            },
        )
    }

    if (showRequestNotificationPermissionRationale) {
        TextAlertDialog(
            title = stringResource(Res.string.notification_permission_title),
            message = stringResource(Res.string.notification_permission_settings_message),
            onDismiss = {
                showRequestNotificationPermissionRationale = false
                shouldRequestNotificationPermission = false
                currentChecked = false
            },
            onConfirm = {
                showRequestNotificationPermissionRationale = false
                shouldRequestNotificationPermission = false
                platformHandlers.openNotificationSettings()
            },
        )
    }

    SwitchPreference(
        title = stringResource(Res.string.settings_auto_check_in),
        summary = if (platformHandlers.capabilities == PlatformCapabilities.Ios) {
            stringResource(Res.string.settings_auto_check_in_description_ios)
        } else {
            stringResource(Res.string.settings_auto_check_in_description)
        },
        checked = currentChecked,
        onCheckedChange = { checked ->
            currentChecked = checked
            if (checked) {
                when (platformHandlers.checkAutoCheckInPrerequisite()) {
                    AutoCheckInPrerequisiteState.Ready -> onAutoCheckInChanged(true)
                    AutoCheckInPrerequisiteState.RequiresNotificationPermission -> {
                        shouldRequestNotificationPermission = true
                    }
                    AutoCheckInPrerequisiteState.RequiresNotificationSettings -> {
                        showRequestNotificationPermissionRationale = true
                    }
                }
            } else {
                shouldRequestNotificationPermission = false
                showRequestNotificationPermissionRationale = false
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
                stringResource(Res.string.settings), style = MaterialTheme.typography.titleLarge
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
            title = { Text(stringResource(Res.string.logout)) },
            text = { Text(stringResource(Res.string.logout_tips)) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) {
                    Text(stringResource(Res.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            })
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clickable { showLogoutDialog = true }) {
        Text(
            stringResource(Res.string.logout),
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
            color = MaterialTheme.colorScheme.primary
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
    summary: String? = null
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        summary?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
    val platformHandlers = LocalAppPlatformHandlers.current
    var showSelectProxyDialog by remember { mutableStateOf(false) }
    var currentProxy by remember(proxyInfo) { mutableStateOf(proxyInfo) }

    val typeText = stringResource(currentProxy.type.titleRes)
    val iosProxyNotice = stringResource(Res.string.settings_proxy_ios_notice)
    val summary = remember(proxyInfo, typeText, iosProxyNotice, platformHandlers.capabilities) {
        val addressText =
            if (proxyInfo.type == ProxyType.Http || proxyInfo.type == ProxyType.Socks) {
                proxyInfo.address + ":" + proxyInfo.port
            } else ""
        val baseSummary = "$typeText $addressText".trim()
        if (platformHandlers.capabilities == PlatformCapabilities.Ios) {
            "$baseSummary\n$iosProxyNotice".trim()
        } else {
            baseSummary
        }
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
