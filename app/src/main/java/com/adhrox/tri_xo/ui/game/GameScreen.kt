package com.adhrox.tri_xo.ui.game

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.GameStatus
import com.adhrox.tri_xo.domain.model.PlayerType
import com.adhrox.tri_xo.ui.theme.Accent
import com.adhrox.tri_xo.ui.theme.Background
import com.adhrox.tri_xo.ui.theme.BlueLink
import com.adhrox.tri_xo.ui.theme.Orange1
import com.adhrox.tri_xo.ui.theme.Orange2

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = hiltViewModel(),
    gameId: String,
    userName: String,
    owner: Boolean,
    navigateToHome: () -> Unit
) {
    LaunchedEffect(key1 = false) {
        gameViewModel.joinToGame(gameId, userName, owner)
    }

    val game: GameModel? by gameViewModel.game.collectAsState()
    val gameStatus: GameStatus by gameViewModel.gameStatus.collectAsState()

    if (gameStatus !is GameStatus.Ongoing) {
        EndGameScreen(
            gameStatus,
            arrayOf(game?.player1!!.tryAgain, game?.player2!!.tryAgain,),
            navigateToHome,

        ) {
            gameViewModel.changeTryAgainStatus(owner)
        }
    } else {
        Board(
            modifier,
            game,
            onItemSelected = {
                position -> gameViewModel.onItemSelected(position)
            }
        )
    }
}

@Composable
fun Board(modifier: Modifier = Modifier, game: GameModel?, onItemSelected: (Int) -> Unit) {
    if (game == null) return

    val clipBoard: ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = game.gameId,
            color = BlueLink,
            modifier = modifier
                .padding(24.dp)
                .clickable {
                    clipBoard.setText(AnnotatedString(game.gameId))
                    Toast
                        .makeText(context, "Copiado", Toast.LENGTH_SHORT)
                        .show()
                    shareId(context, game.gameId)
                })

        val status = if (game.isGameReady) {
            if (game.isMyTurn) {
                "Tu turno"
            } else {
                "Turno rival"
            }
        } else {
            "Esperando por el jugador 2"
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = status, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Accent)
            Spacer(modifier = Modifier.width(6.dp))
            if (!game.isMyTurn || !game.isGameReady) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    trackColor = Orange1,
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            GameItem(game.board[0]) { onItemSelected(0) }
            GameItem(game.board[1]) { onItemSelected(1) }
            GameItem(game.board[2]) { onItemSelected(2) }
        }
        Row {
            GameItem(game.board[3]) { onItemSelected(3) }
            GameItem(game.board[4]) { onItemSelected(4) }
            GameItem(game.board[5]) { onItemSelected(5) }
        }
        Row {
            GameItem(game.board[6]) { onItemSelected(6) }
            GameItem(game.board[7]) { onItemSelected(7) }
            GameItem(game.board[8]) { onItemSelected(8) }
        }
    }
}

@Composable
fun GameItem(playerType: PlayerType, onItemSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .size(64.dp)
            .border(BorderStroke(2.dp, Accent))
            .clickable { onItemSelected() },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = playerType.symbol, label = "") {
            Text(
                text = it,
                color = if (playerType is PlayerType.FirstPlayer) Orange2 else Orange1,
                fontSize = 22.sp
            )
        }
    }
}

@Composable
fun EndGameScreen(
    gameStatus: GameStatus,
    playersTry: Array<Boolean>,
    navigateToHome: () -> Unit,
    changeTryAgainStatus: () -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentAlignment = Alignment.Center
    ) {
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    val gameStatusStr = gameStatus.status
                    val tryAgainResources = getTryAgainResources(playersTry)

                    if (gameStatus is GameStatus.Won){
                        Text(
                            text = "FELICIDADES!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Orange1
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = gameStatusStr,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Orange2
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { changeTryAgainStatus() },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange1)
                    ) {
                        Text(text = "Revancha", color = Accent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            painter = painterResource(id = tryAgainResources[0].first),
                            contentDescription = "",
                            tint = tryAgainResources[0].second

                        )
                        Icon(
                            painter = painterResource(id = tryAgainResources[1].first),
                            contentDescription = "",
                            tint = tryAgainResources[1].second
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navigateToHome() },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange2)
                    ) {
                        Text(text = "Volver al inicio", color = Accent)
                    }
                }
            }
        }
    }
}

private fun shareId(context: Context, idGame: String){
    val sentIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, idGame)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sentIntent, "Compartir ID con...")
    context.startActivity(shareIntent)
}

private fun getTryAgainResources(wantTryArray: Array<Boolean>): List<Pair<Int, Color>> {
    return wantTryArray.map {
        if (it) {
            R.drawable.ic_check to Color.Green
        } else {
            R.drawable.ic_close to Color.Gray
        }
    }
}