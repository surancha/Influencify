package com.example.influencify.ui.screens.pdetales

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.res.painterResource
import coil3.compose.AsyncImage
import com.example.influencify.R
import com.example.influencify.data.Ad
import com.example.influencify.data.Favorite
import com.example.influencify.data.Profile
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.example.influencify.ui.screens.main.AdListItemUi
import com.example.influencify.ui.screens.main.bottom_menu.BottomMenu
import com.example.influencify.ui.screens.main.onFavorites
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetalesScreen(
    profileuid: String,
    navController: NavController
) {
    val db = Firebase.firestore
    var profile by remember { mutableStateOf<Profile?>(null) }
    var userAds by remember { mutableStateOf<List<Ad>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Загрузка профиля
    LaunchedEffect(profileuid) {
        db.collection("users")
            .document(profileuid)
            .collection("data")
            .document("profile")
            .get()
            .addOnSuccessListener { document ->
                profile = document.toObject(Profile::class.java)
                // Загружаем объявления только после получения профиля
                if (profile != null) {
                    db.collection("ads")
                        .whereEqualTo("creatorUid", profile!!.uid)
                        .get()
                        .addOnSuccessListener { task ->
                            userAds = task.toObjects(Ad::class.java)
                            isLoading = false
                        }
                        .addOnFailureListener { exception ->
                            errorMessage = "${exception.message}"
                            isLoading = false
                        }
                }
            }
            .addOnFailureListener { exception ->
                errorMessage = "${exception.message}"
                isLoading = false
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Здесь можно добавить навигацию к экрану сообщений
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_messenge),
                            contentDescription = "Сообщения",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                errorMessage.isNotEmpty() -> {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
                isLoading -> {
                    CircularProgressIndicator()
                }
                profile == null -> {
                    Text(
                        text = "Профиль не найден",
                        color = Color.Gray,
                        fontSize = 18.sp
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = profile!!.imageUrl,
                            contentDescription = "Фото профиля",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = profile!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(userAds.size) { index ->
                                AdListItemUi(
                                    ad = userAds[index],
                                    navController = navController,
                                    onFavClick = {

                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdItem(ad: Ad, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate("ad_detail/${ad.key}") }
            .padding(vertical = 8.dp)
    ) {
        Column {
            AsyncImage(
                model = ad.imageUrl,
                contentDescription = "Изображение объявления",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ad.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = ad.description,
                fontSize = 16.sp,
                color = Color.Gray
            )
        }
    }
}