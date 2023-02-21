package io.github.v2compose.network

import io.github.v2compose.network.bean.Release
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubService {

    companion object {
        private const val BaseUrl = "https://api.github.com/"

        val instance: GithubService by lazy { createGithubApi() }

        private fun createGithubApi(): GithubService {
            val retrofit = Retrofit.Builder()
                .client(OkHttpFactory.httpClient)
                .addConverterFactory(GsonConverterFactory.create(OkHttpFactory.gson))
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