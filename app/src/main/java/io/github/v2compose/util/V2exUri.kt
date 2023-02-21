package io.github.v2compose.util

import io.github.v2compose.Constants

object V2exUri {

    fun topicUrl(topicId: String) = "${Constants.baseUrl}/t/$topicId"

}