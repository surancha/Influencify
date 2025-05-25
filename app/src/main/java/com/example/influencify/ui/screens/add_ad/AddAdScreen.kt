package com.example.influencify.ui.screens.add_ad

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.influencify.R
import com.example.influencify.data.Ad
import com.example.influencify.ui.screens.login.LoginButton
import com.example.influencify.ui.screens.login.RoundedCornerTextField
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.example.influencify.ui.screens.main.bottom_menu.BottomMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

@Composable
fun AddAdScreen(
    navController: NavController,
    navData: MainScreenDataObject
) {
    val selectedPlatform = remember { mutableStateOf("") }
    val selectedCategory = remember { mutableStateOf("") }
    val selectedCurrency = remember { mutableStateOf("") }
    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val urLink = remember { mutableStateOf("") }
    val price = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val firestore = remember { Firebase.firestore }
    val storage = remember { Firebase.storage }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri.value = uri
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomMenu(navController = navController, navData = navData)
        }
    ) { paddingValues ->
        Image(
            painter = painterResource(id = R.drawable.backgraund1),
            contentDescription = "BG",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(start = 25.dp, end = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = if (selectedImageUri.value != null) {
                    rememberAsyncImagePainter(model = selectedImageUri.value)
                } else {
                    painterResource(id = R.drawable.photoadd)
                },
                contentDescription = "Ad Image",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .clickable(
                        onClick = {
                            imageLauncher.launch("image/*")
                        }
                    )
            )
            Text(
                text = "Add your ad",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
            ) {
                RoundedCornerDropDownMеnu { selectedItem ->
                    selectedPlatform.value = selectedItem
                }
                Categorydropmenu { selectedItem ->
                    selectedCategory.value = selectedItem }
            }
            Spacer(modifier = Modifier.height(10.dp))

            RoundedCornerTextField(
                text = title.value,
                label = "Title",
                keyboardType = KeyboardType.Text
            ) {
                title.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            RoundedCornerTextField(
                maxLines = 5,
                singleLine = false,
                text = description.value,
                label = "Description",
                keyboardType = KeyboardType.Text
            ) {
                description.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            RoundedCornerTextField(
                text = urLink.value,
                label = "URL",
                keyboardType = KeyboardType.Text
            ) {
                urLink.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            RoundedCornerTextField(
                text = price.value,
                label = "Price",
                keyboardType = KeyboardType.Number,
                onValueChange = { newValue ->
                    // Фильтр: только цифры, не более 8 символов
                    val filteredValue = newValue.filter { it.isDigit() }.take(8)
                    price.value = filteredValue
                    errorMessage.value = "" // Сбрасываем ошибку при вводе
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            PriceDropMenu { selectedItem ->
                selectedCurrency.value = selectedItem }



            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.height(10.dp))

            // Отображение сообщения об ошибке
            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            LoginButton(
                text = "Save",
                onClick = {
                    // Валидация полей
                    when {
                        title.value.isBlank() -> errorMessage.value = "Title is required"
                        description.value.isBlank() -> errorMessage.value = "Description is required"
                        urLink.value.isBlank() -> errorMessage.value = "URL is required"
                        price.value.isBlank() -> errorMessage.value = "Price is required"
                        price.value.toLongOrNull() == null -> errorMessage.value = "Price must be a valid number"
                        price.value.toLong() < 1 || price.value.toLong() > 10_000_000 -> errorMessage.value = "Price must be between 1 and 10,000,000"
                        selectedPlatform.value.isEmpty() -> errorMessage.value = "Please select a platform"
                        selectedImageUri.value == null -> errorMessage.value = "Please upload an image"
                        currentUserUid.isBlank() -> errorMessage.value = "User not authenticated"
                        else -> {
                            errorMessage.value = ""
                            saveAdImage(
                                selectedImageUri.value!!,
                                storage,
                                firestore,
                                Ad(
                                    title = title.value,
                                    description = description.value,
                                    price = price.value,
                                    urLink = urLink.value,
                                    platform = selectedPlatform.value,
                                    category = selectedCategory.value,
                                    currency = selectedCurrency.value,
                                    creatorUid = currentUserUid
                                ),
                                onSaved = {
                                    navController.navigate(navData) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = false
                                        }
                                        launchSingleTop = true
                                    }
                                },
                                onError = {
                                    errorMessage.value = "Failed to save ad"
                                }
                            )
                        }
                    }
                }
            )
        }
    }
}

private fun saveAdImage(
    uri: Uri,
    storage: FirebaseStorage,
    firestore: FirebaseFirestore,
    ad: Ad,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val timeStamp = System.currentTimeMillis()
    val storageRef = storage.reference
        .child("ad_images")
        .child("image-$timeStamp.jpg")
    val uploadTask = storageRef.putFile(uri)
    uploadTask.addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { url ->
            saveAdToFireStore(
                firestore,
                url.toString(),
                ad,
                onSaved = { onSaved() },
                onError = { onError() }
            )
        }
    }.addOnFailureListener {
        onError()
    }
}

