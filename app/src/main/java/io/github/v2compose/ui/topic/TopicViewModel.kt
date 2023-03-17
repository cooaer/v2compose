package io.github.v2compose.ui.topic

import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.v2compose.R
import io.github.v2compose.core.StringDecoder
import io.github.v2compose.core.extension.isRedirect
import io.github.v2compose.core.extension.redirectLocation
import io.github.v2compose.network.bean.ReplyTopicResultInfo
import io.github.v2compose.network.bean.TopicInfo
import io.github.v2compose.network.bean.TopicInfo.Reply
import io.github.v2compose.repository.AccountRepository
import io.github.v2compose.repository.ActionMethod
import io.github.v2compose.repository.TopicRepository
import io.github.v2compose.ui.BaseViewModel
import io.github.v2compose.usecase.FixHtmlUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.math.ceil

private const val TAG = "TopicViewModel"

@HiltViewModel
class TopicViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    stringDecoder: StringDecoder,
    private val topicRepository: TopicRepository,
    private val accountRepository: AccountRepository,
    private val fixedHtmlImage: FixHtmlUseCase,
) : BaseViewModel(application) {

    companion object {
        const val topicCountPerPage = 100
        const val firstPageIndex = 1
    }

    val topicArgs = TopicArgs(savedStateHandle, stringDecoder)

    //默认的其实页为1
    val initialPage: Int
        get() = maxOf(firstPageIndex, ceil(1f * topicArgs.replyFloor / topicCountPerPage).toInt())


    val isLoggedIn = accountRepository.isLoggedIn
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            false
        )

    val repliesReversed: SharedFlow<Boolean> = topicRepository.repliesOrderReversed
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    fun toggleRepliesReversed() {
        viewModelScope.launch {
            topicRepository.toggleRepliesReversed()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val topicItems: Flow<PagingData<Any>> =
        repliesReversed.flatMapLatest {
            topicRepository.getTopic(
                topicArgs.topicId,
                initialPage,
                it
            )
        }
            .cachedIn(viewModelScope)


    val highlightOpReply: StateFlow<Boolean> = topicRepository.highlightOpReply
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    val replyWithFloor: StateFlow<Boolean> = topicRepository.replyWithFloor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = false
        )

    //缓存评论的收藏、感谢、忽略等状态
    private val _topicInfoWrapper = mutableStateOf(TopicInfoWrapper())
    val topicInfoWrapper: State<TopicInfoWrapper> = _topicInfoWrapper

    fun favoriteTopic() {
        doTopicAction(
            action = "favorite",
            actionNameResId = R.string.topic_menu_item_favorite,
            method = ActionMethod.Get,
            onSuccess = { updateTopicInfoWrapper(favorited = true) },
        )
    }

    fun unFavoriteTopic() {
        doTopicAction(
            action = "unfavorite",
            actionNameResId = R.string.topic_menu_item_unfavorite,
            method = ActionMethod.Get,
            onSuccess = { updateTopicInfoWrapper(favorited = false) },
        )
    }

    fun thanksTopic() {
        val userName = topicInfoWrapper.value.topic?.headerInfo?.userName ?: return
        if (!checkCanThanks(userName)) return
        doTopicAction(
            action = "thank",
            actionNameResId = R.string.menu_item_thank,
            method = ActionMethod.Post,
            onSuccess = { updateTopicInfoWrapper(thanked = true) },
        )
    }

    fun unThanksTopic() {
        viewModelScope.launch {
            updateSnackbarMessage(R.string.unthanks_tips)
        }
    }

    fun ignoreTopic() {
        doTopicAction(
            action = "ignore",
            actionNameResId = R.string.topic_menu_item_ignore,
            method = ActionMethod.Get,
            onSuccess = { updateTopicInfoWrapper(ignored = true) },
        )
    }

    fun unIgnoreTopic() {
        doTopicAction(
            action = "unignore",
            actionNameResId = R.string.topic_menu_item_unignore,
            method = ActionMethod.Get,
            onSuccess = { updateTopicInfoWrapper(ignored = false) },
        )
    }

    fun reportTopic() {
        doTopicAction(
            action = "report",
            actionNameResId = R.string.topic_menu_item_report,
            method = ActionMethod.Get,
            onSuccess = { updateTopicInfoWrapper(reported = true) },
        )
    }

    fun unReportTopic() {
        viewModelScope.launch {
            updateSnackbarMessage(R.string.unreport_tips)
        }
    }

    private fun doTopicAction(
        action: String,
        @StringRes actionNameResId: Int,
        method: ActionMethod,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((String) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        val topic = topicInfoWrapper.value.topic ?: return
        val actionName = context.getString(actionNameResId)
        viewModelScope.launch {
            try {
                val result = topicRepository.doTopicAction(
                    action = action,
                    method = method,
                    topicId = topicArgs.topicId,
                    once = topic.once
                )
                if (result.success) {
                    onSuccess?.invoke()
                    if (result.message.isNotEmpty()) {
                        updateSnackbarMessage(result.message)
                    } else {
                        updateSnackbarMessage(
                            context.getString(
                                R.string.action_success,
                                actionName
                            )
                        )
                    }
                } else {
                    onFailure?.invoke(result.message)
                    if (result.message.isNotEmpty()) {
                        updateSnackbarMessage(result.message)
                    } else {
                        updateSnackbarMessage(
                            context.getString(
                                R.string.action_failure,
                                actionName
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is HttpException && e.code().isRedirect) {
                    onSuccess?.invoke()
                    updateSnackbarMessage(context.getString(R.string.action_success, actionName))
                } else {
                    onError?.invoke(e)
                    updateSnackbarMessage(
                        e.message ?: context.getString(R.string.action_failure, actionName)
                    )
                }

            }
        }
    }

    fun updateTopicInfoWrapper(
        topic: TopicInfo? = null,
        favorited: Boolean? = null,
        thanked: Boolean? = null,
        ignored: Boolean? = null,
        reported: Boolean? = null,
    ) {
        viewModelScope.launch {
            val wrapper = _topicInfoWrapper.value
            _topicInfoWrapper.value = wrapper.copy(
                topic = topic ?: wrapper.topic,
                favorited = favorited ?: wrapper.favorited,
                thanked = thanked ?: wrapper.thanked,
                ignored = ignored ?: wrapper.ignored,
                reported = reported ?: wrapper.reported
            )
        }
    }


    val sizedHtmls = mutableStateMapOf<String, String>()

    fun loadHtmlImage(tag: String, html: String, imageSrc: String?) {
        viewModelScope.launch {
            fixedHtmlImage.loadHtmlImages(html, imageSrc).collectLatest { sizedHtmls[tag] = it }
        }
    }

    //缓存回复的感谢、忽略等状态
    private val _replyWrappers = mutableStateMapOf<String, ReplyWrapper>()
    val replyWrappers: Map<String, ReplyWrapper>
        get() = _replyWrappers.toMap()

    fun thankReply(reply: Reply) {
        if (!checkCanThanks(reply.userName)) return
        doReplyAction(
            reply,
            "thank",
            R.string.menu_item_thank,
            ActionMethod.Post,
            onSuccess = { updateReplyWrapper(reply = reply, thanked = true) })
    }

    fun unFavoriteReply(reply: Reply) {
        doReplyAction(
            reply,
            "unthank",
            R.string.menu_item_unthank,
            ActionMethod.Post,
            onSuccess = { updateReplyWrapper(reply = reply, thanked = false) })
    }

    fun ignoreReply(reply: Reply) {
        val topic = topicInfoWrapper.value.topic ?: return
        val actionName = context.getString(R.string.ignore_comment)
        viewModelScope.launch {
            try {
                val result = topicRepository.ignoreReply(
                    topicId = topicArgs.topicId,
                    replyId = reply.replyId,
                    once = topic.once
                )
                if (result) {
                    updateReplyWrapper(reply, ignored = true)
                    updateSnackbarMessage(context.getString(R.string.action_success, actionName))
                } else {
                    updateSnackbarMessage(context.getString(R.string.action_failure, actionName))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateSnackbarMessage(
                    e.message ?: context.getString(R.string.action_failure, actionName)
                )
            }
        }
    }

    private fun doReplyAction(
        reply: Reply,
        action: String,
        @StringRes actionNameResId: Int,
        method: ActionMethod,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((String) -> Unit)? = null,
        onError: ((Throwable?) -> Unit)? = null
    ) {
        val topic = topicInfoWrapper.value.topic ?: return
        val actionName = context.getString(actionNameResId)
        viewModelScope.launch {
            try {
                val result = topicRepository.doReplyAction(
                    action = action,
                    method = method,
                    topicId = topicArgs.topicId,
                    replyId = reply.replyId,
                    once = topic.once
                )
                if (result.success) {
                    onSuccess?.invoke()
                    if (result.message.isNotEmpty()) {
                        updateSnackbarMessage(result.message)
                    } else {
                        updateSnackbarMessage(
                            context.getString(
                                R.string.action_success,
                                actionName
                            )
                        )
                    }
                } else {
                    onFailure?.invoke(result.message)
                    if (result.message.isNotEmpty()) {
                        updateSnackbarMessage(result.message)
                    } else {
                        updateSnackbarMessage(
                            context.getString(
                                R.string.action_failure,
                                actionName
                            )
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                if (e.isRedirect) {
                    onSuccess?.invoke()
                    updateSnackbarMessage(context.getString(R.string.action_success, actionName))
                } else {
                    onError?.invoke(e)
                    updateSnackbarMessage(
                        e.message ?: context.getString(R.string.action_failure, actionName)
                    )
                }

            }
        }
    }

    private fun updateReplyWrapper(
        reply: Reply,
        thanked: Boolean? = null,
        ignored: Boolean? = null,
    ) {
        viewModelScope.launch {
            val replyWrapper = _replyWrappers[reply.replyId]?.let {
                it.copy(
                    reply = reply,
                    thanked = thanked ?: it.thanked,
                    ignored = ignored ?: it.ignored,
                )
            } ?: ReplyWrapper(
                reply = reply,
                thanked = thanked ?: false,
                ignored = ignored ?: false
            )
            _replyWrappers[reply.replyId] = replyWrapper
        }
    }

    private val _replyTopicState = MutableStateFlow<ReplyTopicState>(ReplyTopicState.Idle)
    val replyTopicState: StateFlow<ReplyTopicState> = _replyTopicState

    fun replyTopic(content: String) {
        viewModelScope.launch {
            _replyTopicState.emit(ReplyTopicState.Idle)
            val topic = topicInfoWrapper.value.topic ?: return@launch
            val actionName = context.getString(R.string.reply)

            _replyTopicState.emit(ReplyTopicState.Loading)
            try {
                val result = topicRepository.replyTopic(topicArgs.topicId, content, topic.once)
                _replyTopicState.emit(ReplyTopicState.Failure(result))
            } catch (e: Exception) {
                e.printStackTrace()
                if (e.isRedirect) {
                    val location = e.redirectLocation ?: ""
                    _replyTopicState.emit(ReplyTopicState.Success(location))
                    updateSnackbarMessage(context.getString(R.string.action_success, actionName))
                } else {
                    _replyTopicState.emit(ReplyTopicState.Error(e))
                    val errorMsg = context.getString(R.string.action_failure, actionName)
                    updateSnackbarMessage(e.message ?: errorMsg)
                }
            }
        }
    }


    private fun checkCanThanks(userName: String): Boolean {
        val account = runBlocking { accountRepository.account.first() }
        if (userName == account.userName) {
            viewModelScope.launch {
                updateSnackbarMessage(context.getString(R.string.thanks_fail_is_self))
            }
            return false
        }
        if (_topicInfoWrapper.value.topic?.headerInfo?.canSendThanks() == false) {
            viewModelScope.launch {
                updateSnackbarMessage(context.getString(R.string.thanks_fail_just_joined))
            }
            return false
        }
        return true
    }

}

@Stable
sealed interface ReplyTopicState {
    object Idle : ReplyTopicState
    object Loading : ReplyTopicState
    data class Success(val redirect: String) : ReplyTopicState
    data class Failure(val result: ReplyTopicResultInfo) : ReplyTopicState
    data class Error(val error: Throwable?) : ReplyTopicState
}