package io.github.v2compose.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val topicRepository: TopicRepository,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val searchArgs = SearchArgs(savedStateHandle, stringDecoder)

    private val _keyword = MutableStateFlow(searchArgs.keyword)
    val keyword: StateFlow<String?> = _keyword.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val historyKeywords = appPreferences.appSettings.mapLatest { it.searchKeywords }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    @OptIn(ExperimentalCoroutinesApi::class)
    val topics = keyword.filterNot { it.isNullOrEmpty() }
        .flatMapLatest {
            topicRepository.search(it!!)
        }
        .cachedIn(viewModelScope)

    fun search(value: String) {
        viewModelScope.launch {
            _keyword.emit(value)
            val searchKeywords = historyKeywords.value.toMutableList()
                .also {
                    it.remove(value)
                    it.add(0, value)
                    if(it.size > 10){
                        it.removeLast()
                    }
                }
            appPreferences.searchKeywords(searchKeywords)
        }
    }

    fun clearHistoryKeywords(){
        viewModelScope.launch{
            appPreferences.searchKeywords(listOf())
        }
    }
}