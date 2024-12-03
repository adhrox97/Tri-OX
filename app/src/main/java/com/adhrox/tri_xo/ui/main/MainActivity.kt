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

    /*override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }
    private fun hideSystemUI(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(android.view.WindowInsets.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }*/
}

@Composable
fun BackgroundContainer(content: @Composable () -> Unit) {
    /*Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.bg_app1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
        content()
    }*/

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
        /*Image(
            painter = painterResource(id = R.drawable.bg_app1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )*/
        content()
    }
}