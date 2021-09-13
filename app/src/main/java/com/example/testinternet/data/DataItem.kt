package com.example.testinternet.data

import android.graphics.Bitmap

data class DataItem(var urlPaper: String = "",
                    var header: String = "",
                    var urlImage: String = "",
                    var text: String = "",
                    var bitmap: Bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.RGB_565)){

}