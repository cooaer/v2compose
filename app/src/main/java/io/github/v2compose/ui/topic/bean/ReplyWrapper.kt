package io.github.v2compose.ui.topic.bean

import io.github.v2compose.network.bean.TopicInfo

data class ReplyWrapper(
    val reply: TopicInfo.Reply,
    val thanked: Boolean? = null,
    val ignored: Boolean? = null,
)