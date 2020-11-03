package com.example.retrofit_github_repos.model

import com.google.gson.annotations.SerializedName

data class GitHubPullRequest(
        val id: String?,
        val title: String?,
        val number: String?,

        @SerializedName("comments_url")
        val commentsUrl: String?,

        val user: GitHubUser?
){
    override fun toString() = "$title - $id"
}