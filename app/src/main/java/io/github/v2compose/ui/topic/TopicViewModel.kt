package io.github.v2compose.ui.topic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.repository.TopicRepository
import javax.inject.Inject

@HiltViewModel
class TopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val topicRepository: TopicRepository,
) : ViewModel() {
    val topicArgs = TopicArgs(savedStateHandle, stringDecoder)

    fun topicItemFlow(reversed: Boolean) =
        topicRepository.getTopic(topicArgs.topicId, reversed).cachedIn(viewModelScope)

}