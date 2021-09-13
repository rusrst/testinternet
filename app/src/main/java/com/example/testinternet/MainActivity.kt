package com.example.testinternet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.testinternet.data.AuthorItem
import com.example.testinternet.database.AddAuthor
import com.example.testinternet.interfaces.CustomAction
import com.example.testinternet.interfaces.HasCustomAction
import com.example.testinternet.interfaces.HasCustomTitle
import com.example.testinternet.recyclerview.ZenAuthorList
import com.example.testinternet.recyclerview.ZenListArticle
import java.util.concurrent.TimeUnit


private const val CHANNEL_ID = "My_notification"

class MainActivity : AppCompatActivity(), ZenAuthorList.Callbacks {
    //lateinit var viewModel: ZenViewModel
    var toolbar: androidx.appcompat.widget.Toolbar? = null
    // нужно чтобы понимать какой фрагмент на экране
    private val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.fragment_container)!!
    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
                updateUi()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        updateUi()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Notification().createChannel(CHANNEL_ID, getString(R.string.channel_notification_all), this)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val isFragmentEmpty = savedInstanceState == null // проверка первое это создание активити или после восстановления (есть фрагмент или нет)
        if (isFragmentEmpty){// создаем фрагмент отображения
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_container, ZenAuthorList.newInstance())
                    .commit()
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)// нужно чтобы понимать какой фрагмент на экране
        if (isWorkSchedule("myWork")) {
            Log.d("TAG", "ЗАДАЧА СОЗДАНА")
           val constraints = Constraints.Builder()
               .setRequiredNetworkType(NetworkType.CONNECTED)
               .build()
           val work = PeriodicWorkRequest
               .Builder(MyWorker::class.java, 4, TimeUnit.HOURS)
               .addTag("myWork")
               .setConstraints(constraints)
               .build()
           WorkManager.getInstance(this.applicationContext)
               .enqueueUniquePeriodicWork("myWork", ExistingPeriodicWorkPolicy.REPLACE, work)
       }
    }
    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
    }

    override fun onButtonPressed() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, AddAuthor())
            .addToBackStack(null)
            .commit()
    }

    override fun onAuthorSelected(data: AuthorItem) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, ZenListArticle.newInstance(data))
                .addToBackStack(null)
                .commit()
    }



    private fun updateUi() {
        val fragment = currentFragment

        if (fragment is HasCustomTitle) {
            supportActionBar?.title = fragment.getTitle()
        } else {
            supportActionBar?.title = getString(R.string.app_name)
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }
        if (fragment is HasCustomAction) {

            toolbar?.menu?.clear()
            createCustomToolbarAction(fragment.getCustomAction())
        } else {
            toolbar?.menu?.clear()
        }
    }

    private fun createCustomToolbarAction(customAction: CustomAction) {
        val iconDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, customAction.iconRes)!!)
        iconDrawable.setTint(Color.BLACK)
            val menuItem = toolbar!!.menu.add(customAction.textRes)
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menuItem.icon = iconDrawable
            menuItem.setOnMenuItemClickListener {
                customAction.onCustomAction.run()
                return@setOnMenuItemClickListener true
            }
    }

    // обработка стрелки назад в toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id: Int = item.itemId
        when (id){
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun isWorkSchedule(tag: String): Boolean{
        val instance = WorkManager.getInstance(this.applicationContext)
        val statuses = instance.getWorkInfosByTag(tag)
        var running = false
        val workInfoList = statuses.get()
        for (workInfo in workInfoList){
            val state = workInfo.state
            running = (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED)
        }
        return running
    }
}