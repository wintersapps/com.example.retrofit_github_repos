package com.example.retrofit_github_repos.model

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object GitHubService {
    private const val BASE_URL = "https://api.github.com/"

    private val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GitHubApi::class.java)

    fun getUnauthorizedApi(clientId: String, clientSecret: String, code: String): Single<GitHubToken> {
        return api.getAuthToken(clientId, clientSecret, code)
    }
}