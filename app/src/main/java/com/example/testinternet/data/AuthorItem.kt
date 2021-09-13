package com.example.testinternet.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class AuthorItem(var title: String = "",
                      var descriptor: String = "",
                      @PrimaryKey var url: String = "",
                      var urlImage: String = "",
                      var bitmap: Bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.RGB_565),
                      var indexAuthor: Int? = null,
                      var lastArticle: String? = null,
                      var isUpdate: Boolean = false
                        ){
}
