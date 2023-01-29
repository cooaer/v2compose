package io.github.v2compose.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.v2compose.bean.DarkMode
import io.github.v2compose.datasource.AppSettings
import io.github.v2compose.datasource.AppSettingsDataSource
import io.github.v2compose.network.bean.Release
import io.github.v2compose.usecase.CheckForUpdatesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val appSettingsDataSource: AppSettingsDataSource,
    val checkForUpdates: CheckForUpdatesUseCase,
) : ViewModel() {

    private val imageDiskCache: DiskCache? = Coil.imageLoader(context).diskCache

    val appSettings: StateFlow<AppSettings> = appSettingsDataSource.appSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AppSettings.Default,
        )

    private val _cacheSize = MutableStateFlow(0L)
    val cacheSize = _cacheSize.asStateFlow()

    init {
        initCacheSize()
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun initCacheSize() {
        viewModelScope.launch {
            val imageSizeMB = imageDiskCache?.size?.div(1024 * 1024)
            _cacheSize.emit(imageSizeMB ?: 0L)
        }
    }

    fun setOpenInInternalBrowser(value: Boolean) {
        viewModelScope.launch {
            appSettingsDataSource.openInInternalBrowser(value)
        }
    }

    fun setDarkMode(value: DarkMode) {
        viewModelScope.launch {
            appSettingsDataSource.darkMode(value)
        }
    }

    fun setTopicTitleTwoLineMax(value: Boolean) {
        viewModelScope.launch {
            appSettingsDataSource.topicTitleOverview(value)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun clearCache() {
        viewModelScope.launch {
            imageDiskCache?.clear()
            _cacheSize.emit(0L)
        }
    }

    fun ignoreRelease(release: Release) {
        viewModelScope.launch {
            appSettingsDataSource.ignoredReleaseName(release.tagName)
        }
    }

}