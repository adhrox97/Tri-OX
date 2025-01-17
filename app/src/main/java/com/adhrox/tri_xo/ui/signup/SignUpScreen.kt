package com.adhrox.tri_xo.ui.signup

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.ui.theme.Accent1
import com.adhrox.tri_xo.ui.theme.Accent2
import com.adhrox.tri_xo.ui.theme.BgText2
import com.adhrox.tri_xo.ui.theme.MainColorBackground

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    var user by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    val uiStatus: AddNewUser by signUpViewModel.uiState.collectAsState()

    uiStatus.error?.let {
        toastMessage(LocalContext.current, stringResource(id = it))
        signUpViewModel.resetErrorStatus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            //.background(Background)
            .padding(12.dp)
    ) {
        Spacer(modifier = Modifier.height(84.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.background(Background)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.Create_account),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.start_now),
                color = Color.White,
                fontWeight = FontWeight.Light,
                fontSize = 38.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextForm(
                value = user,
                onValueChange = {
                    user = it
                    signUpViewModel.onUserChange(it)
                },
                leadingIconImageVector = Icons.Default.AccountBox,
                textLabel = stringResource(id = R.string.username_label)
            )
            if (!uiStatus.isValidUserName){
                Text(
                    text = stringResource(id = R.string.username_symbols_error),
                    //text = "El Nombre de usuario no debe contener los simbolos \"\\\" ó \"-\" ó \":\" ó espacios",
                    color = Accent2,
                    fontWeight = FontWeight.Light
                )
            }
            OutlinedTextForm(
                value = email,
                onValueChange = {
                    email = it
                    signUpViewModel.onEmailChange(it)
                },
                leadingIconImageVector = Icons.Default.Email,
                textLabel = stringResource(id = R.string.email_label)
            )
            OutlinedTextForm(
                value = password,
                onValueChange = {
                    password = it
                    signUpViewModel.onPasswordChange(it)
                },
                leadingIconImageVector = Icons.Default.Lock,
                textLabel = stringResource(id = R.string.password_label),
                visualTransformation = PasswordVisualTransformation(),
            )
            OutlinedTextForm(
                value = repeatPassword,
                onValueChange = {
                    repeatPassword = it
                    signUpViewModel.onRepeatPasswordChange(it)
                },
                leadingIconImageVector = Icons.Default.Lock,
                textLabel = stringResource(id = R.string.repeat_password_label),
                visualTransformation = PasswordVisualTransformation(),
            )
            if (!uiStatus.isPasswordMatch){
                Text(
                    text = stringResource(id = R.string.password_not_match_error),
                    color = Accent2,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    //.height(54.dp)
                    .imePadding(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, disabledContainerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp),
                onClick = { signUpViewModel.registerUser(user, email, password, navigateToHome) },
                enabled = uiStatus.isValidUser()
            ) {
                Text(
                    text = stringResource(id = R.string.sign_up),
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    fontSize = 20.sp
                )
            }
        }
    }
    if (uiStatus.isLoading){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(42.dp),
                trackColor = MainColorBackground,
                color = Accent1,
                strokeWidth = 6.dp
            )
        }
    }
}

@Composable
fun OutlinedTextForm(
    value: String,
    onValueChange: (String) -> Unit,
    textLabel: String,
    leadingIconImageVector: ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
){
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        leadingIcon = { Icon(imageVector = leadingIconImageVector, contentDescription = null) },
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
        label = { Text(text = textLabel, fontSize = 14.sp) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = BgText2,
            focusedContainerColor = Color.LightGray,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedBorderColor = Accent1
        )
    )
}

private fun toastMessage(context: Context, msg: String){
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}