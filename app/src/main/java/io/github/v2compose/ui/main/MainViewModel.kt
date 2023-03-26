package io.github.v2compose.ui.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.R
import io.github.v2compose.bean.ProxyInfo
import io.github.v2compose.core.CheckInWorker
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.network.bean.Release
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.ui.BaseViewModel
import io.github.v2compose.usecase.CheckForUpdatesUseCase
import io.github.v2compose.usecase.CheckInUseCase
import io.github.v2compose.usecase.LoadNodesUseCase
import io.github.v2compose.util.WebViewProxy
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val checkForUpdates: CheckForUpdatesUseCase,
    private val checkIn: CheckInUseCase,
    private val appPreferences: AppPreferences,
    private val accountRepository: AccountRepository,
    private val appExecutorService: ExecutorService,
    val loadNodes: LoadNodesUseCase,
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
        listenAutoCheckIn()
        initWebViewProxy()
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
                .distinctUntilChanged()
                .collectLatest {
                    if (it) {
                        checkInInternal()
                    }
                }
        }
    }

    private suspend fun checkInInternal() {
        val result = checkIn()
        if (result.success) {
            result.message?.let { updateSnackbarMessage(it) }
        } else {
            updateSnackbarMessage(
                result.message ?: context.getString(R.string.daily_mission_failure)
            )
        }
    }

    private val autoCheckInWorkName = "autoCheckInWork"

    private fun listenAutoCheckIn() {
        viewModelScope.launch {
            accountRepository.isLoggedIn.combine(
                accountRepository.autoCheckIn,
                transform = { isLoggedIn, autoCheckIn -> isLoggedIn && autoCheckIn })
                .distinctUntilChanged()
                .collectLatest { shouldCheckIn ->
                    if (shouldCheckIn) {
                        //登录成功并且开启自动签到，会尝试自动签到；
                        checkInInternal()
                        //PeriodicWork 在设定的 repeatInterval 内肯定能执行一次，但是不保证具体是什么时候执行；
                        //将周期设置为每12小时，可以确保每一天之内，肯定能执行一次；
                        //在浏览主页的时候，会自动签到；
                        //使用Work仅为了处理某一天没有打开APP的情况，也能自动签到；
                        val checkInWorkRequest =
                            PeriodicWorkRequestBuilder<CheckInWorker>(
                                Duration.ofHours(12),
                                Duration.ofHours(1),
                            ).build()
                        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                            autoCheckInWorkName,
                            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                            checkInWorkRequest
                        )
                    } else {
                        WorkManager.getInstance(context).cancelUniqueWork(autoCheckInWorkName)
                    }
                }
        }
    }

    private fun initWebViewProxy() {
        viewModelScope.launch {
            appPreferences.proxyInfo.collectLatest {
                if (it != ProxyInfo.Default) {
                    WebViewProxy.updateProxy(it, appExecutorService)
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

    fun loadNodes() {
        viewModelScope.launch {
            loadNodes.execute()
        }
    }

}