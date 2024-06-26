package com.adhrox.tri_xo.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.ui.theme.Accent
import com.adhrox.tri_xo.ui.theme.Background
import com.adhrox.tri_xo.ui.theme.Orange1

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToGame: (String, String, Boolean) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Header()
        Body(
            onCrateGame = { homeViewModel.onCreateGame(navigateToGame) },
            onJoinGame = { gameId -> homeViewModel.onJoinGame(gameId, navigateToGame) }
        )
    }
}

@Composable
fun Header() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(12.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Orange1, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.applogo),
                contentDescription = "logo",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            )
        }
        Text(
            text = "Tri-XO",
            fontSize = 28.sp,
            color = Orange1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Body(onCrateGame: () -> Unit, onJoinGame: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(24.dp),
        colors = CardDefaults.cardColors(containerColor = Background),
        border = BorderStroke(2.dp, Orange1),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                var createGame by remember { mutableStateOf(true) }
                Spacer(modifier = Modifier.height(6.dp))
                Switch(
                    checked = createGame,
                    onCheckedChange = { createGame = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Orange1,
                        checkedTrackColor = Orange1.copy(alpha = 0.54f),
                        uncheckedTrackColor = Background.copy(alpha = 0.38f)
                    )
                )
                AnimatedContent(targetState = createGame, label = "") {
                    when (it) {
                        true -> CreateGame(onCrateGame)
                        false -> JoinGame(onJoinGame)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CreateGame(onCrateGame: () -> Unit) {
    Button(
        onClick = { onCrateGame() },
        colors = ButtonDefaults.buttonColors(containerColor = Orange1)
    ) {
        Text(text = "Crear juego", color = Accent)
    }
}

@Composable
fun JoinGame(onJoinGame: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Accent,
                unfocusedTextColor = Accent,
                focusedBorderColor = Orange1,
                unfocusedBorderColor = Accent,
                cursorColor = Orange1
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onJoinGame(text) },
            enabled = text.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Orange1,
                disabledContainerColor = Color.Gray
            )
        ) {
            Text(text = "Unirse a juego", color = Accent)
        }
    }
}