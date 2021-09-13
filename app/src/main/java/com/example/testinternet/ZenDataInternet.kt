package com.example.testinternet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.testinternet.data.AuthorItem
import com.example.testinternet.data.DataItem
import com.example.testinternet.data.ZenAPI
import com.example.testinternet.data.ZenParser
import com.example.testinternet.database.AuthorDatabase
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.Exception
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

private const val DATABASE_NAME = "AUTHOR_LIST"


class ZenDataInternet () {
    private val zenAPI: ZenAPI
    init {
        val okHttpClient = OkHttpClient.Builder()//установка времени для соединения
            .readTimeout(15, TimeUnit.SECONDS)//установка времени для соединения
            .connectTimeout(15, TimeUnit.SECONDS)//установка времени для соединения
            .build()//установка времени для соединения
        val retrofit = Retrofit.Builder()
                .baseUrl("https://google.com/")
                .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
        zenAPI = retrofit.create(ZenAPI::class.java)
    }
    fun GetZenArticleList (url: String): LiveData<List<DataItem>>{
        val responseLiveData:MutableLiveData<List<DataItem>> = MutableLiveData()
        try {val data: Call<String> = zenAPI.getZenContents(url)
        data.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                responseLiveData.value = ZenParser().GetZenList(response.body().toString())
            }
            override fun onFailure(call: Call<String>, t: Throwable) {}
        })}
        catch (e: Exception){

        }
        return responseLiveData
    }
    fun GetZenAuthorList (url: String): MutableLiveData<AuthorItem>{
        var AuthorItem: MutableLiveData<AuthorItem> = MutableLiveData()
        val data: Call<String> = zenAPI.getZenContents(url)
        data.enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                AuthorItem.value = ZenParser().GetZenAutor(response.body().toString(), url)
            }
            override fun onFailure(call: Call<String>, t: Throwable) {}
        })
        return AuthorItem
    }
    fun GetZenAuthorListNoLiveData(url: String): List<DataItem>{
        val data: Call<String> = zenAPI.getZenContents(url)
        return ZenParser().GetZenList(data.execute().body().toString())
    }
    fun GetZenCallRequest(url: String): Call<String>{
        return zenAPI.getZenContents(url)
    }
    @WorkerThread
    fun getImage (url: String): Bitmap?{
        val response: Response<ResponseBody> = zenAPI.getZenUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream().use(BitmapFactory::decodeStream)
        return bitmap
    }
}