package com.example.testinternet.data

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Url

interface ZenAPI {
    @GET
    fun getZenContents(@Url url: String): Call<String>
    @GET
    fun getZenUrlBytes(@Url url: String ): Call<ResponseBody>
    @GET
    fun getZenContentsBody(@Url url: String) : Response<ResponseBody>
}