package io.github.v2compose.ui.gallery

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.StringDecoder
import javax.inject.Inject

private const val TAG = "GalleryViewModel"

@HiltViewModel
class GalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder
) : ViewModel() {

    val screenArgs = GalleryScreenArgs(savedStateHandle, stringDecoder)

    init {
        Log.d(TAG, "args = $screenArgs")
    }

}