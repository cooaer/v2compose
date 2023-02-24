package io.github.v2compose.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.v2compose.bean.DarkMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "AppSettingsDataSource"

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

data class AppSettings(
    val topicRepliesReversed: Boolean = true,
    val openInInternalBrowser: Boolean = true,
    val darkMode: DarkMode = DarkMode.FollowSystem,
    val topicTitleOverview: Boolean = true,
    val ignoredReleaseName: String? = null,
    val autoCheckIn: Boolean = false,
) {
    companion object {
        val Default = AppSettings()
    }
}

class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
) {

    companion object {
        private val KeyTopicRepliesReversed = booleanPreferencesKey("topic_replies_reversed")
        private val KeyOpenInInternalBrowser = booleanPreferencesKey("open_in_internal_browser")
        private val KeyDarkMode = stringPreferencesKey("dark_mode")
        private val KeyTopicTitleOverview = booleanPreferencesKey("topic_title_overview")
        private val KeyIgnoredReleaseName = stringPreferencesKey("ignored_release_name")
        private val KeyAutoCheckIn = booleanPreferencesKey("auto_check_in")
    }

    val appSettings: Flow<AppSettings> = context.appDataStore.data.map {
        AppSettings(
            topicRepliesReversed = it[KeyTopicRepliesReversed] ?: true,
            openInInternalBrowser = it[KeyOpenInInternalBrowser] ?: true,
            darkMode = it[KeyDarkMode]?.let { value -> DarkMode.valueOf(value) }
                ?: DarkMode.FollowSystem,
            topicTitleOverview = it[KeyTopicTitleOverview] ?: true,
            ignoredReleaseName = it[KeyIgnoredReleaseName],
            autoCheckIn = it[KeyAutoCheckIn] ?: false,
        )
    }.distinctUntilChanged()

    suspend fun toggleTopicRepliesOrder() {
        context.appDataStore.edit {
            it[KeyTopicRepliesReversed] = !(it[KeyTopicRepliesReversed] ?: true)
        }
    }

    suspend fun openInInternalBrowser(value: Boolean) {
        context.appDataStore.edit {
            it[KeyOpenInInternalBrowser] = value
        }
    }

    suspend fun darkMode(value: DarkMode) {
        context.appDataStore.edit {
            it[KeyDarkMode] = value.name
        }
    }

    suspend fun topicTitleOverview(value: Boolean) {
        context.appDataStore.edit {
            it[KeyTopicTitleOverview] = value
        }
    }

    suspend fun ignoredReleaseName(value: String) {
        context.appDataStore.edit {
            it[KeyIgnoredReleaseName] = value
        }
    }

    suspend fun autoCheckIn(value: Boolean) {
        context.appDataStore.edit {
            it[KeyAutoCheckIn] = value
        }
    }
}