package io.github.v2compose.ui.main.home.tab

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.repository.NewsRepository
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val newsRepository: NewsRepository,
    private val topicRepository: TopicRepository,
) : ViewModel() {

    companion object {
        const val KEY_TAB = "tab"
    }

    val tab: String = savedStateHandle[KEY_TAB] ?: ""

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _newsInfoFlow = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val newsUiState = _newsInfoFlow.asStateFlow()

    val topicTitleOverview: StateFlow<Boolean> = topicRepository.topicTitleOverview.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = true,
    )

    init {
        load()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.emit(true)
            loadInternal()
            _isRefreshing.emit(false)
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
            e.printStackTrace()
            _newsInfoFlow.emit(NewsUiState.Error(e))
        }
    }

}

@Stable
sealed interface NewsUiState {
    object Loading : NewsUiState
    data class Success(val newsInfo: NewsInfo) : NewsUiState
    data class Error(val throwable: Throwable?) : NewsUiState
}