package com.example.retrofit_github_repos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.retrofit_github_repos.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody

class MainViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val tokenLD = MutableLiveData<String>()
    val errorLD = MutableLiveData<String>()
    val reposLD = MutableLiveData<List<GitHubRepo>>()
    val pullRequestsLD = MutableLiveData<List<GitHubPullRequest>>()
    val commentsLD = MutableLiveData<List<GitHubComment>>()
    val postCommentLD = MutableLiveData<Boolean>()

    fun getToken(clientId: String, clientSecret: String, code: String) {
        compositeDisposable.add(
                GitHubService.getUnauthorizedApi(clientId, clientSecret, code)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<GitHubToken>() {
                            override fun onSuccess(t: GitHubToken) {
                                tokenLD.value = t.accessToken
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                                errorLD.value = "Can not load token"
                            }

                        })
        )
    }

    fun onLoadRepositories(token: String) {
        compositeDisposable.add(
                GitHubService.getAuthorizedApi(token).getAllRepos()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<List<GitHubRepo>>() {
                            override fun onSuccess(value: List<GitHubRepo>) {
                                reposLD.value = value
                            }

                            override fun onError(e: Throwable) {
                                e.printStackTrace()
                                errorLD.value = "Can not load repositories"
                            }

                        })
        )
    }

    fun onLoadPullRequests(token: String, owner: String?, repo: String?) {
        if (owner != null && repo != null) {
            compositeDisposable.add(
                    GitHubService.getAuthorizedApi(token).getPullRequests(owner, repo)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableSingleObserver<List<GitHubPullRequest>>() {
                                override fun onSuccess(value: List<GitHubPullRequest>) {
                                    pullRequestsLD.value = value
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                    errorLD.value = "Can not load pull requests"
                                }

                            })
            )
        }
    }

    fun onLoadComments(token: String, owner: String?, repo: String?, pullRequestNumber: String?) {
        if (owner != null && repo != null && pullRequestNumber != null) {
            compositeDisposable.add(
                    GitHubService.getAuthorizedApi(token).getComments(owner, repo, pullRequestNumber)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableSingleObserver<List<GitHubComment>>() {
                                override fun onSuccess(value: List<GitHubComment>) {
                                    commentsLD.value = value
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                    errorLD.value = "Can not load comments"
                                }
                            })
            )
        }
    }

    fun onPostComment(token: String, repo: GitHubRepo, pullRequestNumber: String?, comment: GitHubComment){
        if (repo.owner?.login != null && repo.name != null && pullRequestNumber != null) {
            compositeDisposable.add(
                    GitHubService.getAuthorizedApi(token).postComment(repo.owner.login,
                            repo.name, pullRequestNumber, comment)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableSingleObserver<ResponseBody>(){
                                override fun onSuccess(value: ResponseBody) {
                                    postCommentLD.value = true
                                }

                                override fun onError(e: Throwable) {
                                    e.printStackTrace()
                                    errorLD.value = "Can not create comment"
                                }
                            })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}