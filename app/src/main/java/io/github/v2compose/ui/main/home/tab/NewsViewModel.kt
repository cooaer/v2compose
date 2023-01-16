package io.github.v2compose.ui.main.home.tab

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val newsRepository: NewsRepository,
) : ViewModel() {

    companion object {
        const val KEY_TAB = "tab"
    }

    val tab: String = savedStateHandle[KEY_TAB] ?: ""

    private val _refreshingFlow = MutableStateFlow(false);
    val refreshingFlow = _refreshingFlow.asStateFlow()

    private val _newsInfoFlow = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val newsInfoFlow = _newsInfoFlow.asStateFlow()

    init {
        load()
    }

    fun refresh() {
        viewModelScope.launch {
            _refreshingFlow.emit(true)
            loadInternal()
            _refreshingFlow.emit(false)
        }
    }

    fun retry() {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _newsInfoFlow.emit(NewsUiState.Loading)
            loadInternal()
        }
    }

    private suspend fun loadInternal() {
        try {
            val newsInfo = newsRepository.getHomeNews(tab)
            _newsInfoFlow.emit(NewsUiState.Success(newsInfo))
        } catch (e: Exception) {
            _newsInfoFlow.emit(NewsUiState.Error(e))
        }
    }

    //TODO: 当销毁时，缓存数据到硬盘
    override fun onCleared() {
        super.onCleared()
    }
}

@Stable
sealed interface NewsUiState {
    object Loading : NewsUiState
    data class Success(val newsInfo: NewsInfo) : NewsUiState
    data class Error(val throwable: Throwable?) : NewsUiState
}