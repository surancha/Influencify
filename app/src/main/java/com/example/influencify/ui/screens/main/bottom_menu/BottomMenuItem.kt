package com.example.influencify.ui.screens.main.bottom_menu

import com.example.influencify.R
import com.example.influencify.ui.screens.add_ad.data.AddScreenObject

sealed class BottomMenuItem(
    val route: Any,
    val title: String,
    val iconId: Int
) {
    object Home : BottomMenuItem(
        route = "home", // Placeholder route
        title = "Home",
        iconId = R.drawable.ic_home
    )

    object Messages : BottomMenuItem(
        route = AddScreenObject,
        title = "Messages",
        iconId = R.drawable.ic_messenge
    )

    object Add : BottomMenuItem(
        route = AddScreenObject,
        title = "Add",
        iconId = R.drawable.ic_add
    )

    object Profile : BottomMenuItem(
        route = "profile_placeholder", // Will be replaced with ProfileScreenObject
        title = "Profile",
        iconId = R.drawable.ic_profile
    )

    object Favs : BottomMenuItem(
        route = "favorites",
        title = "Favorites",
        iconId = R.drawable.ic_favs
    )
}