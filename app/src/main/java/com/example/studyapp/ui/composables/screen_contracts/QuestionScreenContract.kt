package com.example.studyapp.ui.composables.screen_contracts

import com.example.studyapp.data.model.Question
import com.example.studyapp.util.Events.QuestionScreenEvents
import com.example.studyapp.util.SideEffects.QuestionScreenSideEffects
import com.example.studyapp.util.State.ScreenState.QuestionScreenState
import com.example.studyapp.util.StudyAppError.Companion.newBlankInstance

data class QuestionScreenContract(
    var screenState: QuestionScreenState = QuestionScreenState(Question.newBlankInstance(), listOf()),
    var screenEvents: QuestionScreenEvents = QuestionScreenEvents(),
    var screenSideEffects: QuestionScreenSideEffects = QuestionScreenSideEffects()
)
