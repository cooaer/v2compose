package io.github.v2compose.ui.main.mine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.datasource.Account
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MineViewModel @Inject constructor(private val accountRepository: AccountRepository) :
    ViewModel() {

    val account: StateFlow<Account> = accountRepository.account
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            Account.Empty
        )

    init {
        refreshAccount()
    }

    private fun refreshAccount() {
        if (!account.value.isValid()) {
            return
        }
        viewModelScope.launch {
            accountRepository.refreshAccount()
        }
    }

    //日常任务：领取每日登录奖励
    fun completeDailyMission() {
        viewModelScope.launch {

        }
    }

}

//sealed interface