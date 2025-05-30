package com.example.influencify.ui.screens.login

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.example.influencify.R
import com.example.influencify.data.Profile
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.example.influencify.ui.screens.login.data.SignUpScreenObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun SignUpScreen(
    onBackToLogin: () -> Unit,
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit
) {
    val auth = Firebase.auth
    val firestore = Firebase.firestore
    val storage = Firebase.storage
    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val nameState = remember { mutableStateOf("") }
    val bioState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val confirmPasswordState = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    Image(
        painter = painterResource(id = R.drawable.backgraund),
        contentDescription = "Background",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
        alpha = 0.3f
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 25.dp, end = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Profile image
        Image(
            painter = if (imageUri.value != null) {
                rememberAsyncImagePainter(model = imageUri.value)
            } else {
                painterResource(id = R.drawable.photoadd)
            },
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(60.dp))
                .clickable { imageLauncher.launch("image/*") },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = emailState.value,
            label = "Email",
            keyboardType = KeyboardType.Email,
            onValueChange = { emailState.value = it }
        )
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = nameState.value,
            label = "Name",
            onValueChange = { nameState.value = it }
        )
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = bioState.value,
            label = "Bio",
            maxLines = 5,
            singleLine = false,
            keyboardType = KeyboardType.Text,
            onValueChange = { bioState.value = it }
        )
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = passwordState.value,
            label = "Password",
            keyboardType = KeyboardType.Password,
            onValueChange = { passwordState.value = it }
        )
        Spacer(modifier = Modifier.height(10.dp))

        RoundedCornerTextField(
            text = confirmPasswordState.value,
            label = "Confirm Password",
            keyboardType = KeyboardType.Password,
            onValueChange = { confirmPasswordState.value = it }
        )
        Spacer(modifier = Modifier.height(10.dp))

        LoginButton(
            text = "Sign Up",
            onClick = {
                // Валидация
                if (emailState.value.isBlank() || nameState.value.isBlank() || bioState.value.isBlank()) {
                    errorState.value = "Все поля должны быть заполнены"
                    return@LoginButton
                }
                if (passwordState.value != confirmPasswordState.value) {
                    errorState.value = "Пароли не совпадают"
                    return@LoginButton
                }
                if (passwordState.value.isBlank()) {
                    errorState.value = "Пароль не может быть пустым"
                    return@LoginButton
                }

                // Регистрация пользователя
                auth.createUserWithEmailAndPassword(emailState.value, passwordState.value)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: return@addOnSuccessListener
                        val profile = Profile(
                            name = nameState.value,
                            email = emailState.value,
                            bio = bioState.value,
                            uid = uid
                        )
                        if (imageUri.value != null) {
                            // Загрузка изображения и сохранение профиля
                            val storageRef = storage.reference.child("profile_images/image-$uid.jpg")
                            storageRef.putFile(imageUri.value!!)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { url ->
                                        val profileWithImage = profile.copy(imageUrl = url.toString())
                                        saveProfileToFireStore(
                                            firestore,
                                            profileWithImage,
                                            onSaved = {
                                                onNavigateToMainScreen(
                                                    MainScreenDataObject(uid, emailState.value)
                                                )
                                            },
                                            onError = {
                                                errorState.value = "Не удалось сохранить профиль"
                                            }
                                        )
                                    }
                                }
                                .addOnFailureListener {
                                    errorState.value = "Не удалось загрузить изображение"
                                }
                        } else {
                            // Сохранение профиля без изображения
                            saveProfileToFireStore(
                                firestore,
                                profile,
                                onSaved = {
                                    onNavigateToMainScreen(
                                        MainScreenDataObject(uid, emailState.value)
                                    )
                                },
                                onError = {
                                    errorState.value = "Не удалось сохранить профиль"
                                }
                            )
                        }
                    }
                    .addOnFailureListener {
                        errorState.value = it.message ?: "Ошибка регистрации"
                    }
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (errorState.value.isNotEmpty()) {
            Text(
                text = errorState.value,
                color = Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        LoginButton(
            text = "Назад",
            onClick = { onBackToLogin() }
        )
    }
}

private fun saveProfileToFireStore(
    firestore: com.google.firebase.firestore.FirebaseFirestore,
    profile: Profile,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    firestore.collection("users")
        .document(profile.uid)
        .collection("data")
        .document("profile")
        .set(profile)
        .addOnSuccessListener { onSaved() }
        .addOnFailureListener { onError() }
}