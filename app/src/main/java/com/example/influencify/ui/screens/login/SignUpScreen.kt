package com.example.influencify.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.influencify.R
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SignUpScreen(
    onBackToLogin: () -> Unit,
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit = {} // Optional navigation callback
) {
    val textTypography1 = Typography(
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 25.sp,
            color = Color.Gray
        )
    )

    val auth = Firebase.auth
    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }

    Image(
        painter = painterResource(id = R.drawable.backgraund1),
        contentDescription = "BG",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, end = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        RoundedCornerTextField(
            text = emailState.value,
            label = "Email",
        ) {
            emailState.value = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = emailState.value,
            label = "Email",
        ) {
            emailState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = emailState.value,
            label = "Email",
        ) {
            emailState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = passwordState.value,
            label = "Password",
        ) {
            passwordState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = confirmPasswordState.value,
            label = "Confirm Password",
        ) {
            confirmPasswordState.value = it
        }
        Spacer(modifier = Modifier.height(10.dp))

        LoginButton(
            text = "Sign Up",
            onClick = {
                if (passwordState.value != confirmPasswordState.value) {
                    errorState.value = "Passwords do not match"
                    return@LoginButton
                }

                signUp(
                    auth,
                    emailState.value,
                    passwordState.value,
                    onSignUpSuccess = { navData ->
                        onNavigateToMainScreen(navData) // Navigate to MainScreen on success
                    },
                    onSignUpFailure = { error ->
                        errorState.value = error
                    }
                )
            }
        )

        if (errorState.value.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = errorState.value,
                color = Color.Red,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        LoginButton(
            text = "Back",
            onClick = {
                onBackToLogin()
            }
        )
    }
}