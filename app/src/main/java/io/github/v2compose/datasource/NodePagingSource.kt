package io.github.v2compose.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.v2compose.network.V2exApi

private const val TAG = "NodePagingSource"

class NodePagingSource(private val nodeId: String, private val v2exApi: V2exApi) :
    PagingSource<Int, Any>() {

    companion object {
        const val FirstPageIndex = 1
    }

    private var pageCount: Int = 0

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
            val page = params.key ?: FirstPageIndex
            val nodeInfo = v2exApi.nodesInfo(node = nodeId, page = page)
            Log.d(TAG, "load, result, nodeTopicInfo = $nodeInfo")
            if (page == FirstPageIndex) {
                pageCount =
                    if (nodeInfo.items.isEmpty()) 0 else nodeInfo.total / nodeInfo.items.size
            }
            val data = nodeInfo.items.toMutableList<Any>().apply {
                if (page == FirstPageIndex) add(0, nodeInfo)
            }
            val prev = if (page == FirstPageIndex) null else page - 1
            val next = if (page < pageCount) page + 1 else null
            LoadResult.Page(
                data = data,
                prevKey = prev,
                nextKey = next,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }

    }
}