private fun saveAdToFireStore(
    firestore: FirebaseFirestore,
    url: String,
    ad: Ad,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val db = firestore.collection("ads")
    val key = db.document().id
    db.document(key)
        .set(
            ad.copy(
                key = key,
                imageUrl = url,
                creatorUid = ad.creatorUid
            )
        )
        .addOnSuccessListener {
            onSaved()
        }
        .addOnFailureListener {
            onError()
        }
}
//package com.example.influencify.ui.screens.add_ad
//
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import coil3.compose.rememberAsyncImagePainter
//import com.example.influencify.R
//import com.example.influencify.data.Ad
//import com.example.influencify.ui.screens.login.LoginButton
//import com.example.influencify.ui.screens.login.RoundedCornerTextField
//import com.example.influencify.ui.screens.login.data.MainScreenDataObject
//import com.example.influencify.ui.screens.main.bottom_menu.BottomMenu
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.ktx.firestore
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.ktx.Firebase
//import com.google.firebase.storage.ktx.storage
//
//@Composable
//fun AddAdScreen(
//    navController: NavController,
//    navData: MainScreenDataObject
//) {
//    val selectedPlatform = remember { mutableStateOf("") } // Track selected platform
//    val title = remember { mutableStateOf("") }
//    val description = remember { mutableStateOf("") }
//    val urLink = remember { mutableStateOf("") }
//    val price = remember { mutableStateOf("") }
//    val errorMessage = remember { mutableStateOf("") } // For validation feedback
//    val firestore = remember { Firebase.firestore }
//    val storage = remember { Firebase.storage }
//    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
//    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//
//    val imageLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri ->
//        selectedImageUri.value = uri
//    }
//
//    Scaffold(
//        modifier = Modifier.fillMaxSize(),
//        bottomBar = {
//            BottomMenu(navController = navController, navData = navData)
//        }
//    ) { paddingValues ->
//        Image(
//            painter = painterResource(id = R.drawable.backgraund1),
//            contentDescription = "BG",
//            modifier = Modifier.fillMaxSize(),
//            contentScale = ContentScale.Crop,
//            alpha = 0.2f
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(start = 25.dp, end = 25.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Image(
//                painter = if (selectedImageUri.value != null) {
//                    rememberAsyncImagePainter(model = selectedImageUri.value)
//                } else {
//                    painterResource(id = R.drawable.defoldimg)
//                },
//                contentDescription = "Ad Image",
//                modifier = Modifier
//                    .size(200.dp)
//                    .clip(RoundedCornerShape(15.dp))
//            )
//            Text(
//                text = "Add your ad",
//                color = Color.Black,
//                fontWeight = FontWeight.Bold,
//                fontSize = 40.sp
//            )
//
//            Spacer(modifier = Modifier.height(10.dp))
//            RoundedCornerDropDownMеnu { selectedItem ->
//                selectedPlatform.value = selectedItem
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//
//            RoundedCornerTextField(
//                text = title.value,
//                label = "Title"
//            ) {
//                title.value = it
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//
//            RoundedCornerTextField(
//                maxLines = 5,
//                singleLine = false,
//                text = description.value,
//                label = "Description"
//            ) {
//                description.value = it
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//
//            RoundedCornerTextField(
//                text = urLink.value,
//                label = "URL"
//            ) {
//                urLink.value = it
//            }
//            Spacer(modifier = Modifier.height(10.dp))
//
//            RoundedCornerTextField(
//                text = price.value,
//                label = "Price",
//                keyboardType = KeyboardType.Number,
//                onValueChange = { newValue ->
//                    // Filter to allow only digits
//                    val filteredValue = newValue.filter { it.isDigit() }.take(8)
//                    price.value = filteredValue
//                })
//            Spacer(modifier = Modifier.height(10.dp))
//
//            LoginButton(
//                text = "Select Image",
//                onClick = {
//                    imageLauncher.launch("image/*")
//                }
//            )
//            Spacer(modifier = Modifier.height(10.dp))
//
//            // Display error message if validation fails
//            if (errorMessage.value.isNotEmpty()) {
//                Text(
//                    text = errorMessage.value,
//                    color = Color.Red,
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(10.dp))
//            }
//
//            LoginButton(
//                text = "Save",
//                onClick = {
//                    // Validation checks
//                    when {
//                        title.value.isBlank() -> errorMessage.value = "Title is required"
//                        description.value.isBlank() -> errorMessage.value = "Description is required"
//                        urLink.value.isBlank() -> errorMessage.value = "URL is required"
//                        price.value.isBlank() -> errorMessage.value = "Price is required"
//                        selectedPlatform.value.isEmpty() -> errorMessage.value = "Please select a platform"
//                        selectedImageUri.value == null -> errorMessage.value = "Please upload an image"
//                        currentUserUid.isBlank() -> errorMessage.value = "User not authenticated"
//                        else -> {
//                            errorMessage.value = ""
//                            saveAdImage(
//                                selectedImageUri.value!!,
//                                storage,
//                                firestore,
//                                Ad(
//                                    title = title.value,
//                                    description = description.value,
//                                    price = price.value,
//                                    urLink = urLink.value,
//                                    platform = selectedPlatform.value,
//                                    creatorUid = currentUserUid
//                                ),
//                                onSaved = {
//                                    navController.navigate(navData) {
//                                        popUpTo(navController.graph.startDestinationId) {
//                                            inclusive = false
//                                        }
//                                        launchSingleTop = true
//                                    }
//                                },
//                                onError = {
//                                    errorMessage.value = "Failed to save ad"
//                                }
//                            )
//                        }
//                    }
//                }
//            )
//        }
//    }
//}
//
//private fun saveAdImage(
//    uri: Uri,
//    storage: FirebaseStorage,
//    firestore: FirebaseFirestore,
//    ad: Ad,
//    onSaved: () -> Unit,
//    onError: () -> Unit
//) {
//    val timeStamp = System.currentTimeMillis()
//    val storageRef = storage.reference
//        .child("ad_images")
//        .child("image-$timeStamp.jpg")
//    val uploadTask = storageRef.putFile(uri)
//    uploadTask.addOnSuccessListener {
//        storageRef.downloadUrl.addOnSuccessListener { url ->
//            saveAdToFireStore(
//                firestore,
//                url.toString(),
//                ad,
//                onSaved = { onSaved() },
//                onError = { onError() }
//            )
//        }
//    }.addOnFailureListener {
//        onError()
//    }
//}
//
//private fun saveAdToFireStore(
//    firestore: FirebaseFirestore,
//    url: String,
//    ad: Ad,
//    onSaved: () -> Unit,
//    onError: () -> Unit
//) {
//    val db = firestore.collection("ads")
//    val key = db.document().id
//    db.document(key)
//        .set(
//            ad.copy(
//                key = key,
//                imageUrl = url,
//                creatorUid = ad.creatorUid
//            )
//        )
//        .addOnSuccessListener {
//            onSaved()
//        }
//        .addOnFailureListener {
//            onError()
//        }
//}