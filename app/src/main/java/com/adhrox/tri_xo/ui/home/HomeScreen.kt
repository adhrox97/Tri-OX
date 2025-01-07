package com.adhrox.tri_xo.ui.home

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.domain.model.GameModeEnum
import com.adhrox.tri_xo.domain.model.GameVerificationResult.*
import com.adhrox.tri_xo.ui.theme.Accent
import com.adhrox.tri_xo.ui.theme.Accent2
import com.adhrox.tri_xo.ui.theme.Accent3
import com.adhrox.tri_xo.ui.theme.Background
import com.adhrox.tri_xo.ui.theme.BgText2
import com.adhrox.tri_xo.ui.theme.CustomTypography
import com.adhrox.tri_xo.ui.theme.MainColorBackground
import com.adhrox.tri_xo.ui.theme.Orange1
import com.adhrox.tri_xo.ui.theme.Orange2
import kotlinx.coroutines.coroutineScope

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    navigateToGame: (String, String, Boolean) -> Unit,
    navigateToDetail: (String) -> Unit
) {
    LaunchedEffect(key1 = false) {
        homeViewModel.getCurrentUserModel()
    }

    val gameState: GameState by homeViewModel.gameState.collectAsState()

    gameState.gameVerification?.let { gameVerification ->
        val message = gameVerification.refStatus
        toastMessage(LocalContext.current, stringResource(id = message))
        homeViewModel.resetGameVerificationState()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
        //.background(Background)
    ) {
        Header(gameState.user.userName, gameState.isLoading, navigateToDetail)
        Body(
            loadingState = gameState.isLoading,
            onCrateGame = { gameMode -> homeViewModel.onCreateGame(gameMode, navigateToGame) },
            onJoinGame = { gameId -> homeViewModel.onJoinGame(gameId, navigateToGame) }
        )
    }
    if (gameState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(42.dp),
                trackColor = MainColorBackground,
                color = Accent2,
                strokeWidth = 6.dp
            )
        }
    }
}

@Composable
fun Header(userName: String, loadingState: Boolean, navigateToDetail: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(100.dp),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                topEnd = 25.dp,
                bottomEnd = 25.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D43))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { if (!loadingState) navigateToDetail(userName) },
                    text = "${stringResource(id = R.string.welcome)} $userName",
                    maxLines = 1,
                    color = Color.White,
                    //fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        modifier = Modifier.rotate(45f),
                        painter = painterResource(id = R.drawable.ic_app_splash),
                        contentDescription = null
                    )
                    Image(
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .border(0.dp, Color.Transparent, RoundedCornerShape(50.dp)),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(id = R.drawable.user_image_sample),
                        contentDescription = null,
                    )
                }
            }
        }
    }
}

@Composable
fun Body(loadingState: Boolean, onCrateGame: (String) -> Unit, onJoinGame: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(modifier = Modifier.height(42.dp))
        CreateGame(loadingState) { gameMode -> onCrateGame(gameMode) }
        Image(painter = painterResource(id = R.drawable.ic_app_splash), contentDescription = null)
        JoinGame(onJoinGame)
    }
}

@Composable
fun CreateGame(loadingState: Boolean, onCrateGame: (String) -> Unit) {

    val context = LocalContext.current
    val selectedMode = remember { mutableStateOf(GameModeEnum.STANDARD) }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        onClick = { onCrateGame(selectedMode.value.name) },
        enabled = !loadingState,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE4B702))
    ) {
        Text(
            text = stringResource(id = R.string.create_game),
            style = CustomTypography.bodyLarge,
            color = Color.Black,
            fontSize = 20.sp
        )
    }
    
    val selectedModeString = stringResource(id = R.string.selected_mode)
    
    GameModeSelector(
        selectedMode = selectedMode,
        onModeSelected = { gameModeName ->
            toastMessage(context, "$selectedModeString $gameModeName")
        }
    )
}

@Composable
fun JoinGame(onJoinGame: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    //Column(horizontalAlignment = Alignment.CenterHorizontally) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        placeholder = { Text(text = stringResource(id = R.string.type_game_id), textAlign = TextAlign.Center) },
        onValueChange = { text = it },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            //focusedTextColor = Accent,
            unfocusedTextColor = Accent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFFE4B702),
            unfocusedBorderColor = Color(0xFFF23831),
            cursorColor = Color(0xFFE4B702)
        )
    )
    Spacer(modifier = Modifier.height(8.dp))
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        onClick = {
            onJoinGame(text)
        },
        shape = RoundedCornerShape(12.dp),
        enabled = text.isNotBlank(),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFF23831),
            disabledContainerColor = Color.Gray
        )
    ) {
        Text(
            text = stringResource(id = R.string.join_game),
            style = CustomTypography.bodyLarge,
            color = Color.Black,
            fontSize = 20.sp
        )
    }
}
//}

@Composable
fun GameModeSelector(
    selectedMode: MutableState<GameModeEnum>,
    onModeSelected: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFD1D1D1)),
            border = BorderStroke(1.dp, Color.Black),
            shape = RoundedCornerShape(25),
            onClick = { expanded.value = true }
        ) {
            Text(
                text = "${stringResource(id = R.string.mode)}: ${stringResource(id = selectedMode.value.refMode)}",
                style = CustomTypography.bodyLarge,
                fontWeight = FontWeight.Light,
                color = Color.Black,
                fontSize = 20.sp
            )
        }

        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            GameModeEnum.entries.forEach { mode ->
                val gameModeName = stringResource(id = mode.refMode)
                DropdownMenuItem(
                    //modifier = Modifier.background(Color.Red),
                    onClick = { 
                        selectedMode.value = mode
                        expanded.value = false
                        onModeSelected(gameModeName)
                    },
                    //colors = MenuItemColors(),
                    text = {
                        Text(
                            style = CustomTypography.bodyLarge,
                            fontWeight = FontWeight.Light,
                            text = stringResource(id = mode.refMode)
                        )
                    }
                )
            }
        }
    }
}

private fun toastMessage(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}