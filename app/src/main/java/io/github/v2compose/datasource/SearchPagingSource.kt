package io.github.v2compose.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.github.v2compose.network.V2exApi
import io.github.v2compose.network.bean.SoV2EXSearchResultInfo
import kotlinx.coroutines.delay

class SearchPagingSource(private val keyword: String, private val v2exApi: V2exApi) :
    PagingSource<Int, SoV2EXSearchResultInfo.Hit>() {

    override fun getRefreshKey(state: PagingState<Int, SoV2EXSearchResultInfo.Hit>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SoV2EXSearchResultInfo.Hit> {
        return try {
            val from = params.key ?: 0
            val loadSize = params.loadSize
            val resultInfo = v2exApi.search(keyword = keyword, from = from, size = loadSize)
            val prevKey = if (from == 0) null else from - loadSize
            val nextKey = if (from + loadSize < resultInfo.total) from + loadSize else null
            delay(1000)
            return LoadResult.Page(
                data = resultInfo.hits,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}