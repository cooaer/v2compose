package io.github.v2compose

object Constants {

    const val baseUrl = "https://www.v2ex.com/"
    const val userPath = "/member/"

    fun topicUrl(topicId: String) = "${baseUrl}t/$topicId"

    fun userUrl(userName: String) = "${baseUrl}member/$userName"

}