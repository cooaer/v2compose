package io.github.v2compose.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import v2compose.shared.generated.resources.Res
import v2compose.shared.generated.resources.back

@Composable
fun BackIcon(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onBackClick,
        modifier = modifier,
    ) {
        Icon(
            Icons.AutoMirrored.Rounded.ArrowBack,
            contentDescription = stringResource(Res.string.back),
        )
    }
}
