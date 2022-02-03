package com.example.studyapp.util

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object Navigator {
    val TAG = "NAVIGATOR"
    val currentScreen: MutableState<Screens> = mutableStateOf(Screens.LoginScreen)

    private val previousScreens: MutableState<MutableList<Screens>> =
        mutableStateOf(mutableListOf())

    fun navigateTo(destination: Screens) {
        previousScreens.value.add(currentScreen.value)
        currentScreen.value = destination
    }

    fun navigateUp() {

        Log.e(TAG, "navigateUp: Current screen is ${currentScreen.value}")
        currentScreen.value = previousScreens.value.last()
        Log.e(
            TAG,
            "navigateUp: changed current screen to: ${currentScreen.value}, previousScreen to remove: ${previousScreens.value.last()}",
        )
        previousScreens.value.removeLast()
        Log.e(TAG, "navigateUp: new previous page is ${previousScreens.value.last()}")
    }
}