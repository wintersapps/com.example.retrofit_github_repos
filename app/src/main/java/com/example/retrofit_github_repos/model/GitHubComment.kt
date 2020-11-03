package com.example.retrofit_github_repos.model

data class GitHubComment(
        val body: String?,
        val id: String?
){
    override fun toString() = "$body - $id"
}