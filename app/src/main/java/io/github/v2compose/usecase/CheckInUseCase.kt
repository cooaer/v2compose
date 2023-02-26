package io.github.v2compose.usecase

import io.github.v2compose.core.extension.isRedirect
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.util.V2exUri
import javax.inject.Inject

class CheckInUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
) {

    //状态变化后的自动签到、点击签到按钮、后台自动签到
    suspend operator fun invoke(): CheckInResult {
        return try {
            var dailyInfo = accountRepository.dailyInfo()
            if (!dailyInfo.hadCheckedIn()) {
                dailyInfo = accountRepository.checkIn(dailyInfo.once())
            }
            CheckInResult(dailyInfo.hadCheckedIn(), dailyInfo.continuousLoginDays)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.isRedirect(V2exUri.missionDailyPath)) {
                val dailyInfo = accountRepository.dailyInfo()
                CheckInResult(dailyInfo.hadCheckedIn(), dailyInfo.continuousLoginDays)
            } else {
                CheckInResult(false, e.message)
            }
        }
    }

}

data class CheckInResult(val success: Boolean, val message: String?)