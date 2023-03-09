package io.github.v2compose.usecase

import android.content.Context
import android.net.Uri
import android.util.Log
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Scale
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.cooaer.htmltext.Img
import io.github.cooaer.htmltext.fullUrl
import io.github.v2compose.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject

private const val TAG = "FixedHtmlImageUseCase"

class FixedHtmlImageUseCase @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun loadHtmlImages(html: String, src: String?): Flow<String> {
        return if (src == null) {
            loadImages(html)
        } else {
            loadImage(html, src)
        }
    }

    suspend fun loadImages(html: String): Flow<String> = flow {
        val document = Jsoup.parse(html)
        val loadingImages = document.select("img")
            .associateWith { Img(it) }
            .filter { it.value.width == null || it.value.height == null }
        loadingImages.forEach { (element, img) ->
            element.attr("loadState", "loading")
        }
        emit(document.outerHtml())

        loadingImages.map { (element, img) ->
            val src = img.src.fullUrl(Constants.baseUrl)
            val imageRequest = createImageRequest(src)
            try {
                val result = context.imageLoader.execute(imageRequest)
                Pair(element, result)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair(element, null)
            }
        }.map { (element, result) ->
            fillElement(element, result)
        }

        emit(document.outerHtml())
    }

    private fun createImageRequest(src: String): ImageRequest {
        val srcUri = Uri.parse(src) ?: null
        val screenWidth = context.resources.displayMetrics.widthPixels
        val size = if (srcUri?.lastPathSegment?.endsWith("svg") == true) {
            Size.ORIGINAL
        } else {
            Size(coil.size.Dimension.Undefined, coil.size.Dimension.Undefined)
//            Size(screenWidth, coil.size.Dimension.Undefined)
        }

        return ImageRequest.Builder(context)
            .data(src)
            .size(size)
            .scale(Scale.FIT)
            .build()
    }

    suspend fun loadImage(html: String, src: String): Flow<String> = flow {
        val document = Jsoup.parse(html)
        val loadingImages = document.select("img[src=\"$src\"]")
        loadingImages.forEach { element ->
            element.attr("loadState", "loading")
        }
        emit(document.outerHtml())

        val imageRequest =
            ImageRequest.Builder(context).data(src).size(Size.ORIGINAL).build()
        val imageResult = context.imageLoader.execute(imageRequest)
        loadingImages.forEach {
            fillElement(it, imageResult)
        }
        emit(document.outerHtml())
    }

    private fun fillElement(element: Element, result: ImageResult?) {
        Log.d(
            TAG, "fillElement, src = ${element.attr("src")}, " +
                    "result width = ${result?.drawable?.intrinsicWidth}, " +
                    "resultHeight = ${result?.drawable?.intrinsicHeight}"
        )

        if (result?.drawable != null) {
            val drawable = result.drawable!!
            element.attr("width", drawable.intrinsicWidth.toString())
            element.attr("height", drawable.intrinsicHeight.toString())
            element.attr("loadState", "success")
        } else {
            element.attr("loadState", "error")
        }
    }


}