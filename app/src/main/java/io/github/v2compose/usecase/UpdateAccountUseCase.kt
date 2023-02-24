package io.github.v2compose.usecase

import android.net.Uri
import io.github.v2compose.datasource.AccountPreferences
import io.github.v2compose.network.OkHttpFactory
import io.github.v2compose.network.bean.LoginResultInfo
import io.github.v2compose.network.bean.NewsInfo
import io.github.v2compose.repository.AccountRepository
import kotlinx.coroutines.flow.first
import retrofit2.HttpException
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(
    private val accountPreferences: AccountPreferences,
    private val accountRepository: AccountRepository
) {

    suspend fun updateWithNewsInfo(newsInfo: NewsInfo) {
        if (!accountRepository.isLoggedIn.first()) {
            return
        }
        val loginResultInfo: LoginResultInfo? =
            OkHttpFactory.fruit.fromHtml(newsInfo.rawResponse, LoginResultInfo::class.java)
        if (loginResultInfo == null || !loginResultInfo.isValid) {
            return
        }
        accountPreferences.updateAccount(
            userName = loginResultInfo.userName,
            userAvatar = loginResultInfo.avatar,
        )
    }

    suspend fun updateWithException(e: Exception, userName: String) {
        if (e !is HttpException) return
        val resp = e.response()?.raw() ?: return
        if (!resp.isRedirect) return
        val location = resp.header("location") ?: return
        val uri = Uri.parse(location) ?: return
        if (uri.path == "/") {
            accountPreferences.updateAccount(userName = userName)
        }
    }

}