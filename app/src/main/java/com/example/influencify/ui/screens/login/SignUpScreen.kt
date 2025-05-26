package com.example.influencify.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.influencify.R
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun SignUpScreen(
    onBackToLogin: () -> Unit,
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit = {}
) {
    val textTypography1 = Typography(
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 25.sp,
            color = Color.Gray
        )
    )

    val auth = Firebase.auth
    val db = Firebase.firestore
    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val nameState = remember { mutableStateOf("") }
    val bioState = remember { mutableStateOf("") }
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
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = emailState.value,
            label = "Email",
            keyboardType = KeyboardType.Email
        ) {
            emailState.value = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = nameState.value,
            label = "Name"
        ) {
            nameState.value = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            maxLines = 5,
            singleLine = false,
            text = bioState.value,
            label = "Bio",
            keyboardType = KeyboardType.Text
        ) {
            bioState.value = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = passwordState.value,
            label = "Password",
            keyboardType = KeyboardType.Password
        ) {
            passwordState.value = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = confirmPasswordState.value,
            label = "Confirm Password",
            keyboardType = KeyboardType.Password
        ) {
            confirmPasswordState.value = it
        }

        Spacer(modifier = Modifier.height(10.dp))

        LoginButton(
            text = "Sign Up",
            onClick = {
                if (emailState.value.isBlank() || nameState.value.isBlank() || bioState.value.isBlank()) {
                    errorState.value = "All fields must be filled"
                    return@LoginButton
                }
                if (passwordState.value != confirmPasswordState.value) {
                    errorState.value = "Passwords do not match"
                    return@LoginButton
                }

                signUp(
                    auth,
                    emailState.value,
                    passwordState.value,
                    onSignUpSuccess = { navData ->
                        // Save profile data to Firestore
                        val profileData = hashMapOf(
                            "email" to emailState.value,
                            "name" to nameState.value,
                            "bio" to bioState.value,
                            "uid" to navData.uid
                        )
                        db.collection("users")
                            .document(navData.uid)
                            .collection("data")
                            .document("profile")
                            .set(profileData)
                            .addOnSuccessListener {
                                onNavigateToMainScreen(navData)
                            }
                            .addOnFailureListener { error ->
                                errorState.value = "Failed to save profile: ${error.message}"
                            }
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