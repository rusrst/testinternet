package com.example.testinternet

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import kotlin.math.log

private const val TAG = "BACKGROUND Thread"
private const val MESSAGE_DOWNLOAD = 0
private const val MESSAGE_LIST_IMAGE_DOWNLOAD = 1
class BackgroundThread<T>(val tag: String,
                             private val requestHandler: Handler,
                             private val imageDownloaded: (( Bitmap, Int?) -> Unit)?,
                             private val imageListDownloaded: ((List<Bitmap>)-> Unit)?) : HandlerThread(tag) {
    lateinit var mHandler: Handler
    val zenRepository = ZenViewModel().zenRepository
    init {
        start()
        looper
    }

    private var hasQuit = false
    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun returnImage( url: String){
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, url)
                .sendToTarget()
    }
    fun returnListImage(list: List<String>){
        mHandler.obtainMessage(MESSAGE_LIST_IMAGE_DOWNLOAD, 0, 0, list)
                .sendToTarget()
    }

    override fun onLooperPrepared() {
        mHandler = object: Handler(Looper.myLooper()!!){
            override fun handleMessage(msg: Message) {
                when (msg.what){
                    MESSAGE_DOWNLOAD -> handleRequest(msg.obj as String)
                    MESSAGE_LIST_IMAGE_DOWNLOAD -> downloadListImage(msg.obj as List<String>)
                }
            }
        }

    }
    fun handleRequest(url: String){
        val bitmap = zenRepository.GetImage(url)
                ?: return
        requestHandler.post(Runnable {
            if (imageDownloaded != null) {
                imageDownloaded.let { it(bitmap, null) }
            }
            else return@Runnable
        })
    }
    fun downloadListImage(list: List<String>){
        val bitmap: MutableList<Bitmap> = mutableListOf()

           list.forEach {
               bitmap.add(zenRepository.GetImage(it)
                       ?: Bitmap.createBitmap(75, 75, Bitmap.Config.RGB_565))
           }
               requestHandler.post(Runnable {
                   imageListDownloaded?.let { it(bitmap) }
               })
    }
}