package io.github.v2compose.ui.main.mine

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.R
import io.github.v2compose.bean.Account
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.ui.BaseViewModel
import io.github.v2compose.usecase.CheckInUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(
    application: Application,
    private val checkIn: CheckInUseCase,
    private val accountRepository: AccountRepository,
) : BaseViewModel(application) {

    val account: StateFlow<Account> = accountRepository.account
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            Account.Empty
        )

    val hasCheckingInTips: StateFlow<Boolean> = accountRepository.hasCheckingInTips
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), false)

    val lastCheckInTime: StateFlow<Long> = accountRepository.lastCheckInTime
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0L)

    private val _checkingIn = MutableStateFlow(false)
    val checkingIn = _checkingIn.asStateFlow()

    init {
        refreshAccount()
    }

    private fun refreshAccount() {
        viewModelScope.launch {
            account.map { it.userName }
                .distinctUntilChanged()
                .collectLatest {
                    if (it.isNotEmpty()) {
                        try {
                            accountRepository.refreshAccount()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
        }
    }

    //日常任务：领取每日登录奖励
    fun doCheckIn() {
        viewModelScope.launch {
            _checkingIn.emit(true)
            val result = checkIn()
            if (result.success) {
                result.message?.let { updateSnackbarMessage(it) }
            } else {
                updateSnackbarMessage(
                    result.message ?: context.getString(R.string.daily_mission_failure)
                )
            }
            _checkingIn.emit(false)
        }
    }

}

