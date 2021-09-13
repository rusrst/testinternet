package com.example.testinternet

import android.app.Application

class ZenApp: Application() {
    override fun onCreate() {
        super.onCreate()
        ZenRepository.initializade(this)
    }
}