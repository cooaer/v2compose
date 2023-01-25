package io.github.v2compose.ui.main.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.network.bean.UserPageInfo
import io.github.v2compose.repository.TopicRepository
import io.github.v2compose.repository.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val userRepository: UserRepository,
    private val topicRepository: TopicRepository,
) : ViewModel() {

    val userArgs = UserArgs(savedStateHandle, stringDecoder)

    private val _userPageInfo = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val userPageInfo = _userPageInfo.asStateFlow()

    val userTopics = userRepository.getUserTopics(userArgs.userName).cachedIn(viewModelScope)

    val userReplies = userRepository.getUserReplies(userArgs.userName).cachedIn(viewModelScope)

    val topicTitleOverview: StateFlow<Boolean> = topicRepository.topicTitleOverview.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        initialValue = true,
    )

    init {
        loadUserPageInfo()
    }

    fun retry() {
        loadUserPageInfo()
    }

    private fun loadUserPageInfo() {
        viewModelScope.launch {
            _userPageInfo.emit(UserUiState.Loading)
            try {
                val result = userRepository.getUserPageInfo(userArgs.userName)
                _userPageInfo.emit(UserUiState.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _userPageInfo.emit(UserUiState.Error(e))
            }
        }
    }

}


sealed interface UserUiState {
    data class Success(val userPageInfo: UserPageInfo) : UserUiState
    object Loading : UserUiState
    data class Error(val error: Throwable) : UserUiState
}