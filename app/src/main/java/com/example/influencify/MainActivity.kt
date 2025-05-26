package com.example.influencify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.influencify.ui.screens.add_ad.AddAdScreen
import com.example.influencify.ui.screens.add_ad.data.AddScreenObject
import com.example.influencify.ui.screens.categories.CategoriesScreen
import com.example.influencify.ui.screens.categories.data.CategoriesScreenObject
import com.example.influencify.ui.screens.details.AdDetailScreen
import com.example.influencify.ui.screens.favorites.FavoritesScreen
import com.example.influencify.ui.screens.favorites.data.FavoritesScreenObject
import com.example.influencify.ui.screens.login.LoginScreen
import com.example.influencify.ui.screens.login.SignUpScreen
import com.example.influencify.ui.screens.login.data.LoginScreenObject
import com.example.influencify.ui.screens.login.data.MainScreenDataObject
import com.example.influencify.ui.screens.login.data.SignUpScreenObject
import com.example.influencify.ui.screens.main.MainScreen
import com.example.influencify.ui.screens.profile.EditProfileScreen
import com.example.influencify.ui.screens.profile.ProfileScreen
import com.example.influencify.ui.screens.profile.data.EditProfileScreenObject
import com.example.influencify.ui.screens.profile.data.ProfileScreenObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fs = Firebase.firestore
        val auth = Firebase.auth
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = LoginScreenObject
            ) {
                composable<LoginScreenObject> {
                    LoginScreen { navData ->
                        navController.navigate(navData)
                    }
                }
                composable<MainScreenDataObject> { navEntry ->
                    val navData = navEntry.toRoute<MainScreenDataObject>()
                    MainScreen(
                        navData = navData,
                        navController = navController,
                        selectedCategory = navData.selectedCategory,
                    )
                }
                composable<AddScreenObject> {
                    val navData = navController.previousBackStackEntry
                        ?.toRoute<MainScreenDataObject>()
                        ?: MainScreenDataObject("", "")
                    AddAdScreen(
                        navController = navController,
                        navData = navData
                    )
                }
                composable<SignUpScreenObject> {
                    SignUpScreen(
                        onBackToLogin = { navController.navigate(LoginScreenObject) },
                        onNavigateToMainScreen = { navData -> navController.navigate(navData) }
                    )
                }
                composable<FavoritesScreenObject> { navEntry ->
                    val navData = navEntry.toRoute<FavoritesScreenObject>()
                    FavoritesScreen(
                        navData = navData,
                        navController = navController
                    )
                }
                composable<ProfileScreenObject> { navEntry ->
                    val navData = navEntry.toRoute<ProfileScreenObject>()
                    ProfileScreen(
                        navData = navData,
                        navController = navController
                    )
                }
                composable<EditProfileScreenObject> { navEntry ->
                    val navData = navEntry.toRoute<ProfileScreenObject>()
                    EditProfileScreen(
                        navData = navData,
                        navController = navController
                    )
                }
                composable("adDetail/{adKey}") { backStackEntry ->
                    val adKey = backStackEntry.arguments?.getString("adKey") ?: ""
                    AdDetailScreen(
                        adKey = adKey,
                        navController = navController
                    )
                }
                composable<CategoriesScreenObject> {
                    val navData = navController.previousBackStackEntry
                        ?.toRoute<MainScreenDataObject>()
                        ?: MainScreenDataObject("", "")
                    CategoriesScreen(
                        navController = navController,
                        onCategorieSelected = { category ->
                            val updatedNavData = navData.copy(selectedCategory = category)
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "selectedCategory",
                                category
                            )
                        }
                    )
                }
            }
        }
    }
}