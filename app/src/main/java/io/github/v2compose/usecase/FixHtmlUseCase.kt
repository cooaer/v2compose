package io.github.v2compose.usecase

import android.content.Context
import android.util.Log
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.ImageResult
import coil.size.Scale
import coil.size.Size
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.cooaer.htmltext.fullUrl
import io.github.v2compose.Constants
import io.github.v2compose.util.CfEmailUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import javax.inject.Inject
import kotlin.math.ceil

private const val TAG = "FixedHtmlImageUseCase"

class FixHtmlUseCase @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        const val LoadImagesCountEveryTime = 4
    }

    private val floorRegex = "(^|\\s+)(#\\d+)($|\\s+)".toRegex()

    suspend fun loadHtmlImages(html: String, src: String?): Flow<String> {
        return if (src == null) {
            loadImages(html)
        } else {
            loadImage(html, src)
        }
    }

    suspend fun loadImages(html: String): Flow<String> = flow {
        val document = withContext(Dispatchers.Default) { initialHtml(html, true) }

        //将html中所有的img的加载状态改为loading
        val loadingImages = document.select("img")
            .filter { element ->
                val width = element.attr("width")
                val height = element.attr("height")
                width.isEmpty() && height.isEmpty()
            }
        loadingImages.forEach { element ->
            element.attr("loadState", "loading")
        }
        emit(document.outerHtml())

        //每次加载四张图片
        val loadTimes = ceil(1f * loadingImages.size / LoadImagesCountEveryTime).toInt()
        (0 until loadTimes).forEach { index ->
            val toIndex = minOf((index + 1) * LoadImagesCountEveryTime, loadingImages.size)
            val images = loadingImages.subList(index * LoadImagesCountEveryTime, toIndex)
            images.map { element ->
                val src = element.attr("src").fullUrl(Constants.baseUrl)
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
    }

    private fun createImageRequest(src: String): ImageRequest {
//        val srcUri = Uri.parse(src) ?: null
//        val screenWidth = context.resources.displayMetrics.widthPixels
//        val size = if (srcUri?.lastPathSegment?.endsWith("svg") == true) {
//            Size.ORIGINAL
//        } else {
//            Size(coil.size.Dimension.Undefined, coil.size.Dimension.Undefined)
////            Size(screenWidth, coil.size.Dimension.Undefined)
//        }

        return ImageRequest.Builder(context)
            .data(src)
            .size(Size.ORIGINAL)
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

    private fun initialHtml(content: String, linkFloor: Boolean): Document {
        //替换html所有的符合规则的楼层为可点击的链接
        val newContent = if (linkFloor) {
            floorRegex.replace(content) { matchResult ->
                val start = matchResult.groupValues[1]
                val floor = matchResult.groupValues[2]
                val end = matchResult.groupValues[3]
                "$start<a href=\"$floor\">$floor</a>$end"
            }
        } else content

        val document = Jsoup.parse(newContent)

        //解密cloudflare对邮件名称的加密
        val encodedEmails: List<Element> = document.select(".__cf_email__")
        if (encodedEmails.isNotEmpty()) {
            encodedEmails.forEach { CfEmailUtils.fixEmailProtected(it) }
        }

        return document
    }

}