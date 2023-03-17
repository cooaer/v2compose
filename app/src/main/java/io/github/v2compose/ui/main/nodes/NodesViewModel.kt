package io.github.v2compose.ui.main.nodes

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.extension.castOrNull
import io.github.v2compose.network.bean.Node
import io.github.v2compose.repository.NodeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NodesViewModel @Inject constructor(private val nodeRepository: NodeRepository) : ViewModel() {

    companion object {
        private const val minRequestMills = 500L
    }

    private val _nodesUiState = MutableStateFlow<NodesUiState>(NodesUiState.Loading())
    val nodesUiState = _nodesUiState.asStateFlow()

    init {
        loadNodeCategories()
    }

    fun refresh() {
        loadNodeCategories()
    }

    private fun loadNodeCategories() {
        viewModelScope.launch {
            val currentData = _nodesUiState.value.castOrNull<NodesUiState.Success>()?.data
            _nodesUiState.emit(NodesUiState.Loading(currentData))
            try {
                val startMills = System.currentTimeMillis()

                val nodesNavInfo =
                    nodeRepository.nodesNavInfo.first() ?: nodeRepository.getNodesNavInfo()
                val allNodes = nodeRepository.getAllNodes().associateBy { it.name }
                val result = nodesNavInfo.map { category ->
                    val nodes = category.nodes
                        .map { node -> allNodes[node.name] }
                        .filterIsInstance<Node>()
                    Pair(category.category, nodes)
                }

                val requestMills = System.currentTimeMillis() - startMills
                if (requestMills < minRequestMills) {
                    delay(minRequestMills - requestMills)
                }
                _nodesUiState.emit(NodesUiState.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _nodesUiState.emit(NodesUiState.Error(e))
            }
        }
    }

}

@Stable
sealed interface NodesUiState {
    data class Loading(val data: List<Pair<String, List<Node>>>? = null) : NodesUiState
    data class Success(val data: List<Pair<String, List<Node>>>) : NodesUiState
    data class Error(val error: Throwable?) : NodesUiState
}