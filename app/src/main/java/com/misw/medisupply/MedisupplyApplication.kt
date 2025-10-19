package com.misw.medisupply

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Medisupply
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class MedisupplyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization code here
    }
}
