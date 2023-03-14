package io.github.v2compose.ui.main.mine.following

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import io.github.v2compose.R
import io.github.v2compose.network.bean.MyFollowingInfo
import io.github.v2compose.ui.common.*

private const val TAG = "MyTopicsScreen"

@Composable
fun MyFollowingScreenRoute(
    onBackClick: () -> Unit,
    onTopicClick: (MyFollowingInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
    viewModel: MyFollowingViewModel = hiltViewModel()
) {
    val topicTitleOverview by viewModel.topicTitleOverview.collectAsStateWithLifecycle()
    val myFollowing = viewModel.myFollowing.collectAsLazyPagingItems()

    MyFollowingScreen(
        topicTitleOverview = topicTitleOverview,
        myFollowing = myFollowing,
        onBackClick = onBackClick,
        onTopicClick = onTopicClick,
        onNodeClick = onNodeClick,
        onUserAvatarClick = onUserAvatarClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyFollowingScreen(
    topicTitleOverview: Boolean,
    myFollowing: LazyPagingItems<MyFollowingInfo.Item>,
    onBackClick: () -> Unit,
    onTopicClick: (MyFollowingInfo.Item) -> Unit,
    onNodeClick: (String, String) -> Unit,
    onUserAvatarClick: (String, String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.my_following)) },
                navigationIcon = { BackIcon(onBackClick = onBackClick) },
                scrollBehavior = scrollBehavior
            )
        },
    ) { insets ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(insets)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            LazyColumn() {
                pagingRefreshItem(myFollowing)
                itemsIndexed(myFollowing, key = { _, item -> item.id }) { index, item ->
                    item?.let {
                        Log.d(TAG, "myfollowing, index = $index, item = $item")
                        SimpleTopic(
                            title = item.title,
                            userName = item.userName,
                            userAvatar = item.avatar,
                            time = item.time,
                            replyCount = item.commentNum.toString(),
                            nodeName = item.tagTitle,
                            nodeTitle = item.tagTitle,
                            titleOverview = topicTitleOverview,
                            onItemClick = { onTopicClick(item) },
                            onNodeClick = { onNodeClick(item.tagTitle, item.tagTitle) },
                            onUserAvatarClick = { onUserAvatarClick(item.userName, item.avatar) }
                        )
                    }
                }
                pagingAppendMoreItem(myFollowing)
            }
        }
    }
}