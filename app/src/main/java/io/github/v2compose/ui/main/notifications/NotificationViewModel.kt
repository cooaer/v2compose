package io.github.v2compose.ui.main.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
) : ViewModel() {

    val unreadNotifications = accountRepository.unreadNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        0
    )

    val notifications = accountRepository.getNotifications().cachedIn(viewModelScope)


}