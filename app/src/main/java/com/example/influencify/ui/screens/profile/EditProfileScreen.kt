package com.example.influencify.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.influencify.R
import com.example.influencify.ui.screens.login.RoundedCornerTextField
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.example.influencify.ui.screens.main.bottom_menu.BottomMenu
import com.example.influencify.ui.screens.profile.data.ProfileScreenObject
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

@Composable
fun EditProfileScreen(
    navData: ProfileScreenObject,
    navController: NavController
) {
    val db = remember { Firebase.firestore }
    val storage = remember { Firebase.storage }
    val profileData = remember { mutableStateOf<Map<String, Any>?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf("") }
    val nameState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val bioState = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageUrl = remember { mutableStateOf("") }

    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri.value = uri
    }

    LaunchedEffect(Unit) {
        // Fetch user profile data
        db.collection("users")
            .document(navData.uid)
            .collection("data")
            .document("profile")
            .get()
            .addOnSuccessListener { document ->
                profileData.value = document.data
                nameState.value = document.getString("name") ?: ""
                emailState.value = document.getString("email") ?: ""
                bioState.value = document.getString("bio") ?: ""
                imageUrl.value = document.getString("imageUrl") ?: ""
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                errorMessage.value = "Failed to load profile: ${exception.message}"
                isLoading.value = false
            }
    }

    Scaffold(
        bottomBar = {
            MainScreenDataObject(uid = navData.uid, email = emailState.value).let { mainScreenData ->
                BottomMenu(
                    navController = navController,
                    navData = mainScreenData
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Edit Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading.value -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                errorMessage.value.isNotEmpty() -> {
                    Text(
                        text = errorMessage.value,
                        color = Color.Red,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                profileData.value != null -> {
                    // Profile image
                    Image(
                        painter = if (imageUri.value != null) {
                            rememberAsyncImagePainter(model = imageUri.value)
                        } else if (imageUrl.value.isNotEmpty()) {
                            rememberAsyncImagePainter(model = imageUrl.value)
                        } else {
                            painterResource(id = R.drawable.photoadd) // Placeholder image
                        },
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp))
                            .clickable { imageLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    RoundedCornerTextField(
                        text = nameState.value,
                        label = "Name",
                        keyboardType = KeyboardType.Text
                    ) {
                        nameState.value = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    RoundedCornerTextField(
                        text = emailState.value,
                        label = "Email",
                        keyboardType = KeyboardType.Email
                    ) {
                        emailState.value = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    RoundedCornerTextField(
                        maxLines = 5,
                        singleLine = false,
                        text = bioState.value,
                        label = "Bio",
                        keyboardType = KeyboardType.Text
                    ) {
                        bioState.value = it
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (nameState.value.isBlank() || emailState.value.isBlank() || bioState.value.isBlank()) {
                                errorMessage.value = "All fields must be filled"
                                return@Button
                            }

                            // Upload image to Firebase Storage if selected
                            val profileDataUpdate = hashMapOf(
                                "email" to emailState.value,
                                "name" to nameState.value,
                                "bio" to bioState.value,
                                "uid" to navData.uid
                            )

                            if (imageUri.value != null) {
                                val storageRef = storage.reference.child("profile_images/${navData.uid}.jpg")
                                storageRef.putFile(imageUri.value!!)
                                    .addOnSuccessListener {
                                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                                            profileDataUpdate["imageUrl"] = uri.toString()
                                            saveProfileData(db, navData.uid, profileDataUpdate, errorMessage)
                                        }
                                    }
                                    .addOnFailureListener { error ->
                                        errorMessage.value = "Failed to upload image: ${error.message}"
                                    }
                            } else {
                                profileDataUpdate["imageUrl"] = imageUrl.value
                                saveProfileData(db, navData.uid, profileDataUpdate, errorMessage)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = "Save Changes",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                else -> {
                    Text(
                        text = "No profile data available.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

private fun saveProfileData(
    db: com.google.firebase.firestore.FirebaseFirestore,
    uid: String,
    profileData: HashMap<String, String>,
    errorMessage: androidx.compose.runtime.MutableState<String>
) {
    db.collection("users")
        .document(uid)
        .collection("data")
        .document("profile")
        .set(profileData)
        .addOnSuccessListener {
            errorMessage.value = "Profile updated successfully"
        }
        .addOnFailureListener { error ->
            errorMessage.value = "Failed to update profile: ${error.message}"
        }
}