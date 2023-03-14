package io.github.v2compose.bean

data class AppSettings(
    val topicRepliesReversed: Boolean = true,
    val openInInternalBrowser: Boolean = true,
    val darkMode: DarkMode = DarkMode.FollowSystem,
    val topicTitleOverview: Boolean = true,
    val ignoredReleaseName: String? = null,
    val autoCheckIn: Boolean = false,
    val searchKeywords: List<String> = listOf(),
    val highlightOpReply: Boolean = false,
    val replyWithFloor: Boolean = true,
) {
    companion object {
        val Default = AppSettings()
    }
}