package io.github.v2compose.ui.main.nodes

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.network.bean.Node
import io.github.v2compose.network.bean.NodesNavInfo
import io.github.v2compose.repository.NodeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NodesViewModel @Inject constructor(private val nodeRepository: NodeRepository) : ViewModel() {

    private val _nodesUiState = MutableStateFlow<NodesUiState>(NodesUiState.Idle)
    val nodesUiState = _nodesUiState.asStateFlow()

    private val allNodes: Flow<Map<String, Node>> = flow {
        val allNodes = nodeRepository.getAllNodes()
        emit(allNodes.associateBy { it.name })
    }

    val nodeCategories: StateFlow<List<Pair<String, List<Node>>>> =
        nodeRepository.nodesNavInfo
            .combine(allNodes, transform = { nodesNavInfo, allNodes ->
                nodesNavInfo?.map { category ->
                    val nodes = category.nodes
                        .map { node -> allNodes[node.name] }
                        .filterIsInstance<Node>()
                    Pair(category.category, nodes)
                } ?: listOf()
            })
            .catch { emit(listOf()) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(),
                listOf()
            )

    fun refresh() {
        viewModelScope.launch {
            _nodesUiState.emit(NodesUiState.Loading)
            try {
                nodeRepository.getNodesNavInfo().let {
                    _nodesUiState.emit(NodesUiState.Success(it))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _nodesUiState.emit(NodesUiState.Error(e))
            }
        }
    }

}

@Stable
sealed interface NodesUiState {
    object Idle : NodesUiState
    data class Success(val nodesNavInfo: NodesNavInfo) : NodesUiState
    object Loading : NodesUiState
    data class Error(val error: Throwable?) : NodesUiState
}