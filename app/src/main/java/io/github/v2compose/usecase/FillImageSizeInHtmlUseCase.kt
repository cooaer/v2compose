package io.github.v2compose.usecase

import android.content.Context
import coil.ImageLoader
import coil.request.ImageRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.jsoup.nodes.Document
import javax.inject.Inject

class FillImageSizeUseCase @Inject constructor(
    private val imageLoader: ImageLoader,
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(document: Document): Document = coroutineScope {
        val requests = document.select("img").toList()
            .filter {
                it.attr("width").isNullOrEmpty() || it.attr("height").isNullOrEmpty()
            }
            .map { ele ->
                ImageRequest.Builder(context).apply {
                    data(ele.attr("src"))
                    target {
                        ele.attr("width", it.intrinsicWidth.toString())
                        ele.attr("height", it.intrinsicHeight.toString())
                    }
                }.build().let {
                    async { imageLoader.execute(it) }
                }
            }
        requests.awaitAll()
        document
    }

}