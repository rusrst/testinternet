package com.example.testinternet.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream


class AuthorDatabaseConverter (){
    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }
    fun fromBoolean(boolean: Boolean): Int{
        if (boolean) return 1
        return 0
    }
    fun toBoolean(data: Int): Boolean{
        if (data == 1) return true
        return false
    }
}