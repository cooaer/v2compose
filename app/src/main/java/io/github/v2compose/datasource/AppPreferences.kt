package io.github.v2compose.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.v2compose.bean.DarkMode
import io.github.v2compose.network.OkHttpFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "AppSettingsDataSource"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


data class AppSettings(
    val topicRepliesReversed: Boolean = true,
    val openInInternalBrowser: Boolean = true,
    val darkMode: DarkMode = DarkMode.FollowSystem,
    val topicTitleOverview: Boolean = true,
    val ignoredReleaseName: String? = null,
) {
    companion object {
        val Default = AppSettings()
    }
}

data class Account(
    val userName: String = "",
    val userAvatar: String = "",
    val description: String = "",
    val nodes: Int = 0,
    val topics: Int = 0,
    val following: Int = 0,
) {
    companion object {
        val Empty = Account()

        fun fromJson(json: String): Account {
            return OkHttpFactory.gson.fromJson(json, Account::class.java)
        }
    }

    fun toJson(): String {
        return OkHttpFactory.gson.toJson(this)
    }

    fun isValid(): Boolean {
        return userName.isNotEmpty()
    }
}


class AppPreferences @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val KeyTopicRepliesReversed = booleanPreferencesKey("topic_replies_reversed")
        private val KeyOpenInInternalBrowser = booleanPreferencesKey("open_in_internal_browser")
        private val KeyDarkMode = stringPreferencesKey("dark_mode")
        private val KeyTopicTitleOverview = booleanPreferencesKey("topic_title_overview")

        private val KeyIgnoredReleaseName = stringPreferencesKey("ignored_release_name")

        private val KeyAccount = stringPreferencesKey("account")
    }

    val appSettings: Flow<AppSettings> = context.dataStore.data.map {
        AppSettings(
            topicRepliesReversed = it[KeyTopicRepliesReversed] ?: true,
            openInInternalBrowser = it[KeyOpenInInternalBrowser] ?: true,
            darkMode = it[KeyDarkMode]?.let { value -> DarkMode.valueOf(value) }
                ?: DarkMode.FollowSystem,
            topicTitleOverview = it[KeyTopicTitleOverview] ?: true,
            ignoredReleaseName = it[KeyIgnoredReleaseName],
        )
    }.distinctUntilChanged()

    val account: Flow<Account> = context.dataStore.data.map { preferences ->
        with(preferences[KeyAccount]) {
            if (isNullOrEmpty()) Account.Empty else Account.fromJson(this)
        }
    }.distinctUntilChanged()

    suspend fun toggleTopicRepliesOrder() {
        context.dataStore.edit {
            it[KeyTopicRepliesReversed] = !(it[KeyTopicRepliesReversed] ?: true)
        }
    }

    suspend fun openInInternalBrowser(value: Boolean) {
        context.dataStore.edit {
            it[KeyOpenInInternalBrowser] = value
        }
    }

    suspend fun darkMode(value: DarkMode) {
        context.dataStore.edit {
            it[KeyDarkMode] = value.name
        }
    }

    suspend fun topicTitleOverview(value: Boolean) {
        context.dataStore.edit {
            it[KeyTopicTitleOverview] = value
        }
    }

    suspend fun ignoredReleaseName(value: String) {
        context.dataStore.edit {
            it[KeyIgnoredReleaseName] = value
        }
    }

    suspend fun account(value: Account) {
        context.dataStore.edit {
            it[KeyAccount] = value.toJson()
        }
    }

}