package io.github.v2compose.ui.login.google

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.datasource.Account
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GoogleLoginViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    val account = accountRepository.account
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), Account.Empty)

    suspend fun fetchUserInfo() {
        try {
            accountRepository.fetchUserInfo()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}