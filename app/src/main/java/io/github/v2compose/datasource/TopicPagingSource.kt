package io.github.v2compose.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.v2compose.network.V2exApi

class TopicPagingSource constructor(
    private val v2exApi: V2exApi,
    private val topicId: String,
    private val reversed: Boolean
) : PagingSource<Int, Any>() {
    companion object {
        private const val startPageReversed = 9999
    }

    private var startPage = if (reversed) startPageReversed else 1

    override fun getRefreshKey(state: PagingState<Int, Any>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {
        return try {
            var page = params.key ?: startPage
            val topicInfo = v2exApi.topicDetails(topicId, page)
            if (page == startPageReversed) {
                startPage = topicInfo.totalPage
                page = startPage
            }
            val prevPage = if (page == startPage) null else page + (if (reversed) 1 else -1)
            val nextPage = if (reversed) {
                if (page <= 0) null else page - 1
            } else {
                if (page < topicInfo.totalPage) page + 1 else null
            }
            val data: List<Any> = mutableListOf<Any>().apply {
                if (page == startPage) {
                    add(topicInfo)
                }
                addAll(if (reversed) topicInfo.replies.reversed() else topicInfo.replies)
            }
            LoadResult.Page(
                data,
                prevPage,
                nextPage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }


}