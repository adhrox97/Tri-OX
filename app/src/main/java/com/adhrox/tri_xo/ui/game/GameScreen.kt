package com.adhrox.tri_xo.ui.game

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.domain.model.GameModel
import com.adhrox.tri_xo.domain.model.GameStatus
import com.adhrox.tri_xo.domain.model.PlayerType
import com.adhrox.tri_xo.ui.theme.Accent
import com.adhrox.tri_xo.ui.theme.Accent2
import com.adhrox.tri_xo.ui.theme.Accent3
import com.adhrox.tri_xo.ui.theme.Background
import com.adhrox.tri_xo.ui.theme.BlueLink
import com.adhrox.tri_xo.ui.theme.CustomTypography
import com.adhrox.tri_xo.ui.theme.Orange1
import com.adhrox.tri_xo.ui.theme.Orange2
import kotlin.math.PI
import kotlin.math.pow

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
    //val gameStatus: GameStatus by gameViewModel.gameStatus.collectAsState()

    if (game?.status !is GameStatus.Ongoing && game != null) {
        EndGameScreen(
            game?.status!!,
            arrayOf(game?.player1!!.tryAgain, game?.player2!!.tryAgain),
            navigateToHome,

            ) {
            gameViewModel.changeTryAgainStatus(owner)
        }
    } else {
        Board(
            modifier,
            game,
            onItemSelected = { position ->
                gameViewModel.onItemSelected(position)
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
        modifier
            .fillMaxSize()
            .background(Color.Transparent),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 24.dp),
            text = "Game ID",
            fontSize = 32.sp,
            fontFamily = FontFamily(
                Font(R.font.tomorrow_regular, FontWeight.Normal)
            ),
            color = Color.White
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .padding(horizontal = 48.dp),
            shape = RoundedCornerShape(25),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2D43))
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = game.gameId,
                    color = BlueLink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            clipBoard.setText(AnnotatedString(game.gameId))
                            Toast
                                .makeText(context, "Copiado", Toast.LENGTH_SHORT)
                                .show()
                            shareId(context, game.gameId)
                        }
                )
            }
        }

        val status = if (game.isGameReady) {
            if (game.isMyTurn) {
                "Tu turno"
            } else {
                "Turno rival"
            }
        } else {
            "Esperando por el jugador 2"
        }
        Spacer(modifier = Modifier.height(56.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .padding(end = 82.dp)
                .background(
                    Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0f to Accent3,
                            0.6f to Color(0xFF807F7F),
                            1f to Color(0x00FFFFFF)
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = status, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                if (!game.isMyTurn || !game.isGameReady) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        trackColor = Orange1,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(84.dp))

        /*Row {
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
        }*/

        //PreviewTicTacToeBoard()
        TicTacToeBoard(game.board, onItemSelected)

        /*Row {
            TableSlot(game.board[0], 315f) { onItemSelected(0) }
            TableSlot(game.board[1], 270f) { onItemSelected(1) }
            TableSlot(game.board[2], 225f) { onItemSelected(2) }
        }
        Row {
            TableSlot(game.board[3], 0f)   { onItemSelected(3) }
            TableSlot(game.board[4], 135f) { onItemSelected(4) }
            TableSlot(game.board[5], 180f) { onItemSelected(5) }
        }
        Row {
            TableSlot(game.board[6], 45f)  { onItemSelected(6) }
            TableSlot(game.board[7], 90f)  { onItemSelected(7) }
            TableSlot(game.board[8], 135f) { onItemSelected(8) }
        }*/
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
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
            //.background(Background),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.3f)),
            border = BorderStroke(2.dp, Accent3),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    val gameStatusStr = gameStatus.status
                    val tryAgainResources = getTryAgainResources(playersTry)

                    if (gameStatus is GameStatus.Won) {
                        Text(
                            text = "FELICIDADES!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Accent3
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = gameStatusStr,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Accent2,
                        //maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    if (gameStatus !is GameStatus.Finished) {
                        Button(
                            onClick = { changeTryAgainStatus() },
                            colors = ButtonDefaults.buttonColors(containerColor = Accent2)
                        ) {
                            Text(
                                text = "Revancha",
                                color = Color.Black,
                                style = CustomTypography.bodyLarge
                            )
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
                    }
                    Button(
                        onClick = { navigateToHome() },
                        colors = ButtonDefaults.buttonColors(containerColor = Accent3)
                    ) {
                        Text(
                            text = "Volver al inicio",
                            color = Color.Black,
                            style = CustomTypography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TableSlot(playerType: PlayerType, angle: Float, onItemSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(12.dp)
            .size(64.dp)
            .border(BorderStroke(2.dp, Color.Black))
            .clickable { onItemSelected() }
            .clip(RoundedCornerShape(25))
            .gradientBackground(
                colors = arrayOf(
                    0.2f to Color.Transparent,
                    1f to Color(0x83990101)
                ),
                angle = angle
            )
            /*.background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0.2f to Color.Transparent,
                        1f to Color(0x83990101)
                    ),
                    //start = Offset(0f, 0f),
                    //end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    //start = Offset(Float.POSITIVE_INFINITY, 0f),
                    //end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    //start = Offset(Float.POSITIVE_INFINITY, 0f),
                    //end = Offset(0f, Float.POSITIVE_INFINITY),
                    //start = Offset(Float.POSITIVE_INFINITY, 0f),
                    //end = Offset(0f, 0f),
                    //start = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    //end = Offset(0f, 0f),
                    //start = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                    //end = Offset(Float.POSITIVE_INFINITY, 0f),
                    //start = Offset(0f, Float.POSITIVE_INFINITY),
                    //end = Offset(Float.POSITIVE_INFINITY, 0f),
                    //start = Offset(0f, 0f),
                    //end = Offset(Float.POSITIVE_INFINITY, 0f),
                )
            )*/,

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

//------------------------------------------------------------------
@Composable
fun TicTacToeBoard(
    board: List<PlayerType>, // El tablero representado como una lista de listas ('X', 'O', ' ')
    onItemSelected: (Int) -> Unit
    //onCellClick: (index: Int) -> Unit
) {
    Box(
        modifier = Modifier
            .size(300.dp) // Tamaño del tablero
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val cellSize = 300.dp.toPx() / 3
                    val col = (tapOffset.x / cellSize).toInt()
                    val row = (tapOffset.y / cellSize).toInt()

                    val index = row * 3 + col
                    onItemSelected(index)
                }
            }
    ) {
        // Colocar los símbolos "X" o "O" en el lugar adecuado
        for (index in board.indices) {

            val playerType = board[index]
            val symbol = playerType.symbol
            val gradientDegList = listOf(315f, 270f, 225f, 0f, null, 180f, 45f, 90f, 135f)

            //if (symbol != "") {
            val row = index / 3
            val col = index % 3
            Box(
                modifier = Modifier
                    .size(100.dp) // Tamaño de cada celda
                    //.padding(4.dp)
                    .align(Alignment.TopStart)
                    .offset(x = (col * 100).dp, y = (row * 100).dp) // Ajustar la posición de cada símbolo
                    //.border(BorderStroke(2.dp, Color.Black))
                    //.clip(RoundedCornerShape(25))
                    .gradientBackground(
                        colors = arrayOf(
                            0.3f to Color.Transparent,
                            1f to Accent3
                            //1f to Color(0x83990101)
                        ),
                        angle = gradientDegList[index]
                    ),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(targetState = symbol, label = "") {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = 48.sp),
                        color = if (playerType is PlayerType.FirstPlayer) Color.Red else Color.Blue
                    )
                }

                /*Box(
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
                }*/

                /*Text(
                    text = symbol.toString(),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 48.sp),
                    color = if (symbol == "X") Color.Red else Color.Blue
                )*/
            }
        // Dibujar el tablero con líneas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val boardSize = size.minDimension
            val cellSize = boardSize / 3

            // Dibujar las líneas horizontales
            for (i in 1..2) {
                drawLine(
                    color = Color.Black,
                    start = Offset(10f, i * cellSize),
                    end = Offset(boardSize-10f, i * cellSize),
                    strokeWidth = 24f,
                    cap = StrokeCap.Round
                )
            }

            // Dibujar las líneas verticales
            for (i in 1..2) {
                drawLine(
                    color = Color.Black,
                    start = Offset(i * cellSize, 10f),
                    end = Offset(i * cellSize, boardSize-10f),
                    strokeWidth = 24f,
                    cap = StrokeCap.Round
                )
            }
        }

        /*// Colocar los símbolos "X" o "O" en el lugar adecuado
        for (row in 0 until 3) {
            for (col in 0 until 3) {
                val symbol = board[row][col]
                if (symbol != ' ') {
                    Box(
                        modifier = Modifier
                            .size(100.dp) // Tamaño de cada celda
                            .align(Alignment.TopStart)
                            .offset(x = (col * 100).dp, y = (row * 100).dp), // Ajustar la posición de cada símbolo
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = symbol.toString(),
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 48.sp),
                            color = if (symbol == 'X') Color.Red else Color.Blue
                        )
                    }
                }
            }
        }*/
            //}
        }
    }
}

/*@Preview
@Composable
fun PreviewTicTacToeBoard() {
    *//*val board = listOf(
        listOf('X', 'O', ' '),
        listOf(' ', 'X', 'O'),
        listOf('O', ' ', 'X')
    )*//*

    val board = listOf("X", "O", "O", " ", "X", "O", "O", " ", "X")

    TicTacToeBoard(board) { index ->
        Log.i("adhrox", "Click en la celda $index")
    }
}*/
//------------------------------------------------------------------

fun Modifier.gradientBackground(colors: Array<Pair<Float, Color>>, angle: Float?) = this.then(
    Modifier.drawBehind {

        if (angle != null) {
            val angleRad = angle / 180f * PI
            val x = kotlin.math.cos(angleRad).toFloat() //Fractional x
            val y = kotlin.math.sin(angleRad).toFloat() //Fractional y

            val radius:Float = kotlin.math.sqrt(
                ((size.width.pow(2) + size.height.pow(2))) / 2f)
            val offset = center + Offset(x * radius, y * radius)

            val exactOffset = Offset(
                x = kotlin.math.min(offset.x.coerceAtLeast(0f), size.width),
                y = size.height - kotlin.math.min(offset.y.coerceAtLeast(0f), size.height)
            )

            drawRect(
                brush = Brush.linearGradient(
                    colorStops = colors,
                    start = Offset(size.width, size.height) - exactOffset,
                    end = exactOffset
                ),
                size = size
            )
        } else {
            drawRect(
                color = colors.last().second,
                size = size
            )
        }
    }
)

private fun shareId(context: Context, idGame: String) {
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