package com.misw.medisupply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.misw.medisupply.presentation.navigation.MainNavGraph
import com.misw.medisupply.ui.theme.MedisupplyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedisupplyTheme {
                // MainNavGraph handles role selection and multi-role navigation
                MainNavGraph()
            }
        }
    }
}