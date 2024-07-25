package com.adhrox.tri_xo.ui.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.ui.game.GameStatus
import com.adhrox.tri_xo.ui.theme.Background
import com.adhrox.tri_xo.ui.theme.Orange1
import com.adhrox.tri_xo.ui.theme.Orange2

@Preview
@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToHome: () -> Unit = {  }
) {
    var user by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    val uiStatus: AddNewUser by signUpViewModel.uiState.collectAsState()

    /*val view = LocalView.current

    DisposableEffect(view) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val controller = ViewCompat.getWindowInsetsController(view)
            if (isKeyboardVisible) {
                controller?.show(WindowInsetsCompat.Type.navigationBars())
            } else {
                controller?.hide(WindowInsetsCompat.Type.navigationBars())
            }
            insets
        }
        onDispose {
            ViewCompat.setOnApplyWindowInsetsListener(view, null)
        }
    }*/

    uiStatus.error?.let {
        toastMessage(LocalContext.current, it)
        signUpViewModel.resetErrorStatus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .padding(12.dp)
    ) {
        IconButton(
            modifier = Modifier.padding(top = 20.dp),
            onClick = { }
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = ""
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = "Crear cuenta",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = user,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = ""
                    )
                },
                onValueChange = {
                    user = it
                    signUpViewModel.onUserChange(it)
                },
                label = { Text(text = "Nombre de usuario", fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Color.White
                )
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = email,
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "") },
                onValueChange = {
                    email = it
                    signUpViewModel.onEmailChange(it)
                },
                label = { Text(text = "Correo electrónico", fontSize = 14.sp) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Color.White
                )
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = password,
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "") },
                onValueChange = {
                    password = it
                    signUpViewModel.onPasswordChange(it)
                },
                label = { Text(text = "Contraseña", fontSize = 14.sp) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Color.White
                )
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = repeatPassword,
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "") },
                onValueChange = {
                    repeatPassword = it
                    signUpViewModel.onRepeatPasswordChange(it)
                },
                label = { Text(text = "Repetir contraseña", fontSize = 14.sp) },
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedLabelColor = Color.White
                )
            )
            if (!uiStatus.isPasswordMatch){
                Text(text = "La contraseña no coincide", color = Color.Red)
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                colors = ButtonDefaults.buttonColors(containerColor = Orange1, disabledContainerColor = Color.Gray),
                onClick = { signUpViewModel.registerUser(user, email, password, navigateToHome) },
                enabled = uiStatus.isValidUser()
            ) {
                Text(text = "Registrarse")
            }
        }
    }
    if (uiStatus.isLoading){
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

private fun toastMessage(context: Context, msg: String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}