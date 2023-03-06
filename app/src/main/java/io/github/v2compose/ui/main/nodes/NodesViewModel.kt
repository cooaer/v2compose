package io.github.v2compose.ui.main.nodes

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.network.bean.NodesNavInfo
import io.github.v2compose.repository.NodeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NodesViewModel @Inject constructor(private val nodeRepository: NodeRepository) : ViewModel() {

    private val _nodesUiState = MutableStateFlow<NodesUiState>(NodesUiState.Idle)
    val nodesUiState = _nodesUiState.asStateFlow()

    val nodesNavInfo: StateFlow<NodesNavInfo?> = nodeRepository.nodesNavInfo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

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