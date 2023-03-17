package io.github.v2compose.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.v2compose.network.V2exService

private const val TAG = "TopicPagingSource"

class TopicPagingSource constructor(
    private val v2exService: V2exService,
    private val topicId: String,
    private val reversed: Boolean
) : PagingSource<Int, Any>() {
    companion object {
        private const val firstPageIndex = 1
        private const val startPageReversed = 999
    }

    private var startPage = if (reversed) startPageReversed else 1

    override fun getRefreshKey(state: PagingState<Int, Any>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {
        return try {
            var page = params.key ?: startPage
            Log.d(TAG, "load, page = $page")
            val topicInfo = v2exService.topicDetails(topicId, page)
            if (page == startPageReversed) {
                startPage = topicInfo.totalPage
                page = startPage
            }

            val largerPage = if (page < topicInfo.totalPage) page + 1 else null
            val smallerPage = if (page <= firstPageIndex) null else page - 1
            val prevPage = if (reversed) largerPage else smallerPage
            val nextPage = if (reversed) smallerPage else largerPage

            val data: List<Any> = mutableListOf<Any>().apply {
                add(topicInfo)
                addAll(if (reversed) topicInfo.replies.reversed() else topicInfo.replies)
            }
            LoadResult.Page(data, prevPage, nextPage)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }


}