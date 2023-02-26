package io.github.v2compose.ui.node

import android.app.Application
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.R
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.network.bean.NodeInfo
import io.github.v2compose.network.bean.NodeTopicInfo
import io.github.v2compose.repository.NodeRepository
import io.github.v2compose.repository.TopicRepository
import io.github.v2compose.ui.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NodeViewModel"

@HiltViewModel
class NodeViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val nodeRepository: NodeRepository,
    private val topicRepository: TopicRepository,
) : BaseViewModel(application) {

    val nodeArgs = NodeArgs(savedStateHandle, stringDecoder)

    private val _nodeTopicInfo = MutableStateFlow<NodeTopicInfo?>(null)
    val nodeTopicInfo: StateFlow<NodeTopicInfo?> = _nodeTopicInfo

    private val _nodeInfo = MutableStateFlow<NodeUiState>(NodeUiState.Loading)
    val nodeInfo = _nodeInfo.asStateFlow()

    val nodeTopicItems =
        nodeRepository.getNodeTopicInfoFlow(nodeArgs.nodeId).cachedIn(viewModelScope)

    //标题概览
    val topicTitleOverview: StateFlow<Boolean> = topicRepository.topicTitleOverview.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = true,
    )

    init {
        loadNodeInternal()
    }

    fun retryNode() {
        loadNodeInternal()
    }

    private fun loadNodeInternal() {
        viewModelScope.launch {
            _nodeInfo.emit(NodeUiState.Loading)
            try {
                val nodeInfo = nodeRepository.getNodeInfo(nodeArgs.nodeId)
                Log.d(TAG, "loadNodeInternal, result, nodeInfo = $nodeInfo")
                _nodeInfo.emit(NodeUiState.Success(nodeInfo))
            } catch (e: Exception) {
                e.printStackTrace()
                _nodeInfo.emit(NodeUiState.Error(e))
            }
        }
    }

    fun updateNodeTopicInfo(value: NodeTopicInfo?) {
        viewModelScope.launch {
            _nodeTopicInfo.emit(value)
        }
    }

    fun follow() {
        doNodeAction { it.favoriteLink }
    }

    private fun doNodeAction(actionUrl: (NodeTopicInfo) -> String) {
        val node = _nodeTopicInfo.value ?: return
        val url = actionUrl(node)
        viewModelScope.launch {
            try {
                val result = nodeRepository.doNodeAction(nodeArgs.nodeId, url)
                _nodeTopicInfo.emit(result)
//                val successTips =
//                    if (result.hasStared()) R.string.node_favorite_success_tips else R.string.node_unfavorite_success_tips
//                updateSnackbarMessage(context.getString(successTips))
            } catch (e: Exception) {
                e.printStackTrace()
                updateSnackbarMessage(e.message ?: context.getString(R.string.node_action_failure_tips))
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