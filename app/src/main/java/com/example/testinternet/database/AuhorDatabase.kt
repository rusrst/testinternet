package com.example.testinternet.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.testinternet.data.AuthorItem

@Database(entities = [AuthorItem::class], version = 3, exportSchema = false)
@TypeConverters(AuthorDatabaseConverter::class)
abstract class AuthorDatabase: RoomDatabase() {
    abstract fun authorDAO(): AuthorDAO
}