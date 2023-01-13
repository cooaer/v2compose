package io.github.v2compose.ui.main.nodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.result.Result
import io.github.v2compose.core.result.asResult
import io.github.v2compose.repository.NodeRepository
import io.github.v2compose.network.bean.NodesNavInfo
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class NodesViewModel @Inject constructor(private val nodeRepository: NodeRepository) : ViewModel() {

    val nodesNavInfo: StateFlow<NodesUiState> = flow<NodesNavInfo> {
        emit(nodeRepository.getNodesNavInfo())
    }.asResult().map {
        when (it) {
            is Result.Success -> {
                NodesUiState.Success(it.data)
            }
            is Result.Loading -> {
                NodesUiState.Loading
            }
            is Result.Error -> {
                NodesUiState.Error
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        NodesUiState.Loading,
    )
}

sealed interface NodesUiState {
    data class Success(val nodesNavInfo: NodesNavInfo) : NodesUiState
    object Loading : NodesUiState
    object Error : NodesUiState
}