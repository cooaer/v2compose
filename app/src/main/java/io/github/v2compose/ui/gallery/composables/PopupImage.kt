package io.github.v2compose.ui.gallery.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import io.github.v2compose.ui.common.GalleryImage

@Composable
fun PopupImage(imageUrl: String, onDismiss: () -> Unit) {

    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        )
    ) {
        GalleryImage(imageUrl = imageUrl, onBackgroundClick = onDismiss)
    }
}