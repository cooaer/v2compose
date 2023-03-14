package io.github.v2compose.ui.main.mine.nodes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.network.bean.MyNodesInfo
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyNodesViewModel @Inject constructor(private val accountRepository: AccountRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow<MyNodesUiState>(MyNodesUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadMyNodes()
    }

    fun refresh() {
        loadMyNodes()
    }

    private fun loadMyNodes() {
        viewModelScope.launch {
            _uiState.emit(MyNodesUiState.Loading)
            try {
                val result = accountRepository.getMyNodes()
                _uiState.emit(MyNodesUiState.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.emit(MyNodesUiState.Error(e))
            }
        }
    }

}

sealed interface MyNodesUiState {
    object Loading : MyNodesUiState
    data class Success(val data: MyNodesInfo) : MyNodesUiState
    data class Error(val error: Throwable?) : MyNodesUiState
}