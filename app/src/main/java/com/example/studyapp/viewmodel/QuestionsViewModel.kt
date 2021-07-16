package com.example.studyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyapp.model.Question

class QuestionsViewModel : ViewModel() {
    val _questions = MutableLiveData<List<Question>>()
    private val questions: LiveData<List<Question>>
        get() = _questions

    val currentQuestion = MutableLiveData<Question>()

    val currentWeek = MutableLiveData("Week")
}