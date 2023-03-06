package io.github.v2compose.ui.main.notifications

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.usecase.FixedHtmlImageUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val fixedHtmlImage: FixedHtmlImageUseCase,
) : ViewModel() {

    val isLoggedIn = accountRepository.isLoggedIn
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    val unreadNotifications = accountRepository.unreadNotifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        0
    )

    val notifications = accountRepository.getNotifications().cachedIn(viewModelScope)

    val sizedHtmls = mutableStateMapOf<String, String>()

    fun loadHtmlImage(tag: String, html: String, imageSrc: String?) {
        viewModelScope.launch {
            fixedHtmlImage.loadHtmlImages(html, imageSrc).collectLatest { sizedHtmls[tag] = it }
        }
    }
}