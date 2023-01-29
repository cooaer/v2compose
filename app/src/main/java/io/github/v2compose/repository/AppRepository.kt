package io.github.v2compose.repository

import io.github.v2compose.network.bean.Release

interface AppRepository {

    suspend fun getAppLatestRelease():Release

}