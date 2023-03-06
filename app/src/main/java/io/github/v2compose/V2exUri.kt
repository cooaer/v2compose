package io.github.v2compose

object V2exUri {

    const val myTopicsUrl= "https://v2ex.com/my/topics"
    const val myNodesUrl = "https://v2ex.com/my/nodes"
    const val myFollowingUrl = "https://v2ex.com/my/following"

    const val missionDailyPath = "/mission/daily"

    fun topicUrl(topicId: String) = "${Constants.baseUrl}/t/$topicId"

    fun userUrl(userName: String) = Constants.baseUrl + userPath(userName)

    fun nodeUrl(nodeId: String) = Constants.baseUrl + nodePath(nodeId)

    fun topicPath(topicId: String) = "/t/$topicId"

    fun nodePath(nodeId: String) = "/go/$nodeId"

    fun userPath(userName: String) = "/member/$userName"

}