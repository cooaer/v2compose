package io.github.v2compose.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.network.bean.LoginParam
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.usecase.UpdateAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val updateAccount: UpdateAccountUseCase,
) :
    ViewModel() {

    private val _loginParam = MutableStateFlow<LoginParamState>(LoginParamState.Loading)
    val loginParam = _loginParam.asStateFlow()

    private val _login = MutableStateFlow<LoginState>(LoginState.Idle)
    val login = _login.asStateFlow()

    init {
        fetchLoginParam()
    }

    fun fetchLoginParam() {
        viewModelScope.launch {
            _loginParam.emit(LoginParamState.Loading)
            try {
                val result = accountRepository.getLoginParam()
                _loginParam.emit(LoginParamState.Success(result))
            } catch (e: Exception) {
                e.printStackTrace()
                _loginParam.emit(LoginParamState.Error(e))
            }
        }
    }

    fun login(userName: String, password: String, captcha: String) {
        val loginParamState = loginParam.value
        if (loginParamState !is LoginParamState.Success) {
            return
        }
        viewModelScope.launch {
            _login.emit(LoginState.Loading)
            try {
                val loginParams = loginParamState.data.toMap(userName, password, captcha)
                val result = accountRepository.login(loginParams)
                _loginParam.emit(LoginParamState.Success(result))
                _login.emit(LoginState.Idle)
            } catch (e: Exception) {
                e.printStackTrace()
                _login.emit(LoginState.Error(e))
                updateAccount.updateWithException(e, userName)
            }
        }
    }

    fun resetLoginState() {
        viewModelScope.launch {
            _login.emit(LoginState.Idle)
        }
    }

}

sealed interface LoginParamState {
    data class Success(val data: LoginParam) : LoginParamState
    object Loading : LoginParamState
    data class Error(val error: Throwable?) : LoginParamState
}

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Error(val error: Throwable?) : LoginState
}