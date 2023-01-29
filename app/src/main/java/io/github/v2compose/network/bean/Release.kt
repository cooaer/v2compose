package io.github.v2compose.network.bean

data class Release(
    val id: Int,
    val name: String?,
    val body: String?,
    val tagName: String,
    val htmlUrl: String,
) {
    companion object {
        val Empty = Release(0, "", "", "", "")

        fun fromMap(map:Map<String, Any?>):Release{
            return Release(
                id = map["id"] as Int,
                name = map["name"] as String?,
                body = map["body"] as String?,
                tagName = map["tagName"] as String,
                htmlUrl = map["htmlUrl"] as String,
            )
        }
    }

    fun isValid(): Boolean {
        return id > 0 && tagName.isNotEmpty() && htmlUrl.isNotEmpty()
    }

    fun toMap():Map<String, Any?>{
        return mapOf(
            "id" to id,
            "name" to name,
            "body" to body,
            "tagName" to tagName,
            "htmlUrl" to htmlUrl,
        )
    }
}

data class ReleaseAsset(
    val id: Int,
    val name: String,
    val contentType: String,
    val size: Int,
    val downloadCount: Int,
    val browserDownloadUrl: String,
)