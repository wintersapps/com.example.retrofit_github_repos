package com.example.retrofit_github_repos.model

import com.example.retrofit_github_repos.BuildConfig
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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

    fun getAuthorizedApi(token: String): GitHubApi{
        val okHttpClient = OkHttpClient.Builder()

        val logging =  HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        if(BuildConfig.DEBUG){
            okHttpClient.addInterceptor(logging)
        }

        okHttpClient.addInterceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder()
                    .addHeader("Authorization", "token ${token}")
                    .build()
            chain.proceed(newRequest)
        }

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(GitHubApi::class.java)
    }
}