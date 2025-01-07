package com.adhrox.tri_xo.ui.login

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.ui.theme.Accent2
import com.adhrox.tri_xo.ui.theme.BgText
import com.adhrox.tri_xo.ui.theme.BgText2
import com.adhrox.tri_xo.ui.theme.Orange1
import com.adhrox.tri_xo.ui.theme.Orange2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToSignUp: () -> Unit,
    navigateToHome: () -> Unit
) {
    val uiStatus: LoginState by loginViewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val errorOccurredString = stringResource(id = R.string.error_occurred)
    val googleLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    loginViewModel.loginWithGoogle(account.idToken!!) { navigateToHome() }
                } catch (e: ApiException) {
                    toastMessage(context, "$errorOccurredString: ${e.message}")
                }
            }
        }

    uiStatus.error?.let {
        toastMessage(context, stringResource(id = it))
        loginViewModel.resetErrorStatus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        //.background(Background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${stringResource(id = R.string.welcome)},",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 38.sp
        )
        Text(
            text = stringResource(id = R.string.greetings),
            color = Color.White,
            fontWeight = FontWeight.Light,
            fontSize = 38.sp
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it },
            label = {
                Text(
                    text = stringResource(id = R.string.email),
                    fontSize = 14.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = BgText2,
                focusedContainerColor = Color.LightGray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Accent2
            ),
            shape = RoundedCornerShape(12.dp),
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = stringResource(id = R.string.password),
                    fontSize = 14.sp
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = BgText2,
                focusedContainerColor = Color.LightGray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                unfocusedBorderColor = Color.White,
                focusedBorderColor = Accent2
            ),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.elevatedButtonElevation(8.dp),
            onClick = { loginViewModel.login(email, password) { navigateToHome() } }
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            //colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.White),
            onClick = { navigateToSignUp() }
        ) {
            Text(
                text = stringResource(id = R.string.sign_up),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(72.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(id = R.string.another_way_login),
                fontWeight = FontWeight.ExtraLight,
                fontSize = 18.sp,
                color = Color.White
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            FloatingActionButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    loginViewModel.onGoogleLoginSelected() {
                        googleLauncher.launch(it.signInIntent)
                    }
                }
            ) {
                Image(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = ""
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            FloatingActionButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    loginViewModel.signUpAnonymously(
                        navigateToHome
                    )
                }
            ) {
                Image(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.ic_anonymously),
                    contentDescription = ""
                )
            }
            /*IconButton(
                modifier = Modifier.size(40.dp),
                onClick = {
                    loginViewModel.onGoogleLoginSelected() {
                        googleLauncher.launch(it.signInIntent)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Black.copy(
                        alpha = 0.38f
                    )
                )
            ) {
                Image(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = ""
                )
            }*/
        }
        /*Card(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 16.dp)
                .background(Color.Transparent),
                //.background(MaterialTheme.colorScheme.background.copy(alpha = 0.9f)),
            //elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column {
                    Text(
                        text = "Iniciar sesion",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "Email", fontSize = 14.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.LightGray,
                            focusedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "Contraseña", fontSize = 14.sp) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.LightGray,
                            focusedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.Start) {
                        Text(
                            modifier = Modifier.clickable {
                                loginViewModel.signUpAnonymously(
                                    navigateToHome
                                )
                            },
                            text = "Anonimo",
                            color = Color.Gray,
                            fontSize = 18.sp
                        )
                        *//*Spacer(modifier = Modifier.weight(1f))
                        Text(
                            modifier = Modifier.clickable { navigateToSignUp() },
                            text = "Registrarse",
                            color = Color.Red,
                            fontSize = 18.sp
                        )*//*
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        onClick = { loginViewModel.login(email, password) { navigateToHome() } }
                    ) {
                        Text(
                            text = "Iniciar sesion",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                        border = BorderStroke(2.dp, Color.Black),
                        onClick = { navigateToSignUp() }
                    ) {
                        Text(
                            text = "Registrarse",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FloatingActionButton(
                            onClick = {
                                loginViewModel.onGoogleLoginSelected() {
                                    googleLauncher.launch(it.signInIntent)
                                }
                            }
                        ) {
                            Image(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = ""
                            )
                        }
                        *//*IconButton(
                            modifier = Modifier.size(40.dp),
                            onClick = {
                                loginViewModel.onGoogleLoginSelected() {
                                    googleLauncher.launch(it.signInIntent)
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = Color.Black.copy(
                                    alpha = 0.38f
                                )
                            )
                        ) {
                            Image(
                                modifier = Modifier.size(28.dp),
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = ""
                            )
                        }*//*
                    }
                }
            }
        }*/
    }
    if (uiStatus.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(42.dp),
                trackColor = Orange1,
                color = Orange2,
                strokeWidth = 6.dp
            )
        }
    }
}

private fun toastMessage(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}