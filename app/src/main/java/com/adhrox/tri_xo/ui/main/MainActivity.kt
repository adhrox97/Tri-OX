package com.adhrox.tri_xo.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.adhrox.tri_xo.ui.core.ContentWrapper
import com.adhrox.tri_xo.ui.theme.GradientColorBackground
import com.adhrox.tri_xo.ui.theme.MainColorBackground
import com.adhrox.tri_xo.ui.theme.TriXOTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var navigationController: NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //hideSystemUI()
        setContent {
            TriXOTheme {
                BackgroundContainer {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        navigationController = rememberNavController()
                        ContentWrapper(navigationController = navigationController, Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun BackgroundContainer(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
            .fillMaxSize()
            .background(
                //Color.Red
                Brush.verticalGradient(
                    colorStops = arrayOf(
                        0.87f to MainColorBackground,
                        1f to GradientColorBackground
                    )
                )
            )
        )
        content()
    }
}