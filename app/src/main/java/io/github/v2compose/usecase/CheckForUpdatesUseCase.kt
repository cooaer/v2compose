package io.github.v2compose.usecase

import io.github.v2compose.BuildConfig
import io.github.v2compose.core.extension.newerThan
import io.github.v2compose.core.extension.toAppVersion
import io.github.v2compose.datasource.AppPreferences
import io.github.v2compose.network.bean.Release
import io.github.v2compose.repository.AppRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckForUpdatesUseCase @Inject constructor(
    private val appRepository: AppRepository,
    private val appPreferences: AppPreferences,
) {

    suspend operator fun invoke(): Release {
        val release = try {
            appRepository.getAppLatestRelease()
        } catch (e: Exception) {
            e.printStackTrace()
            return Release.Empty
        }
        val appSettings = appPreferences.appSettings.first()
        if (appSettings.ignoredReleaseName == release.tagName) {
            return Release.Empty
        }
        if (release.tagName.toAppVersion().newerThan(BuildConfig.VERSION_NAME.toAppVersion())) {
            return release
        }
        return Release.Empty
    }

}