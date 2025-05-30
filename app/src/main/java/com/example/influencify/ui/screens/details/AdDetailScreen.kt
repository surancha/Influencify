package com.example.influencify.ui.screens.details

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
import coil3.compose.AsyncImage
import com.example.influencify.data.Ad
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.example.influencify.ui.screens.main.bottom_menu.BottomMenu
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdDetailScreen(
    adKey: String,
    navController: NavController
) {
    var ad by remember { mutableStateOf<Ad?>(null) }
    val db = Firebase.firestore

    // Fetch the ad from Firestore when the screen loads
    LaunchedEffect(adKey) {
        db.collection("ads")
            .document(adKey)
            .get()
            .addOnSuccessListener { document ->
                ad = document.toObject(Ad::class.java)
            }
            .addOnFailureListener {
                // Handle error (e.g., show a toast or log)
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    if (ad != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = ad!!.title,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            )
                        }

                    }

                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomMenu(navController = navController, navData = MainScreenDataObject(uid = "", email = ""))
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (ad != null) {
                // Display the ad photo
                AsyncImage(
                    model = ad!!.imageUrl,
                    contentDescription = "Ad Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Platform",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = ad!!.platform,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Description",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display the description
                Text(
                    text = ad!!.description,
                    color = Color.DarkGray,
                    fontSize = 16.sp,
                    maxLines = 10
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display the price
                Text(
                    text = "${ad!!.price} ${ad!!.currency}",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Display the URL
                Text(
                    text = ad!!.urLink,
                    color = Color.Blue,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ad!!.urLink))
                        navController.context.startActivity(intent)
                    }
                )


            } else {
                Text(
                    text = "Loading...",
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            }
        }
    }
}
//package com.example.influencify.ui.screens.ad_detail
//
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableFloatStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import coil3.compose.AsyncImage
//import com.example.influencify.data.Ad
//import com.example.influencify.data.Rating
//import com.example.influencify.ui.screens.login.LoginButton
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.ktx.Firebase
//import com.gowtham.ratingbar.RatingBar
//import com.gowtham.ratingbar.RatingBarStyle
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AdDetailScreen(
//    navController: NavController,
//    adKey: String
//) {
//    val db = Firebase.firestore
//    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//    val adState = remember { mutableStateOf<Ad?>(null) }
//    val userRating = remember { mutableFloatStateOf(0f) }
//    val averageRating = remember { mutableFloatStateOf(0f) }
//    val errorMessage = remember { mutableStateOf("") }
//
//    // Загрузка данных объявления
//    LaunchedEffect(adKey) {
//        db.collection("ads").document(adKey).get()
//            .addOnSuccessListener { document ->
//                adState.value = document.toObject(Ad::class.java)
//            }
//            .addOnFailureListener {
//                errorMessage.value = "Failed to load ad"
//            }
//
//        // Загрузка рейтинга пользователя
//        if (currentUserUid.isNotEmpty()) {
//            db.collection("ads").document(adKey).collection("rating").document(currentUserUid).get()
//                .addOnSuccessListener { document ->
//                    val rating = document.toObject(Rating::class.java)
//                    userRating.floatValue = rating?.rating ?: 0f
//                }
//        }
//
//        // Вычисление среднего рейтинга
//        db.collection("ads").document(adKey).collection("rating").get()
//            .addOnSuccessListener { querySnapshot ->
//                if (!querySnapshot.isEmpty) {
//                    val ratings = querySnapshot.documents.mapNotNull { it.toObject(Rating::class.java)?.rating }
//                    averageRating.floatValue = ratings.average().toFloat()
//                }
//            }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Ad Details") },
//                navigationIcon = {
//                    IconButton(onClick = { navController.popBackStack() }) {
//                        Icon(
//                            imageVector = Icons.Default.ArrowBack,
//                            contentDescription = "Back",
//                            tint = Color.Black
//                        )
//                    }
//                }
//            )
//        }
//    ) { paddingValues ->
//        adState.value?.let { ad ->
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues)
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Top
//            ) {
//                AsyncImage(
//                    model = ad.imageUrl,
//                    contentDescription = "Ad Image",
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .clip(RoundedCornerShape(15.dp)),
//                    contentScale = ContentScale.Crop
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                Text(
//                    text = ad.title,
//                    fontSize = 24.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = ad.description,
//                    fontSize = 16.sp,
//                    color = Color.Gray
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = "Price: ${ad.price}",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Medium
//                )
//
//                Spacer(modifier = Modifier.height(8.dp))
//
//                Text(
//                    text = "Platform: ${ad.platform}",
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Medium
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Отображение среднего рейтинга
//                Row(
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = "Average Rating: ${String.format("%.1f", averageRating.floatValue)}",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    Spacer(modifier = Modifier.size(8.dp))
//                    RatingBar(
//                        value = averageRating.floatValue,
//                        isIndicator = true,
//                        numOfStars = 5,
//                        size = 24.dp,
//                        style = RatingBarStyle.Default,
//                        onValueChange = {},
//                        onRatingChanged = {}
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // Поле для установки рейтинга
//                if (currentUserUid.isNotEmpty()) {
//                    Text(
//                        text = "Your Rating:",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                    RatingBar(
//                        value = userRating.floatValue,
//                        numOfStars = 5,
//                        size = 32.dp,
//                        isIndicator = false,
//                        style = RatingBarStyle.Default, // если RatingBarStyle поддерживает цвета — отлично
//                        onValueChange = { userRating.floatValue = it },
//                        onRatingChanged = { newRating: Float ->
//                            val ratingData = Rating(
//                                userUid = currentUserUid,
//                                rating = newRating,
//                                timestamp = System.currentTimeMillis()
//                            )
//
//                            db.collection("ads").document(adKey).collection("rating")
//                                .document(currentUserUid)
//                                .set(ratingData)
//                                .addOnSuccessListener {
//                                    db.collection("ads").document(adKey).collection("rating").get()
//                                        .addOnSuccessListener { querySnapshot ->
//                                            if (!querySnapshot.isEmpty) {
//                                                val ratings = querySnapshot.documents.mapNotNull {
//                                                    it.toObject(Rating::class.java)?.rating
//                                                }
//                                                averageRating.floatValue = ratings.average().toFloat()
//                                                db.collection("ads").document(adKey)
//                                                    .update("averageRating", averageRating.floatValue)
//                                            }
//                                        }
//                                }
//                                .addOnFailureListener {
//                                    errorMessage.value = "Failed to save rating"
//                                }
//                        }
//                    )
//
//                } else {
//                    Text(
//                        text = "Please log in to rate this ad",
//                        color = Color.Red,
//                        fontSize = 16.sp
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                if (errorMessage.value.isNotEmpty()) {
//                    Text(
//                        text = errorMessage.value,
//                        color = Color.Red,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                // Кнопка для перехода по ссылке (если нужна)
//                LoginButton(
//                    text = "Visit Link",
//                    onClick = { /* Открыть ad.urLink в браузере, если нужно */ }
//                )
//            }
//        } ?: run {
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(paddingValues),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(text = "Loading ad...", fontSize = 18.sp)
//            }
//        }
//    }
//}