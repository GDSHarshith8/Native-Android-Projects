package com.profileviewer.Repo

import com.profileviewer.network.RetrofitClient
import com.profileviewer.network.User

class ProfileRepository {

    suspend fun fetchUser(): User {
        return RetrofitClient.api.getProfile().user
    }
}
