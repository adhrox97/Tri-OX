package com.adhrox.tri_xo.ui.userdetail

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.domain.model.UserModel
import com.adhrox.tri_xo.ui.theme.Accent1
import com.adhrox.tri_xo.ui.theme.Accent2
import com.adhrox.tri_xo.ui.theme.CustomTypography
import com.adhrox.tri_xo.ui.theme.MainColorBackground
import kotlin.math.PI
import kotlin.math.atan2

@Composable
fun UserDetailScreen(
    modifier: Modifier = Modifier,
    userInfo: UserModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            //.background(Background)
            //.padding(5.dp)
        ,
        //verticalArrangement = Arrangement.spacedBy(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        val context = LocalContext.current
        Header(userInfo.userName, userInfo.email)
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            PieChart(
                modifier = Modifier
                    .size(500.dp),
                context = context,
                input = listOf(
                    PieChartItem(
                        color = Accent1,
                        value = userInfo.gamesInfo["win"]!!,
                        description = stringResource(id = R.string.win_stat)
                    ),
                    PieChartItem(
                        color = Color.DarkGray,
                        value = userInfo.gamesInfo["tie"]!!,
                        description = stringResource(id = R.string.tie_stat)
                    ),
                    PieChartItem(
                        color = Accent2,
                        value = userInfo.gamesInfo["lose"]!!,
                        description = stringResource(id = R.string.lose_stat)
                    )
                ),
                centerText = "${stringResource(id = R.string.total_stat)}\n${userInfo.gamesInfo["total"]!!}"
            )
        }
    }
}

@Composable
fun Header(userName: String, userEmail: String){
    Box(
        modifier = Modifier
            .padding(top = 24.dp)
            .size(165.dp)
            .clip(RoundedCornerShape(50))
            .border(2.dp, Accent2, RoundedCornerShape(50)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .size(155.dp)
                .clip(RoundedCornerShape(50))
                .border(0.dp, Color.Transparent, RoundedCornerShape(50)),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = R.drawable.user_image_sample),
            contentDescription = null,
        )
    }
    Spacer(modifier = Modifier.size(12.dp))
    Text(
        text = userName,
        color = Color.White,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
    )
    Text(
        text = userEmail,
        color = Color.White
    )
}

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    context: Context,
    radius:Float = 250f,
    innerRadius:Float = 100f,
    transparentWidth:Float = 25f,
    input:List<PieChartItem>,
    centerText:String = ""
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }

    var inputList by remember {
        mutableStateOf(input)
    }
    var isCenterTapped by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = { offset ->
                            val tapAngleInDegrees = (-atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat() - 90f).mod(360f)
                            val centerClicked = if (tapAngleInDegrees < 90) {
                                offset.x < circleCenter.x + innerRadius && offset.y < circleCenter.y + innerRadius
                            } else if (tapAngleInDegrees < 180) {
                                offset.x > circleCenter.x - innerRadius && offset.y < circleCenter.y + innerRadius
                            } else if (tapAngleInDegrees < 270) {
                                offset.x > circleCenter.x - innerRadius && offset.y > circleCenter.y - innerRadius
                            } else {
                                offset.x < circleCenter.x + innerRadius && offset.y > circleCenter.y - innerRadius
                            }

                            if (centerClicked) {
                                inputList = inputList.map {
                                    it.copy(isTapped = !isCenterTapped)
                                }
                                isCenterTapped = !isCenterTapped
                            } else {
                                val anglePerValue = 360f / input.sumOf {
                                    it.value
                                }
                                var currAngle = 0f
                                inputList.forEach { pieChartInput ->
                                    currAngle += pieChartInput.value * anglePerValue
                                    if (tapAngleInDegrees < currAngle) {
                                        val description = pieChartInput.description
                                        inputList = inputList.map {
                                            if (description == it.description) {
                                                it.copy(isTapped = !it.isTapped)
                                            } else {
                                                it.copy(isTapped = false)
                                            }
                                        }
                                        return@detectTapGestures
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            circleCenter = Offset(x = width / 2f, y = height / 2f)

            val totalValue = input.sumOf {
                it.value
            }
            val anglePerValue = 360f / totalValue
            var currentStartAngle = 0f

            inputList.forEach { pieChartInput ->
                val scale = if (pieChartInput.isTapped) 1.1f else 1.0f
                val angleToDraw = pieChartInput.value * anglePerValue
                scale(scale) {
                    drawArc(
                        color = pieChartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius * 2f,
                            height = radius * 2f
                        ),
                        topLeft = Offset(
                            (width - radius * 2f) / 2f,
                            (height - radius * 2f) / 2f
                        )
                    )
                    currentStartAngle += angleToDraw
                }
                var rotateAngle = currentStartAngle - angleToDraw / 2f - 90f
                var factor = 1f
                if (rotateAngle > 90f) {
                    rotateAngle = (rotateAngle + 180).mod(360f)
                    factor = -0.92f
                }

                val percentage = (pieChartInput.value / totalValue.toFloat() * 100).toInt()

                drawContext.canvas.nativeCanvas.apply {
                    if (percentage > 3) {
                        rotate(rotateAngle) {
                            drawText(
                                "$percentage %",
                                circleCenter.x,
                                circleCenter.y + (radius - (radius - innerRadius) / 2f) * factor,
                                Paint().apply {
                                    textSize = 13.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = Color.White.toArgb()
                                }
                            )
                        }
                    }
                }
                if (pieChartInput.isTapped) {
                    val tabRotation = currentStartAngle - angleToDraw - 90f
                    rotate(tabRotation) {
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f, radius * 1.2f),
                            color = MainColorBackground,
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
                    rotate(tabRotation + angleToDraw) {
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f, radius * 1.2f),
                            color = MainColorBackground,
                            cornerRadius = CornerRadius(15f, 15f)
                        )
                    }
                    if (pieChartInput.value != 0){
                        rotate(rotateAngle) {
                            drawContext.canvas.nativeCanvas.apply {
                                drawText(
                                    "${pieChartInput.description}: ${pieChartInput.value}",
                                    circleCenter.x,
                                    circleCenter.y + radius * 1.3f * factor,
                                    Paint().apply {
                                        textSize = 22.sp.toPx()
                                        textAlign = Paint.Align.CENTER
                                        color = Color.White.toArgb()
                                        isFakeBoldText = true
                                        typeface = ResourcesCompat.getFont(context, R.font.tomorrow_regular)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (inputList.first().isTapped) {
                rotate(-90f) {
                    drawRoundRect(
                        topLeft = circleCenter,
                        size = Size(12f, radius * 1.2f),
                        color = MainColorBackground,
                        cornerRadius = CornerRadius(15f, 15f)
                    )
                }
            }
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = Color.White.copy(alpha = 0.8f).toArgb()
                        setShadowLayer(10f, 0f, 0f, MainColorBackground.toArgb())
                    }
                )
            }

            drawCircle(
                color = Color.White.copy(0.2f),
                radius = innerRadius + transparentWidth / 2f
            )

        }
        Text(
            centerText,
            modifier = Modifier
                .width(Dp(innerRadius / 1f))
                .padding(25.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
    }
}