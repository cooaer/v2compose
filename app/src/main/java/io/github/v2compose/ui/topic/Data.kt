package io.github.v2compose.ui.topic

import io.github.v2compose.network.bean.TopicInfo

data class TopicInfoWrapper(
    val topic: TopicInfo? = null,
    val favorited: Boolean? = null,
    val thanked: Boolean? = null,
    val ignored: Boolean? = null,
    val reported: Boolean? = null,
) {

    val favoriteCount: Int
        get()  {
            val innerCount = topic?.headerInfo?.favoriteCount ?: 0
            val innerFavorited = topic?.headerInfo?.hadFavorited()
            if(favorited == true && innerFavorited == false){
                return innerCount + 1
            }else if(favorited == false && innerFavorited == true){
                return innerCount - 1
            }
            return innerCount
        }

    val isFavorited: Boolean
        get() = favorited ?: topic?.headerInfo?.hadFavorited() ?: false

    val isThanked: Boolean
        get() = thanked ?: topic?.headerInfo?.hadThanked() ?: false

    val isIgnored: Boolean
        get() = ignored ?: topic?.headerInfo?.hadIgnored() ?: false

    val isReported: Boolean
        get() = reported ?: topic?.hasReported() ?: false
}

data class ReplyWrapper(
    val reply: TopicInfo.Reply,
    val thanked: Boolean = false,
    val ignored: Boolean = false,
)