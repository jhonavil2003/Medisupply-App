package com.misw.medisupply

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import android.util.Log
import com.amplifyframework.core.Amplify
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin

/**
 * Application class for Medisupply
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection
 */
@HiltAndroidApp
class MedisupplyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.configure(applicationContext) // carga res/raw/amplifyconfiguration.json
            Log.i("App", "Amplify inicializado")
        } catch (e: Exception) {
            Log.e("App", "Error inicializando Amplify", e)
        }
        // Application initialization code here
    }
}
