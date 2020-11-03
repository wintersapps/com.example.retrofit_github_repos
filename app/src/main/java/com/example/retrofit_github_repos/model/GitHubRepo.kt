package com.example.retrofit_github_repos.model

data class GitHubRepo(
        val name: String?,
        val url: String?,
        val owner: GitHubUser?
){
    override fun toString() = "$name - $url"
}