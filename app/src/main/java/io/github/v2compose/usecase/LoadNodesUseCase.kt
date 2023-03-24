package io.github.v2compose.usecase

import androidx.compose.runtime.Stable
import io.github.v2compose.network.bean.TopicNode
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LoadNodesUseCase @Inject constructor(private val topicRepository: TopicRepository) {

    private val _state = MutableStateFlow<LoadNodesState>(LoadNodesState.Idle)
    val state: StateFlow<LoadNodesState> = _state

    suspend fun execute() {
        _state.emit(LoadNodesState.Loading)
        try {
            val result = topicRepository.getTopicNodes()
            if (result.isNotEmpty()) {
                _state.emit(LoadNodesState.Success(result))
            } else {
                _state.emit(LoadNodesState.Error(null))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _state.emit(LoadNodesState.Error(e))
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
