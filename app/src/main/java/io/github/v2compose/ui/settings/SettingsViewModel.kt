package io.github.v2compose.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.v2compose.bean.DarkMode
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.datasource.AppSettings
import io.github.v2compose.network.bean.Release
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.usecase.CheckForUpdatesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.Cache
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val appPreferences: AppPreferences,
    val checkForUpdates: CheckForUpdatesUseCase,
    private val accountRepository: AccountRepository,
    private val httpCache: Cache,
    private val imageDiskCache: DiskCache,
) : ViewModel() {

    val appSettings: StateFlow<AppSettings> = appPreferences.appSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AppSettings.Default,
        )

    private val _cacheSize = MutableStateFlow(0L)
    val cacheSize = _cacheSize.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = accountRepository.isLoggedIn.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        false,
    )

    init {
        initCacheSize()
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun initCacheSize() {
        viewModelScope.launch {
            val imageCacheSize = imageDiskCache.size.div(1024 * 1024)
            val httpCacheSize = httpCache.size().div(1024 * 1024)
            _cacheSize.emit(imageCacheSize + httpCacheSize)
        }
    }

    fun updateAutoCheckIn(value: Boolean) {
        viewModelScope.launch {
            appPreferences.autoCheckIn(value)
        }
    }

    fun setOpenInInternalBrowser(value: Boolean) {
        viewModelScope.launch {
            appPreferences.openInInternalBrowser(value)
        }
    }

    fun setDarkMode(value: DarkMode) {
        viewModelScope.launch {
            appPreferences.darkMode(value)
        }
    }

    fun setTopicTitleTwoLineMax(value: Boolean) {
        viewModelScope.launch {
            appPreferences.topicTitleOverview(value)
        }
    }

    fun toggleHighlightOpReply(value: Boolean) {
        viewModelScope.launch {
            appPreferences.highlightOpReply(value)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun clearCache() {
        viewModelScope.launch {
            imageDiskCache.clear()
            httpCache.evictAll()
            _cacheSize.emit(0L)
        }
    }

    fun ignoreRelease(release: Release) {
        viewModelScope.launch {
            appPreferences.ignoredReleaseName(release.tagName)
        }
    }

    suspend fun logout() {
        accountRepository.logout()
    }

}