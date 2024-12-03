package com.adhrox.tri_xo.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    splashViewModel: SplashViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit
){

    LaunchedEffect(key1 = false) {
        when(splashViewModel.checkDestination()){
            SplashDestination.Home -> navigateToHome()
            SplashDestination.Login -> navigateToLogin()
        }
    }

    /*val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )*/
    
    /*Box(modifier = Modifier.fillMaxSize()){
        Image(
            painter = painterResource(id = R.drawable.bg_app1),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize()
        )
    }*/

    Column(
        modifier = modifier
            .fillMaxSize(),
            //background(Background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(275.dp),
                //.padding(6.dp),
                //.clip(RoundedCornerShape(50))
                //.border(4.dp, PurpleTriqui, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_splash),
                contentDescription = "logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    //.rotate(rotation)
                    //.padding(bottom = 36.dp)
            )
        }
    }
}