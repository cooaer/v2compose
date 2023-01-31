package io.github.v2compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.datasource.Account
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.datasource.AppSettings
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class V2AppViewModel @Inject constructor(
    private val appPreferences: AppPreferences,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    val appSettings = appPreferences.appSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AppSettings.Default
    )

    val account = accountRepository.account.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        Account.Empty
    )

}