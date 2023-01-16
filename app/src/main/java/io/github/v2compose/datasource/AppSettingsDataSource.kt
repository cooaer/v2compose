package io.github.v2compose.datasource

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

const val TAG = "AppSettingsDataSource"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


data class AppSettings(val topicRepliesReversed: Boolean)

class AppSettingsDataSource @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        private val KEY_REPLIES_ORDER = booleanPreferencesKey("topic_replies_reversed")
    }

    val topicRepliesReversed: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_REPLIES_ORDER] ?: true }

    val appSettings: Flow<AppSettings> = context.dataStore.data.map {
        AppSettings(
            topicRepliesReversed = it[KEY_REPLIES_ORDER] ?: true,
        )
    }

    suspend fun toggleTopicRepliesOrder() {
        context.dataStore.edit { preferences ->
            preferences.also { it[KEY_REPLIES_ORDER] = !(it[KEY_REPLIES_ORDER] ?: true) }
        }
    }

}