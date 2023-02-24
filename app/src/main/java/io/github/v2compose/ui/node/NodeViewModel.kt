package io.github.v2compose.ui.node

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.network.bean.NodeInfo
import io.github.v2compose.repository.NodeRepository
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NodeViewModel"

@HiltViewModel
class NodeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val nodeRepository: NodeRepository,
    private val topicRepository: TopicRepository,
) : ViewModel() {

    val nodeArgs = NodeArgs(savedStateHandle, stringDecoder)

    private val _nodeInfoFlow = MutableStateFlow<NodeUiState>(NodeUiState.Loading)
    val nodeInfoFlow = _nodeInfoFlow.asStateFlow()

    val nodeTopicInfoFlow =
        nodeRepository.getNodeTopicInfoFlow(nodeArgs.nodeId).cachedIn(viewModelScope)

    val topicTitleOverview: StateFlow<Boolean> = topicRepository.topicTitleOverview.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = true,
    )

    init {
        loadNodeInternal()
    }

    fun refreshNode() {
        loadNodeInternal()
    }

    fun retryNode() {
        loadNodeInternal()
    }

    private fun loadNodeInternal() {
        viewModelScope.launch {
            _nodeInfoFlow.emit(NodeUiState.Loading)
            try {
                val nodeInfo = nodeRepository.getNodeInfo(nodeArgs.nodeId)
                Log.d(TAG, "loadNodeInternal, result, nodeInfo = $nodeInfo")
                _nodeInfoFlow.emit(NodeUiState.Success(nodeInfo))
            } catch (e: Exception) {
                e.printStackTrace()
                _nodeInfoFlow.emit(NodeUiState.Error(e))
            }
        }
    }
}

@Stable
sealed interface NodeUiState {
    data class Success(val nodeInfo: NodeInfo) : NodeUiState
    object Loading : NodeUiState
    data class Error(val error: Throwable?) : NodeUiState
}