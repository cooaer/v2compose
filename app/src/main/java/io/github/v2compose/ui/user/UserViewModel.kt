package io.github.v2compose.ui.user

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.R
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.network.bean.UserPageInfo
import io.github.v2compose.repository.TopicRepository
import io.github.v2compose.repository.UserRepository
import io.github.v2compose.ui.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val userRepository: UserRepository,
    private val topicRepository: TopicRepository,
) : BaseViewModel(application) {

    val userArgs = UserArgs(savedStateHandle, stringDecoder)

    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val userUiState = _userUiState.asStateFlow()

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
            _userUiState.emit(UserUiState.Loading)
            try {
                val result = userRepository.getUserPageInfo(userArgs.userName)
                _userUiState.emit(UserUiState.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _userUiState.emit(UserUiState.Error(e))
            }
        }
    }

    fun followUser() {
        doUserAction { it.followUrl }
    }

    fun blockUser() {
        doUserAction { it.blockUrl }
    }

    private fun doUserAction(actionUrl: (UserPageInfo) -> String) {
        if (userUiState.value !is UserUiState.Success) {
            return
        }
        val userPageInfo = (userUiState.value as UserUiState.Success).userPageInfo
        val url = actionUrl(userPageInfo)
        viewModelScope.launch {
            try {
                val result = userRepository.doUserAction(userPageInfo.userName, url)
                _userUiState.emit(UserUiState.Success(result))
//                updateSnackbarMessage(R.string.user_action_success)
            } catch (e: Exception) {
                e.printStackTrace()
                updateSnackbarMessage(e.message ?: context.getString(R.string.user_action_failure))
            }
        }
    }

}

@Stable
sealed interface UserUiState {
    data class Success(val userPageInfo: UserPageInfo) : UserUiState
    object Loading : UserUiState
    data class Error(val error: Throwable) : UserUiState
}