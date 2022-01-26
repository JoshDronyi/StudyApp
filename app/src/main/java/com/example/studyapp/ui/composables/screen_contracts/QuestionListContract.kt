package com.example.studyapp.ui.composables.screen_contracts

import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.util.Events.QuestionListScreenEvents
import com.example.studyapp.util.SideEffects.QuestionListScreenSideEffects
import com.example.studyapp.util.State.ScreenState.QuestionListScreenState

data class QuestionListContract(
    var screenState: QuestionListScreenState = QuestionListScreenState(
        listOf(),
        StudentProgress(0, 0, 0, 0)
    ),
    var screenEvent: QuestionListScreenEvents = QuestionListScreenEvents(),
    var sideEffects: QuestionListScreenSideEffects = QuestionListScreenSideEffects()
)