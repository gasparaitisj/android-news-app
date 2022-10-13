package com.telesoftas.justasonboardingapp.ui.main

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(DebugTree("JUDICIALGRANITE"))
    }
}

class DebugTree(private val tagPrefix: String) : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "$tagPrefix.$tag", message, t)
    }
}
