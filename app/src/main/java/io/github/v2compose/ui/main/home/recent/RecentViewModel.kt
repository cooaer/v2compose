package io.github.v2compose.ui.main.home.recent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.repository.NewsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RecentViewModel @Inject constructor(
    newsRepository: NewsRepository,
    appPreferences: AppPreferences
) : ViewModel() {

    val recentTopics = newsRepository.recentTopics.cachedIn(viewModelScope)
    val topicTitleOverview = appPreferences.appSettings.map { it.topicTitleOverview }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), true)

}