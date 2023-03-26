package io.github.v2compose.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.RecentTopics

class RecentTopicsPagingSource(private val v2exService: V2exService) :
    PagingSource<Int, RecentTopics.Item>() {

    companion object {
        const val FIRST_PAGE: Int = 1
    }

    private val currentIds = mutableSetOf<String>()

    override fun getRefreshKey(state: PagingState<Int, RecentTopics.Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecentTopics.Item> {
        return try {
            val page = params.key ?: FIRST_PAGE
            val topics = v2exService.recentTopics(page)
            val prevKey = if (page <= FIRST_PAGE) null else page - 1
            val nextKey = if (page < topics.pageCount) page + 1 else null
            val data = topics.items.filterNot { currentIds.contains(it.id) }
            currentIds.addAll(data.map { it.id })
            LoadResult.Page(data, prevKey, nextKey)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}