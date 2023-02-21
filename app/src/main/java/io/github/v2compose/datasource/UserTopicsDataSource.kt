package io.github.v2compose.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.v2compose.core.error.VisibilityError
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.UserTopics

class UserTopicsDataSource(private val userName: String, private val v2exService: V2exService) :
    PagingSource<Int, UserTopics.Item>() {

    companion object {
        const val FIRST_PAGE: Int = 1
    }

    override fun getRefreshKey(state: PagingState<Int, UserTopics.Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserTopics.Item> {
        return try {
            val page = params.key ?: FIRST_PAGE
            val userTopics = v2exService.userTopics(userName, page)
            val prevKey = if (page == FIRST_PAGE) null else page - 1
            val nextKey = if (page < userTopics.pageCount) page + 1 else null
            if (userTopics.visibility.isNotEmpty()) {
                LoadResult.Error(VisibilityError(userTopics.visibility))
            } else {
                LoadResult.Page(userTopics.items, prevKey, nextKey)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}