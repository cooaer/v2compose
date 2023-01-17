package io.github.v2compose.network

import io.github.v2compose.network.bean.*
import io.github.v2compose.util.RefererUtils
import me.ghui.fruit.converter.retrofit.FruitConverterFactory
import me.ghui.retrofit.converter.GlobalConverterFactory
import me.ghui.retrofit.converter.annotations.Html
import me.ghui.retrofit.converter.annotations.Json
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by ghui on 05/05/2017.
 */
interface V2exApi {

    companion object {
        val instance: V2exApi by lazy { createV2exApi() }
        private fun createV2exApi(): V2exApi {
            val retrofit = Retrofit.Builder()
                .client(OkHttpFactory.httpClient)
                .addConverterFactory(
                    GlobalConverterFactory.create()
                        .add(FruitConverterFactory.create(OkHttpFactory.fruit), Html::class.java)
                        .add(GsonConverterFactory.create(OkHttpFactory.gson), Json::class.java)
                )
                .baseUrl(NetConstants.BASE_URL)
                .build()
            return retrofit.create(V2exApi::class.java)
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
    @GET("/recent")
    suspend fun recentNews(@Query("p") page: Int): NewsInfo

    @Html
    @GET("/signinnext=/mission/daily")
    suspend fun loginParam(): LoginParam

    @Html
    @FormUrlEncoded
    @Headers("Referer: " + NetConstants.BASE_URL + "/signin")
    @POST("/signin")
    suspend fun login(@FieldMap loginParams: Map<String, String>): Response<ResponseBody>

    @Html
    @GET("/t/{id}")
    suspend fun topicDetails(@Path("id") topicId: String, @Query("p") page: Int): TopicInfo

    @Html
    @GET("/notifications")
    @Headers("user-agent: " + OkHttpFactory.WEB_USER_AGENT)
    suspend fun notifications(@Query("p") page: Int): NotificationInfo

    @Html
    @GET("/my/following")
    suspend fun specialCareInfo(@Query("p") page: Int): CareInfo

    @Html
    @GET("/my/topics")
    suspend fun topicStarInfo(@Query("p") page: Int): TopicStarInfo

    @Html
    @GET("/my/nodes")
    suspend fun nodeStarInfo(): NodeStarInfo

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
    @GET
    suspend fun bingSearch(@Url url: String): BingSearchResultInfo

    @Html
    @GET("/new")
    suspend fun topicCreatePageInfo(): CreateTopicPageInfo

    @Html
    @GET("/append/topic/{id}")
    suspend fun appendPageInfo(
        @Header("Referer") referer: String,
        @Path("id") topicID: String
    ): AppendTopicPageInfo

    @Html
    @FormUrlEncoded
    @POST("/new")
    suspend fun postTopic(@FieldMap postParams: Map<String, String>): TopicInfo

    @Html
    @FormUrlEncoded
    @POST("/append/topic/{id}")
    suspend fun appendTopic(
        @Path("id") topicId: String,
        @FieldMap postParams: Map<String, String>
    ): TopicInfo

    @Html
    @POST("/thank/reply/{id}")
    suspend fun thxReplier(@Path("id") replyId: String, @Query("once") once: String): SimpleInfo

    @Html
    @POST("/thank/topic/{id}")
    suspend fun thxCreator(@Path("id") id: String, @Query("once") once: String): SimpleInfo

    @Html
    @POST("/ajax/money")
    suspend fun thxMoney(): ThxResponseInfo

    // /favorite/topic/812518
    @Html
    @GET("/favorite/topic/{id}")
    suspend fun starTopic(
        @Header("Referer") referer: String,
        @Path("id") id: String,
        @Query("once") once: String
    ): TopicInfo

    @Html
    @GET("/ignore/topic/{id}")
    suspend fun ignoreTopic(@Path("id") id: String, @Query("once") once: String): NewsInfo

    @Html
    @POST("/ignore/reply/{id}")
    suspend fun ignoreReply(
        @Path("id") replyId: String,
        @Query("once") once: String
    ): IgnoreResultInfo

    @Html
    @GET("/settings/ignore/node/{id}")
    suspend fun ignoreNode(@Path("id") nodeId: String, @Query("once") once: String): NodeTopicInfo

    @Html
    @GET("/settings/unignore/node/{id}")
    suspend fun unIgnoreNode(@Path("id") nodeId: String, @Query("once") once: String): NodeTopicInfo

    @Html
    @GET("/unfavorite/topic/{id}")
    suspend fun unStarTopic(
        @Header("Referer") referer: String,
        @Path("id") id: String,
        @Query("once") once: String
    ): TopicInfo

    @Html
    @POST("/up/topic/{id}")
    suspend fun upTopic(@Path("id") id: String, @Query("t") string: String): SimpleInfo

    @Html
    @POST("/down/topic/{id}")
    suspend fun downTopic(@Path("id") id: String, @Query("t") string: String): SimpleInfo

    @Html
    @FormUrlEncoded
    @POST("/t/{id}")
    suspend fun replyTopic(
        @Path("id") id: String,
        @FieldMap replyMap: Map<String, String>
    ): TopicInfo

    @Html
    @GET
    suspend fun blockUser(@Url url: String): SimpleInfo

    @Html
    @GET
    suspend fun followUser(@Header("Referer") referer: String, @Url url: String): UserPageInfo

    @Html
    @GET
    @Headers("Referer: " + RefererUtils.TINY_REFER)
    suspend fun starNode(@Url url: String): SimpleInfo

    @Html
    @GET("/mission/daily")
    suspend fun dailyInfo(): DailyInfo

    //    /mission/daily/redeemonce=84830
    @Html
    @Headers("Referer: " + NetConstants.BASE_URL + "/mission/daily")
    @GET("/mission/daily/redeem")
    suspend fun checkIn(@Query("once") once: String): DailyInfo

    @Html
    @FormUrlEncoded
    @Headers("Referer: " + NetConstants.BASE_URL + "/mission/daily")
    @POST("/2fanext=/mission/daily")
    suspend fun signInTwoStep(@FieldMap map: Map<String, String>): NewsInfo

    @Html
    @Headers("Referer: " + RefererUtils.TINY_REFER)
    @GET
    suspend fun requestByUrl(@Url url: String): DailyInfo

    @Html
    @GET
    suspend fun fadeTopic(@Url url: String): TopicInfo

    @Html
    @GET
    suspend fun stickyTopic(@Url url: String): TopicInfo
}