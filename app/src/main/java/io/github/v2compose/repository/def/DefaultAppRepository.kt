package io.github.v2compose.repository.def

import io.github.v2compose.Constants
import io.github.v2compose.network.GithubApi
import io.github.v2compose.network.bean.Release
import io.github.v2compose.repository.AppRepository
import javax.inject.Inject

class DefaultAppRepository @Inject constructor(private val githubApi: GithubApi) : AppRepository {

    override suspend fun getAppLatestRelease(): Release {
        return githubApi.getTheLatestRelease(Constants.owner, Constants.repo)
    }

}