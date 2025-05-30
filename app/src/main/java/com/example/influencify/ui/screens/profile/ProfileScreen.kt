package com.example.influencify.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.influencify.R
import com.example.influencify.data.Ad
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.example.influencify.ui.screens.main.AdListItemUi
import com.example.influencify.ui.screens.main.bottom_menu.BottomMenu
import com.example.influencify.ui.screens.profile.data.EditProfileScreenObject
import com.example.influencify.ui.screens.profile.data.ProfileScreenObject
import com.example.influencify.ui.theme.MyGrayL
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun ProfileScreen(
    navData: ProfileScreenObject,
    navController: NavController
) {
    val db = remember { Firebase.firestore }
    val userAds = remember { mutableStateOf<List<Ad>>(emptyList()) }
    val profileData = remember { mutableStateOf<Map<String, Any>?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        // Fetch user profile data
        db.collection("users")
            .document(navData.uid)
            .collection("data")
            .document("profile")
            .get()
            .addOnSuccessListener { document ->
                profileData.value = document.data
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                errorMessage.value = "Failed to load profile: ${exception.message}"
                isLoading.value = false
            }

        // Fetch user ads
        db.collection("ads")
            .whereEqualTo("creatorUid", navData.uid)
            .get()
            .addOnSuccessListener { task ->
                userAds.value = task.toObjects(Ad::class.java)
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                errorMessage.value = "Failed to load ads: ${exception.message}"
                isLoading.value = false
            }
    }

    Scaffold(
        bottomBar = {
            MainScreenDataObject(uid = navData.uid, email = profileData.value?.get("email")?.toString() ?: "").let { mainScreenData ->
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
                text = "${profileData.value?.get("name") ?: "N/A"}",
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
                        painter = if (profileData.value?.get("imageUrl")?.toString()?.isNotEmpty() == true) {
                            rememberAsyncImagePainter(model = profileData.value?.get("imageUrl"))
                        } else {
                            painterResource(id = R.drawable.photoadd) // Placeholder image
                        },
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(60.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Name",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "${profileData.value?.get("name") ?: "N/A"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(15.dp))

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MyGrayL))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Email: ${profileData.value?.get("email") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MyGrayL))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Bio: ${profileData.value?.get("bio") ?: "N/A"}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(MyGrayL))
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            navController.navigate(EditProfileScreenObject(navData.uid))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = "Change",
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

            Text(
                text = "Your Ads",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            when {
                userAds.value.isEmpty() -> {
                    Text(
                        text = "You haven't created any ads yet.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(userAds.value) { ad ->
                            ProfileAdListItemUi(
                                ad = ad,
                                navController = navController,
                                onDeleteclick = {
                                    db.collection("ads").document(ad.key)
                                        .delete()
                                        .addOnSuccessListener {
                                            userAds.value =
                                                userAds.value.filter { it.key != ad.key }
                                        }
                                        .addOnFailureListener { exception ->
                                            errorMessage.value =
                                                "Failed to delete ad: ${exception.message}"
                                        }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}