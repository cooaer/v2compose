package io.github.v2compose.network

import com.google.gson.Gson
import io.github.v2compose.network.bean.*
import me.ghui.fruit.Fruit
import me.ghui.fruit.converter.retrofit.FruitConverterFactory
import me.ghui.retrofit.converter.GlobalConverterFactory
import me.ghui.retrofit.converter.annotations.Html
import me.ghui.retrofit.converter.annotations.Json
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by ghui on 05/05/2017.
 */
interface V2exService {

    companion object {
        fun createV2exService(httpClient: OkHttpClient, fruit: Fruit, gson: Gson): V2exService {
            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(
                    GlobalConverterFactory.create()
                        .add(FruitConverterFactory.create(fruit), Html::class.java)
                        .add(GsonConverterFactory.create(gson), Json::class.java)
                )
                .baseUrl(NetConstants.BASE_URL)
                .build()
            return retrofit.create(V2exService::class.java)
        }
    }

    @Json
    @GET("/api/topics/hot.json")
    suspend fun dailyHot(): DailyHotInfo

    @Json
    @GET("/api/nodes/show.json")
    suspend fun nodeInfo(@Query("name") name: String): NodeInfo

    @Json
    @GET("/api/nodes/s2.json")
    suspend fun nodes(): NodesInfo

    @Json
    @GET("/api/nodes/all.json")
    suspend fun allNodes(): List<Node>

    //https://www.v2ex.com/api/nodes/list.json?fields=name,title,topics,aliases&sort_by=topics&reverse=1
    @Json
    @GET("api/nodes/list.json?fields=name,title,topics,aliases&sort_by=topics&reverse=1")
    suspend fun topicNodes(): List<TopicNode>

    @Json
    @GET("/api/members/show.json")
    suspend fun userInfo(@Query("username") username: String): UserInfo

    @Json
    @GET("https://www.sov2ex.com/api/search")
    suspend fun search(
        @Query("q") keyword: String,
        @Query("from") from: Int,
        @Query("size") size: Int,
    ): SoV2EXSearchResultInfo

    // Below is html API
    @Html
    @GET("/")
    suspend fun homeNews(@Query("tab") tab: String): NewsInfo

    @Html
    @GET("/signin")
    suspend fun loginParam(): LoginParam

    @Html
    @FormUrlEncoded
    @Headers("Referer: " + NetConstants.BASE_URL + "/signin")
    @POST("/signin")
    suspend fun login(@FieldMap loginParams: Map<String, String>): LoginParam

    @Html
    @GET("/t/{id}")
    suspend fun topicDetails(@Path("id") topicId: String, @Query("p") page: Int): TopicInfo

    @Html
    @GET("/notifications")
    @Headers("user-agent: " + NetConstants.webUserAgent)
    suspend fun notifications(@Query("p") page: Int): NotificationInfo

    @Html
    @GET("/my/following")
    suspend fun myFollowingInfo(@Query("p") page: Int): MyFollowingInfo

    @Html
    @GET("/my/topics")
    suspend fun myTopicsInfo(@Query("p") page: Int): MyTopicsInfo

    @Html
    @GET("/my/nodes")
    suspend fun myNodesInfo(): MyNodesInfo

    @Html
    @GET("/")
    suspend fun nodesNavInfo(): NodesNavInfo

    @Html
    @GET("/go/{node}")
    suspend fun nodesInfo(@Path("node") node: String, @Query("p") page: Int): NodeTopicInfo

    @Html
    @GET("/member/{user}")
    suspend fun userPageInfo(@Path("user") username: String): UserPageInfo

    @Html
    @GET("/member/{user}/topics")
    suspend fun userTopics(@Path("user") username: String, @Query("p") page: Int): UserTopics

    @Html
    @GET("/member/{user}/replies")
    suspend fun userReplies(@Path("user") username: String, @Query("p") page: Int): UserReplies

    @Html
    @GET
    suspend fun bingSearch(@Url url: String): BingSearchResultInfo

    @Html
    @GET("/write")
    suspend fun createTopicPageInfo(): CreateTopicPageInfo

    @Html
    @FormUrlEncoded
    @POST("/write")
    suspend fun createTopic(@FieldMap postParams: Map<String, String>): CreateTopicPageInfo

    @Html
    @GET("/append/topic/{id}")
    suspend fun appendTopicPageInfo(
        @Header("Referer") referer: String,
        @Path("id") topicID: String
    ): AppendTopicPageInfo

    @Html
    @FormUrlEncoded
    @POST("/append/topic/{id}")
    suspend fun appendTopic(
        @Path("id") topicId: String,
        @FieldMap postParams: Map<String, String>
    ): AppendTopicPageInfo

    @Html
    @POST("/ajax/money")
    suspend fun thxMoney(): ThxResponseInfo

    @Json
    @GET("/{action}/topic/{id}")
    suspend fun getTopicAction(
        @Header("referer") referer: String,
        @Path("action") action: String,
        @Path("id") topicId: String,
        @Query("once") once: String
    ): V2exResult

    @Json
    @POST("/{action}/topic/{id}")
    suspend fun postTopicAction(
        @Header("referer") referer: String,
        @Path("action") action: String,
        @Path("id") topicId: String,
        @Query("once") once: String
    ): V2exResult

    @Json
    @GET("/{action}/reply/{id}")
    suspend fun getReplyAction(
        @Header("referer") referer: String,
        @Path("action") action: String,
        @Path("id") replyId: String,
        @Query("once") once: String,
    ): V2exResult

    @Json
    @POST("/{action}/reply/{id}")
    suspend fun postReplyAction(
        @Header("referer") referer: String,
        @Path("action") action: String,
        @Path("id") replyId: String,
        @Query("once") once: String,
    ): V2exResult

    @Json
    @POST("/ignore/reply/{id}")
    suspend fun ignoreReply(
        @Header("referer") referer: String,
        @Path("id") replyId: String,
        @Query("once") once: String,
    ): Response<ResponseBody>

    @Html
    @FormUrlEncoded
    @POST("/t/{id}")
    suspend fun replyTopic(
        @Path("id") id: String,
        @FieldMap replyMap: Map<String, String>
    ): ReplyTopicResultInfo

    // https://www.v2ex.com/follow/264541?once=87883
    // https://www.v2ex.com/unfollow/264541?once=86758
    @Html
    @GET
    suspend fun userAction(@Header("Referer") referer: String, @Url url: String): UserPageInfo

    //Request URL: https://www.v2ex.com/unfavorite/node/770?once=18542
    //Request URL: https://www.v2ex.com/favorite/node/770?once=18542
    @Html
    @GET
    suspend fun nodeAction(@Header("Referer") referer: String, @Url url: String): NodeTopicInfo

    @Html
    @GET("/mission/daily")
    suspend fun dailyInfo(): DailyInfo

    //    /mission/daily/redeem?once=84830
    @Html
    @Headers("Referer: " + NetConstants.BASE_URL + "/mission/daily")
    @GET("/mission/daily/redeem")
    suspend fun checkIn(@Query("once") once: String): DailyInfo

    @Html
    @GET("/2fa")
    suspend fun twoStepLogin(): TwoStepLoginInfo

    @Html
    @FormUrlEncoded
    @Headers("Referer: " + NetConstants.BASE_URL)
    @POST("/2fa")
    suspend fun signInTwoStep(@FieldMap map: Map<String, String>): TwoStepLoginInfo

    @Html
    @GET("/member/{user}")
    suspend fun homePageInfo(@Path("user") username: String): HomePageInfo

}