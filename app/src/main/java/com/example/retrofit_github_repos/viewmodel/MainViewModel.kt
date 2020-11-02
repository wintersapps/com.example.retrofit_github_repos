package com.example.retrofit_github_repos.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.retrofit_github_repos.model.GitHubService
import com.example.retrofit_github_repos.model.GitHubToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel: ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val tokenLD = MutableLiveData<String>()
    val errorLD = MutableLiveData<String>()

    fun getToken(clientId: String, clientSecret: String, code: String){
        compositeDisposable.add(
                GitHubService.getUnauthorizedApi(clientId, clientSecret, code)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<GitHubToken>(){
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}