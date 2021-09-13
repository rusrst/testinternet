package com.example.testinternet.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.testinternet.data.AuthorItem
import java.util.*

@Dao
interface AuthorDAO {
    @Query("SELECT * from authorItem")
    fun getAuthorList(): LiveData<List<AuthorItem>>
    @Query("SELECT * from authorItem")
    fun getAuthorListNoLiveData(): List<AuthorItem>?
    @Query("SELECT * from authorItem WHERE url=(:url)")
    fun getAuthorItem(url: String): LiveData<AuthorItem?>
    @Update
    fun updateAutherItem (authorItem: AuthorItem)
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun addAutherItem(authorItem: AuthorItem)
}