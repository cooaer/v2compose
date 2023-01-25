package io.github.v2compose

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.datasource.AppSettings
import io.github.v2compose.datasource.AppSettingsDataSource
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class V2AppViewModel @Inject constructor(
    private val appSettingsDataSource: AppSettingsDataSource,
) : ViewModel() {

    val appSettings = appSettingsDataSource.appSettings.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        AppSettings.Default
    )

}