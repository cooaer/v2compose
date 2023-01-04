package io.github.v2compose.ui.main.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val TAB_NAMES =
        arrayOf("全部", "最热", "技术", "创意", "好玩", "Apple", "酷工作", "交易", "城市", "问与答", "R2", "节点", "关注")
    private val TAB_VALUES = arrayOf(
        "all",
        "hot",
        "tech",
        "creative",
        "play",
        "apple",
        "jobs",
        "deals",
        "city",
        "qna",
        "r2",
        "nodes",
        "members"
    )
    val newsTabInfos =
        TAB_NAMES.mapIndexed { index, title -> NewsTabInfo(title, TAB_VALUES[index]) }

}

data class NewsTabInfo(val name: String, val value: String)