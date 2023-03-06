package io.github.v2compose.repository.def

import io.github.v2compose.Constants
import io.github.v2compose.network.GithubService
import io.github.v2compose.network.bean.Release
import io.github.v2compose.repository.AppRepository
import javax.inject.Inject

class DefaultAppRepository @Inject constructor(private val githubService: GithubService) : AppRepository {

    override suspend fun getAppLatestRelease(): Release {
        return githubService.getTheLatestRelease(Constants.owner, Constants.repo)
    }

}