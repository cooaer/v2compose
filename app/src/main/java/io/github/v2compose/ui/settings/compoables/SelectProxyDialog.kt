package io.github.v2compose.ui.settings.compoables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import io.github.v2compose.R
import io.github.v2compose.bean.ProxyInfo
import io.github.v2compose.bean.ProxyType
import io.github.v2compose.util.InetValidator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectProxyDialog(
    proxyInfo: ProxyInfo, onDismiss: () -> Unit, onProxySelected: (ProxyInfo) -> Unit
) {
    val context = LocalContext.current

    var proxyType by remember(proxyInfo) { mutableStateOf(proxyInfo.type) }
    var proxyAddress by remember(proxyInfo) { mutableStateOf(proxyInfo.address) }
    var proxyPort by remember(proxyInfo) { mutableStateOf(proxyInfo.port.toString()) }

    val inputEnabled =
        remember(proxyType) { proxyType != ProxyType.System && proxyType != ProxyType.Direct }

    var proxyAddressError by remember { mutableStateOf("") }
    var proxyPortError by remember { mutableStateOf("") }

    val adressFocusRequester = remember { FocusRequester() }
    val portFocusRequester = remember { FocusRequester() }

    val checkProxyInfo = {
        while (true) {
            if (proxyType == ProxyType.Direct || proxyType == ProxyType.System) {
                onProxySelected(ProxyInfo(proxyType))
                break
            }

            val address = proxyAddress.trim()
            if (address.isEmpty()) {
                proxyAddressError = context.getString(R.string.settings_proxy_hostOrIp_empty)
                break
            }
            if (!InetValidator.isValidHostOrIp(address)) {
                proxyAddressError = context.getString(R.string.settings_proxy_hostOrIp_format_error)
                break
            }

            val port = proxyPort.trim()
            val portInt = port.toIntOrNull() ?: -1
            if (port.isEmpty()) {
                proxyPortError = context.getString(R.string.settings_proxy_port_empty)
                break
            }
            if (!InetValidator.isValidInetPort(portInt)) {
                proxyPortError = context.getString(R.string.settings_proxy_port_error)
                break
            }
            onProxySelected(ProxyInfo(proxyType, address, portInt))
            break
        }
    }

    AlertDialog(onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.settings_proxy)) },
        text = {
            Column {
                SelectProxyType(proxyType, onProxyTypeSelected = { proxyType = it })
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = TextFieldValue(proxyAddress, TextRange(proxyAddress.length)),
                    onValueChange = {
                        proxyAddress = it.text
                        proxyAddressError = ""
                    },
                    modifier = Modifier.focusRequester(adressFocusRequester),
                    label = { Text(stringResource(id = R.string.settings_proxy_hostOrIp)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { portFocusRequester.requestFocus() }),
                    isError = proxyAddressError.isNotEmpty(),
                    supportingText = { Text(proxyAddressError) },
                    singleLine = true,
                    enabled = inputEnabled,
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = TextFieldValue(proxyPort, TextRange(proxyPort.length)),
                    onValueChange = {
                        proxyPort = it.text
                        proxyPortError = ""
                    },
                    modifier = Modifier.focusRequester(portFocusRequester),
                    label = { Text(stringResource(id = R.string.settings_proxy_port)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { checkProxyInfo() }),
                    isError = proxyPortError.isNotEmpty(),
                    supportingText = { Text(proxyPortError) },
                    singleLine = true,
                    enabled = inputEnabled,
                )

                LaunchedEffect(true) {
                    adressFocusRequester.requestFocus()
                }
            }
        },
        confirmButton = {
            TextButton(onClick = checkProxyInfo) {
                Text(stringResource(id = R.string.ok))
            }
        })
}

@Composable
private fun SelectProxyType(proxyType: ProxyType, onProxyTypeSelected: (ProxyType) -> Unit) {
    var showProxyTypeDropdown by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showProxyTypeDropdown = true }
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(id = proxyType.titleResId))
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { showProxyTypeDropdown = true }) {
            Icon(Icons.Rounded.ArrowDropDown, "drop down")
        }

        DropdownMenu(
            expanded = showProxyTypeDropdown,
            onDismissRequest = { showProxyTypeDropdown = false },
            properties = PopupProperties(focusable = false),
        ) {
            listOf(ProxyType.Direct, ProxyType.Http, ProxyType.Socks).forEach {
                DropdownMenuItem(
                    text = {
                        Text(stringResource(id = it.titleResId))
                    },
                    onClick = {
                        showProxyTypeDropdown = false
                        onProxyTypeSelected(it)
                    },
                )
            }
        }
    }
}

val ProxyType.titleResId
    get() = when (this) {
        ProxyType.System -> R.string.settings_proxy_system
        ProxyType.Direct -> R.string.settings_proxy_direct
        ProxyType.Http -> R.string.settings_proxy_http
        ProxyType.Socks -> R.string.settings_proxy_socks
    }
