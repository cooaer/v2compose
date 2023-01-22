package io.github.v2compose.ui.search

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.network.bean.SoV2EXSearchResultInfo

private const val argsKeyword = "keyword"
private const val searchScreenRoute = "/search?keyword={$argsKeyword}"

data class SearchArgs(val keyword: String?) {
    constructor(savedStateHandle: SavedStateHandle, stringDecoder: StringDecoder) : this(
        savedStateHandle.get<String>(argsKeyword)?.let { stringDecoder.decodeString(it) }
    )
}

fun NavController.navigateToSearch(keyword: String? = null) {
    val encodedKeyword = Uri.encode(keyword) ?: ""
    navigate("/search?keyword=$encodedKeyword")
}

fun NavGraphBuilder.searchScreen(
    goBack: () -> Unit,
    onTopicClick: (SoV2EXSearchResultInfo.Hit) -> Unit
) {
    composable(
        route = searchScreenRoute,
        arguments = listOf(navArgument(argsKeyword) {
            type = NavType.StringType
            nullable = true
        })
    ) {
        SearchScreenRoute(goBack = goBack, onTopicClick = onTopicClick)
    }
}