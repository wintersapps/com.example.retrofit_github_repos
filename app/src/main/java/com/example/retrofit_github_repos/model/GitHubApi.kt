package com.example.retrofit_github_repos.model

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface GitHubApi {

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("https://github.com/login/oauth/access_token")
    fun getAuthToken(@Field("client_id") clientId: String,
                     @Field("client_secret") clientSecret: String,
                     @Field("code") code: String): Single<GitHubToken>

    @GET("user/repos")
    fun getAllRepos(): Single<List<GitHubRepo>>

    @GET("/repos/{owner}/{repo}/pulls")
    fun getPullRequests(
            @Path("owner") owner: String,
            @Path("repo") repo: String
    ): Single<List<GitHubPullRequest>>

    @GET("/repos/{owner}/{repo}/issues/{issue_number}/comments")
    fun getComments(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("issue_number") pullRequestNumber: String
    ): Single<List<GitHubComment>>

    @POST("/repos/{owner}/{repo}/issues/{issue_number}/comments")
    fun postComment(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Path("issue_number") pullRequestNumber: String,
            @Body comment: GitHubComment
    ): Single<ResponseBody>
}