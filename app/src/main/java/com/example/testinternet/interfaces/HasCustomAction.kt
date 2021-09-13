package com.example.testinternet.interfaces

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface HasCustomAction {
    fun getCustomAction(): CustomAction
}

class CustomAction(
    @DrawableRes val iconRes: Int,
    @StringRes val textRes: Int,
    val onCustomAction: Runnable
)