package io.github.v2compose.ui.write

import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.bean.DraftTopic
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.core.extension.isRedirect
import io.github.v2compose.network.bean.CreateTopicPageInfo
import io.github.v2compose.network.bean.TopicNode
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class WriteTopicViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val topicRepository: TopicRepository,
) : ViewModel() {

    val writeTopicArgs: WriteTopicArgs = WriteTopicArgs(savedStateHandle, stringDecoder)

    private val _loadNodesState = MutableStateFlow<LoadNodesState>(LoadNodesState.Idle)
    val loadNodesState: StateFlow<LoadNodesState> = _loadNodesState

    private val _createTopicState = MutableStateFlow<CreateTopicState>(CreateTopicState.Idle)
    val createTopicState: StateFlow<CreateTopicState> = _createTopicState

    val draftTopic: DraftTopic
        get() = runBlocking {
            topicRepository.draftTopic.first().let { local ->
                if (local.node != null) local else
                    local.copy(
                        node = TopicNode(
                            writeTopicArgs.nodeId ?: "",
                            writeTopicArgs.nodeName ?: "",
                        )
                    )
            }
        }

    init {
        loadNodes()
    }

    fun loadNodes() {
        viewModelScope.launch {
            _loadNodesState.emit(LoadNodesState.Loading)
            try {
                val result = topicRepository.getTopicNodes()
                if (result.isNotEmpty()) {
                    _loadNodesState.emit(LoadNodesState.Success(result))
                } else {
                    _loadNodesState.emit(LoadNodesState.Error(null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _loadNodesState.emit(LoadNodesState.Error(e))
            }
        }
    }


    fun createTopic(title: String, content: String, nodeId: String) {
        viewModelScope.launch {
            val once: String =
                _createTopicState.value.let { if (it is CreateTopicState.Failure) it.pageInfo.once else "" }
            _createTopicState.emit(CreateTopicState.Loading)
            try {
                val currentOnce = once.ifEmpty {
                    val pageInfo = topicRepository.getCreateTopicPageInfo()
                    if (pageInfo.once.isNullOrEmpty()) {
                        _createTopicState.emit(CreateTopicState.Error(null))
                        return@launch
                    }
                    pageInfo.once
                }
                val result = topicRepository.createTopic(title, content, nodeId, currentOnce)
                _createTopicState.emit(CreateTopicState.Failure(result))
            } catch (e: Exception) {
                e.printStackTrace()
                while (true) {
                    if (e !is HttpException || !e.code().isRedirect) break
                    saveDraftTopic("", "", null)
                    val location = e.response()?.raw()?.headers?.get("location") ?: break
                    val topicId = Uri.parse(location).pathSegments.getOrNull(1) ?: break
                    _createTopicState.emit(CreateTopicState.Success(topicId))
                    return@launch
                }
                _createTopicState.emit(CreateTopicState.Error(e))
            }
        }
    }

    fun saveDraftTopic(title: String, content: String, node: TopicNode?) {
        viewModelScope.launch {
            topicRepository.saveDraftTopic(title, content, node)
        }
    }

}

@Stable
sealed interface LoadNodesState {
    object Idle : LoadNodesState
    object Loading : LoadNodesState
    data class Success(val data: List<TopicNode>) : LoadNodesState
    data class Error(val error: Throwable?) : LoadNodesState
}

@Stable
sealed interface CreateTopicState {
    object Idle : CreateTopicState
    object Loading : CreateTopicState
    data class Success(val topicId: String) : CreateTopicState
    data class Failure(val pageInfo: CreateTopicPageInfo) : CreateTopicState
    data class Error(val error: Throwable?) : CreateTopicState
}