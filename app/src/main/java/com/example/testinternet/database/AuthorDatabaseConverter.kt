package com.example.hhapitest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.hhapitest.utils.Event
import com.example.hhapitest.utils.ResourceActions
import com.example.hhapitest.views.Navigator
import com.example.hhapitest.views.UIActions
import com.example.hhapitest.views.base.BaseScreen
import com.example.hhapitest.views.base.LiveEvent
import com.example.hhapitest.views.base.MutableLiveEvent


const val ARG_SCREEN = "ARG_SCREEN"


class MainViewModel(application: Application): AndroidViewModel(application), UIActions, Navigator {
    val whenActivityActive = ResourceActions<MainActivity>()


    private val _result = MutableLiveEvent<Any>()
    val result: LiveEvent<Any> = _result

    override fun launch(screen: BaseScreen) = whenActivityActive{
        launchFragment(it, screen)
    }

    override fun goBack(result: Any?) = whenActivityActive{
        if (result != null){
            _result.value = Event(result)
        }
        it.onBackPressed()
    }

    override fun toast(message: String) {
        TODO("Not yet implemented")
    }

    override fun getString(messageRes: Int, vararg args: Any): String {
        TODO("Not yet implemented")
    }
    fun launchFragment(activity: MainActivity, screen: BaseScreen, addToBackStack: Boolean = true){
        
    }

    override fun onCleared() {
        super.onCleared()
        whenActivityActive.clear()
    }
}