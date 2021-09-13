package com.example.testinternet

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.testinternet.data.DataItem
import com.example.testinternet.data.AuthorItem
import com.example.testinternet.database.AuthorDatabase

import java.util.concurrent.Executors


private const val DATABASE_NAME = "AuthorDatabase"
class ZenRepository (context: Context) {
    private val executor = Executors.newSingleThreadExecutor()
    private val zenDataInternet = ZenDataInternet()
    fun GetZenArticleList(string: String) = zenDataInternet.GetZenArticleList(string)
    fun GetZenAuthorList (url: String) = zenDataInternet.GetZenAuthorList(url)
    fun GetImage (url: String) = zenDataInternet.getImage(url)



    val migrationToTwo = object : Migration(1,2){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL( "ALTER TABLE AuthorItem ADD COLUMN indexAuthor INTEGER DEFAULT NULL" )
            database.execSQL("ALTER TABLE AuthorItem ADD COLUMN lastArticle TEXT DEFAULT NULL")
        }
    }
    val migrationToThree = object : Migration(2,3){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL( "ALTER TABLE AuthorItem ADD COLUMN isUpdate INTEGER DEFAULT 0 NOT NULL" )
        }
    }



    companion object{
        private var INSTANCE: ZenRepository? = null
        fun initializade(context: Context){
            if (INSTANCE == null){
                INSTANCE = ZenRepository(context)
            }
        }
        fun get(): ZenRepository{
            return INSTANCE ?: throw IllegalStateException("ZenRepository = null")
        }
    }

    private val database: AuthorDatabase = Room.databaseBuilder(
        context.applicationContext,
        AuthorDatabase::class.java,
        DATABASE_NAME
    )
        .addMigrations(migrationToTwo)
        .addMigrations(migrationToThree)
        .build()
    private val zenDAO = database.authorDAO()
    fun addAuthorZen (author: AuthorItem) {
        executor.execute {
            zenDAO.addAutherItem(author)
        }
    }
    fun updateAuthorZen(author: AuthorItem){
        executor.execute{
            zenDAO.updateAutherItem(author)
        }
    }
    fun getAuthorList(): LiveData<List<AuthorItem>> = zenDAO.getAuthorList()
    fun getAuthorListNoLiveData(): List<AuthorItem>? = zenDAO.getAuthorListNoLiveData()
    fun GetAuthorItem(url: String) = zenDAO.getAuthorItem(url)
    fun getZenCallRequest(url: String) = zenDataInternet.GetZenCallRequest(url)
    fun getZenAuthorListNoLiveData(url: String): List<DataItem> = zenDataInternet.GetZenAuthorListNoLiveData(url)
}