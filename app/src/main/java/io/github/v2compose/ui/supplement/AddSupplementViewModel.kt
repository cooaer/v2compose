package io.github.v2compose.ui.supplement

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.bean.ContentFormat
import io.github.v2compose.core.extension.isRedirect
import io.github.v2compose.core.extension.redirectLocation
import io.github.v2compose.datasource.AccountPreferences
import io.github.v2compose.network.bean.AppendTopicPageInfo
import io.github.v2compose.repository.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AddSupplementViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val topicRepository: TopicRepository,
    private val accountPreferences: AccountPreferences,
) : ViewModel() {

    val args = AddSupplementArgs(savedStateHandle)

    val draftSupplement: String
        get() = runBlocking { accountPreferences.draftSupplement.first() }

    private val _pageInfo = MutableStateFlow<AppendTopicPageInfo?>(null)
    val pageInfo: StateFlow<AppendTopicPageInfo?> = _pageInfo

    private val _addSupplementState = MutableStateFlow<AddSupplementState>(AddSupplementState.Idle)
    val addSupplementState: StateFlow<AddSupplementState> = _addSupplementState

    fun updateDraftSupplement(value: String) {
        viewModelScope.launch {
            accountPreferences.draftSupplement(value)
        }
    }

    fun addSupplement(text: String, contentFormat: ContentFormat) {
        viewModelScope.launch {
            _addSupplementState.emit(AddSupplementState.Loading)
            var once = _pageInfo.value?.once
            if (once.isNullOrEmpty()) {
                try {
                    val newPageInfo = topicRepository.getAppendTopicPageInfo(args.topicId)
                    _pageInfo.emit(newPageInfo)
                    once = newPageInfo.once
                } catch (e: Exception) {
                    e.printStackTrace()
                    _addSupplementState.emit(AddSupplementState.Error(e))
                }
            }
            try {
                val newPageInfo =
                    topicRepository.addSupplement(args.topicId, text, contentFormat, once!!)
                _pageInfo.emit(newPageInfo)
                _addSupplementState.emit(AddSupplementState.Error(null))
            } catch (e: Exception) {
                e.printStackTrace()
                if (e.isRedirect) {
                    _addSupplementState.emit(AddSupplementState.Success(e.redirectLocation))
                } else {
                    _addSupplementState.emit(AddSupplementState.Error(e))
                }
            }
        }
    }

}

@Stable
sealed interface AddSupplementState {
    object Idle : AddSupplementState
    object Loading : AddSupplementState
    data class Success(val redirect: String?) : AddSupplementState
    data class Error(val error: Throwable?) : AddSupplementState
}