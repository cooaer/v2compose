package io.github.v2compose.ui.topic

import android.util.Size
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val topicRepository: TopicRepository,
) : ViewModel() {
    val topicArgs = TopicArgs(savedStateHandle, stringDecoder)

    val repliesReversed: SharedFlow<Boolean> = topicRepository.repliesOrderReversed
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    private val _htmlImageSizes = mutableStateMapOf<String, Size>()
    val htmlImageSizes: Map<String, Size>
        get() = _htmlImageSizes.toMap()

    @OptIn(ExperimentalCoroutinesApi::class)
    val topicItemFlow: Flow<PagingData<Any>> =
        repliesReversed.flatMapLatest { topicRepository.getTopic(topicArgs.topicId, it) }
            .cachedIn(viewModelScope)

    fun toggleRepliesReversed() {
        viewModelScope.launch {
            topicRepository.toggleRepliesReversed()
        }
    }

    fun saveHtmlImageSize(src: String, size: Size) {
        _htmlImageSizes[src] = size
    }

}