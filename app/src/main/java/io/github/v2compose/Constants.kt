package io.github.v2compose

object Constants {
    const val host = "v2ex.com"
    const val baseUrl = "https://www.v2ex.com/"
    const val userPath = "/member/"

    const val source = "https://github.com/cooaer/V2Compose"

    const val topicTitleOverviewMaxLines = 2

    fun topicUrl(topicId: String) = "${baseUrl}t/$topicId"

    fun userUrl(userName: String) = "${baseUrl}member/$userName"

}