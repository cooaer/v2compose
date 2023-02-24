package io.github.v2compose.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.R
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.network.bean.Release
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.ui.BaseViewModel
import io.github.v2compose.usecase.CheckForUpdatesUseCase
import io.github.v2compose.usecase.CheckInUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val checkForUpdates: CheckForUpdatesUseCase,
    private val checkIn: CheckInUseCase,
    private val appPreferences: AppPreferences,
    private val accountRepository: AccountRepository,
) : BaseViewModel(application) {

    private val _newRelease = MutableStateFlow(Release.Empty)
    val newRelease = _newRelease.asStateFlow()

    val unreadNotifications = accountRepository.unreadNotifications
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = 0
        )

    init {
        autoCheckForUpdates()
        listenCanCheckIn()
    }

    private fun autoCheckForUpdates() {
        viewModelScope.launch {
            val release = checkForUpdates.invoke()
            _newRelease.emit(release)
        }
    }

    private fun listenCanCheckIn() {
        viewModelScope.launch {
            accountRepository.hasCheckingInTips
                .combine(
                    accountRepository.autoCheckIn,
                    transform = { hasCheckingInTips, autoCheckIn -> hasCheckingInTips && autoCheckIn })
                .collectLatest {
                    if (it) {
                        val result = checkIn()
                        if (result.success) {
                            result.message?.let { updateSnackbarMessage(it) }
                        } else {
                            updateSnackbarMessage(
                                result.message ?: context.getString(R.string.daily_mission_failure)
                            )
                        }
                    }
                }
        }
    }

    fun resetNewRelease() {
        viewModelScope.launch {
            _newRelease.emit(Release.Empty)
        }
    }

    fun ignoreRelease(release: Release) {
        viewModelScope.launch {
            appPreferences.ignoredReleaseName(release.tagName)
        }
    }

}