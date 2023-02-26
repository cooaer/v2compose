package io.github.v2compose.util

import io.github.v2compose.Constants

object V2exUri {

    const val missionDailyPath = "/mission/daily"

    fun topicUrl(topicId: String) = "${Constants.baseUrl}/t/$topicId"

    fun userUrl(userName: String) = Constants.baseUrl + userPath(userName)

    fun userPath(userName: String) = "/member/$userName"

    fun nodeUrl(nodeId: String) = Constants.baseUrl + nodePath(nodeId)

    fun nodePath(nodeId: String) = "/go/$nodeId"

}