package io.github.v2compose.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.network.bean.Release
import io.github.v2compose.usecase.CheckForUpdatesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val checkForUpdates: CheckForUpdatesUseCase,
    private val appPreferences: AppPreferences,
) :
    ViewModel() {

    private val _newRelease = MutableStateFlow(Release.Empty)
    val newRelease = _newRelease.asStateFlow()

    init {
        autoCheckForUpdates()
    }

    private fun autoCheckForUpdates() {
        viewModelScope.launch {
            val release = checkForUpdates.invoke()
            _newRelease.emit(release)
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