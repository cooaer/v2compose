package io.github.v2compose.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.v2compose.network.V2exService
import io.github.v2compose.network.bean.UserReplies

class UserRepliesDataSource(private val userName: String, private val v2ExService: V2exService) :
    PagingSource<Int, UserReplies.Item>() {

    companion object {
        const val FIRST_PAGE: Int = 1
    }

    override fun getRefreshKey(state: PagingState<Int, UserReplies.Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserReplies.Item> {
        return try {
            val page = params.key ?: FIRST_PAGE
            val userReplies = v2ExService.userReplies(userName, page)
            val prevKey = if (page == FIRST_PAGE) null else page - 1
            val nextKey = if (page < userReplies.pageCount) page + 1 else null
            LoadResult.Page(userReplies.items, prevKey, nextKey)
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}