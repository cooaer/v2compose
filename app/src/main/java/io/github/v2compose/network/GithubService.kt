package io.github.v2compose.network

import com.google.gson.Gson
import io.github.v2compose.network.bean.Release
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {

    companion object {
        private const val BaseUrl = "https://api.github.com/"

        fun createGithubApi(httpClient: OkHttpClient, gson: Gson): GithubService {
            val retrofit = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BaseUrl)
                .build()
            return retrofit.create(GithubService::class.java)
        }
    }

    //eg : https://api.github.com/repos/tachiyomiorg/tachiyomi/releases/latest
    @GET("/repos/{owner}/{repo}/releases/latest")
    suspend fun getTheLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Release

}