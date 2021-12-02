package com.example.studyapp.util

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

object Navigator {
    val currentScreen: MutableState<Screens> = mutableStateOf(Screens.LoginScreen)
    private val previousScreens: MutableState<MutableList<Screens>> = mutableStateOf(mutableListOf())
    fun navigateTo(destination: Screens) {
        previousScreens.value.add(currentScreen.value)
        currentScreen.value = destination
    }

    fun navigateUp(){
        currentScreen.value = previousScreens.value.removeLast()
    }
}