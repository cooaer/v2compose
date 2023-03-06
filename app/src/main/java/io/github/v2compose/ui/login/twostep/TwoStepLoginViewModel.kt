package io.github.v2compose.ui.login.twostep

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.network.bean.TwoStepLoginInfo
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.usecase.UpdateAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TwoStepLoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val updateAccount: UpdateAccountUseCase,
) : ViewModel() {

    private val _twoStepLoginUiState =
        MutableStateFlow<TwoStepLoginUiState>(TwoStepLoginUiState.Loading)
    val twoStepLoginUiState = _twoStepLoginUiState.asStateFlow()

    private val _login = MutableStateFlow<LoginState>(LoginState.Idle)
    val login = _login.asStateFlow()

    init {
        fetchTwoStepLoginInfo()
    }

    fun fetchTwoStepLoginInfo() {
        viewModelScope.launch {
            _twoStepLoginUiState.emit(TwoStepLoginUiState.Loading)
            try {
                val result = accountRepository.getTwoStepLoginInfo()
                _twoStepLoginUiState.emit(TwoStepLoginUiState.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _twoStepLoginUiState.emit(TwoStepLoginUiState.Error(e))
            }
        }
    }

    fun loginNextStep(code: String) {
        val uiState = _twoStepLoginUiState.value
        if (uiState !is TwoStepLoginUiState.Success) {
            return
        }
        viewModelScope.launch {
            _login.emit(LoginState.Loading)
            try {
                val result = accountRepository.loginNextStep(uiState.twoStepLoginInfo.once, code)
                _login.emit(LoginState.Idle)
                _twoStepLoginUiState.emit(TwoStepLoginUiState.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _login.emit(LoginState.Error(e))
            }
        }
    }

    fun resetLoginState() {
        viewModelScope.launch {
            _login.emit(LoginState.Idle)
        }
    }

}

@Stable
sealed interface TwoStepLoginUiState {
    data class Success(val twoStepLoginInfo: TwoStepLoginInfo) : TwoStepLoginUiState
    object Loading : TwoStepLoginUiState
    data class Error(val error: Throwable) : TwoStepLoginUiState
}

@Stable
sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Error(val error: Throwable?) : LoginState
}