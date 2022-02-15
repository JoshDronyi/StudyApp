package com.example.studyapp.ui.composables.sharedcomposables

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import com.example.studyapp.R
import com.example.studyapp.data.model.User
import com.example.studyapp.util.DrawerOptions
import com.example.studyapp.util.Screens

@ExperimentalCoilApi
@Composable
fun NavDrawer(
    navController: NavController,
    user: User?,
    shouldCloseDrawer: (shouldClose: Boolean) -> Unit,
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Log.e(TAG, "NavDrawer: user  was $user")

        user?.let {
            Log.e(TAG, "NavDrawer: url was ${it.photoUrl}")
            DrawerImage(
                imageUrl = Uri.parse(it.photoUrl),
                description = it.firstName,
            )
        } ?: DrawerImage(
            imageID = R.drawable.ic_account_circle,
            description = "Account User's name",
            imageUrl = null
        )


        Divider()
        DrawerItem(text = DrawerOptions.HOME) {
            shouldCloseDrawer.invoke(true)
            handleDrawerSelection(navController, it, context)
        }
        Divider()
        DrawerItem(text = DrawerOptions.SCOREBOARD) {
            shouldCloseDrawer.invoke(true)
            handleDrawerSelection(navController, it, context)
        }
        Divider()
        DrawerItem(text = DrawerOptions.PROFILE) {
            shouldCloseDrawer.invoke(true)
            handleDrawerSelection(navController, it, context)
        }
    }
}

private fun handleDrawerSelection(
    navController: NavController,
    option: DrawerOptions,
    context: Context
) {
    val currentScreen = navController.currentBackStackEntry?.destination?.route
    when (option) {
        DrawerOptions.HOME -> {
            Log.e(TAG, "handleDrawerSelection: MainScreen Route: ${Screens.HomeScreen.route}")
            navController.navigate(Screens.HomeScreen.route)
        }
        DrawerOptions.SCOREBOARD -> {
            Toast.makeText(
                context,
                "Leader Board not yet created.",
                Toast.LENGTH_SHORT
            ).show()
        }
        DrawerOptions.PROFILE -> {
            Log.e(TAG, "handleDrawerSelection: currentScreen is $currentScreen")
            navController.navigate(Screens.ProfileScreen.route)
        }
    }
}

val TAG = "DrawerScreen"