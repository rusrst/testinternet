package com.example.testinternet

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.testinternet.data.DataItem

class ZenViewModel (): ViewModel() {
    val zenRepository = ZenRepository.get()
    lateinit var dataItemLiveData: LiveData<List<DataItem>>
    fun setUrlZenArticleList (url: String) {
        dataItemLiveData = zenRepository.GetZenArticleList(url)
    }
    val authorListLiveData = zenRepository.getAuthorList()
    fun authorItem(url: String) = zenRepository.GetAuthorItem(url)
}