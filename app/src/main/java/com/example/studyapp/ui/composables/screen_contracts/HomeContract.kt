package com.example.studyapp.ui.composables.screen_contracts

import com.example.studyapp.util.Events.HomeScreenEvents
import com.example.studyapp.util.SideEffects.HomeScreenSideEffects
import com.example.studyapp.util.State.ScreenState.HomeScreenState

data class HomeContract(
    var screenState: HomeScreenState = HomeScreenState(),
    var screenEvent: HomeScreenEvents = HomeScreenEvents(),
    var screenSideEffects: HomeScreenSideEffects = HomeScreenSideEffects()
)