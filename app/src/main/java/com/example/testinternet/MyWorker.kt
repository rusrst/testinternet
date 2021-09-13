package com.example.testinternet

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.testinternet.data.AuthorItem
import com.example.testinternet.data.ZenParser
import retrofit2.Retrofit
import kotlin.math.log

private val CHANNEL_ID = "My_notification"
class MyWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    val myContext = context
    override fun doWork(): Result {
        Log.d("TAG", "START WORKMANAGER")
        val newArticlesOfAuthorItems: MutableList<String> = mutableListOf()
        ZenRepository.initializade(myContext)
        val zenRepository = ZenRepository.get()
        val authorList = zenRepository.getAuthorListNoLiveData()
        if (authorList != null){
            authorList.forEach{
                if (it.isUpdate){
                    var data = zenRepository.getZenAuthorListNoLiveData(it.url)[0]
                    data.urlPaper = data.urlPaper.substringBefore("&")
                    if (data.urlPaper != it.lastArticle){
                        it.lastArticle = data.urlPaper
                        zenRepository.updateAuthorZen(it)
                        newArticlesOfAuthorItems.add(it.title)
                    }
                }
            }
        }
        if (!newArticlesOfAuthorItems.isEmpty()) {
            var longString = "Есть обновления на каналах: "
            newArticlesOfAuthorItems.forEach {
                longString += it
                longString += ", "
            }
            Notification().sendNotification(myContext, CHANNEL_ID, R.drawable.ic_baseline_cached_24,
                "Есть обновления", longString, 1, null)
        }
        else{
            Notification().sendNotification(myContext, CHANNEL_ID, R.drawable.ic_baseline_cached_24,
                "Есть обновления", "", 2, null)
        }

        return  Result.success()
    }
}