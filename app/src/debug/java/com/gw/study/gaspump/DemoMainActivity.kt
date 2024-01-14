package com.gw.study.gaspump

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.gw.study.gaspump.ui.screen.GasPumpApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DemoMainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GasPumpApp()
        }
    }

}