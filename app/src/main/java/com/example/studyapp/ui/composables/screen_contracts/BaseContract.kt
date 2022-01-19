package com.example.studyapp.ui.composables.screen_contracts

import android.media.metrics.Event
import com.example.studyapp.util.SideEffects
import com.example.studyapp.util.State

open class BaseContract {
    open lateinit var screenState: State.ScreenState
    open lateinit var screenEvent: Event
    open lateinit var screenSideEffects: SideEffects
}