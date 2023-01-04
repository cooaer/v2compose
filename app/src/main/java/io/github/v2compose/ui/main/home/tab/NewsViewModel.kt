package io.github.v2compose.ui.main.home.tab

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.result.Result
import io.github.v2compose.core.result.asResult
import io.github.v2compose.data.NewsRepository
import io.github.v2compose.network.bean.NewsInfo
import kotlinx.coroutines.flow.*
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

    val newsInfo: StateFlow<NewsUiState> = flow {
        emit(newsRepository.getHomeNews(tab))
    }.asResult().map { result ->
        when (result) {
            is Result.Success -> {
                NewsUiState.Success(result.data)
            }
            is Result.Loading -> {
                NewsUiState.Loading
            }
            is Result.Error -> {
                NewsUiState.Error
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = NewsUiState.Loading
    )


    //TODO: 当销毁时，缓存数据到硬盘
    override fun onCleared() {
        super.onCleared()
    }
}

@Stable
sealed interface NewsUiState {
    data class Success(val newsInfo: NewsInfo) : NewsUiState
    object Loading : NewsUiState
    object Error : NewsUiState
}