package io.github.v2compose.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.v2compose.bean.Account
import io.github.v2compose.bean.DraftTopic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "AccountSettingsDataSource"

private val Context.accountDataStore: DataStore<Preferences> by preferencesDataStore(name = "account")

class AccountPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
    private val moshi: Moshi,
) {

    companion object {
        private val KeyAccount = stringPreferencesKey("account")
        private val KeyDraftTopic = stringPreferencesKey("draft_topic")

        private val KeyUnreadNotificationsCount = intPreferencesKey("unread_notifications_count")
        private val KeyLastCheckInTime = longPreferencesKey("last_check_in_time")
    }

    val account: Flow<Account> = context.accountDataStore.data.map { preferences ->
        preferences[KeyAccount].let {
            if (it.isNullOrEmpty()) Account.Empty else Account.fromJson(moshi, it)
        }
    }.distinctUntilChanged()

    val draftTopic: Flow<DraftTopic> = context.accountDataStore.data.map { preferences ->
        preferences[KeyDraftTopic].let {
            if (it.isNullOrEmpty()) DraftTopic.Empty else DraftTopic.fromJson(moshi, it)
        }
    }

    val unreadNotifications = context.accountDataStore.data.map {
        it[KeyUnreadNotificationsCount] ?: 0
    }

    val lastCheckInTime = context.accountDataStore.data.map {
        it[KeyLastCheckInTime] ?: 0L
    }

    suspend fun account(value: Account) {
        context.accountDataStore.edit {
            it[KeyAccount] = value.toJson(moshi)
        }
    }

    suspend fun updateAccount(
        userName: String? = null,
        userAvatar: String? = null,
        description: String? = null,
        nodes: Int? = null,
        topics: Int? = null,
        following: Int? = null,
    ) {
        val current = account.first()
        account(
            current.copy(
                userName = userName ?: current.userName,
                userAvatar = userAvatar ?: current.userAvatar,
                description = description ?: current.description,
                nodes = nodes ?: current.nodes,
                topics = topics ?: current.topics,
                following = following ?: current.following,
            )
        )
    }

    suspend fun draftTopic(value: DraftTopic) {
        context.accountDataStore.edit {
            it[KeyDraftTopic] = value.toJson(moshi)
        }
    }

    suspend fun unreadNotifications(value: Int) {
        context.accountDataStore.edit {
            it[KeyUnreadNotificationsCount] = value
        }
    }

    suspend fun lastCheckInTime(value: Long) {
        context.accountDataStore.edit {
            it[KeyLastCheckInTime] = value
        }
    }

    suspend fun clear() {
        context.accountDataStore.edit {
            it.clear()
        }
    }


}