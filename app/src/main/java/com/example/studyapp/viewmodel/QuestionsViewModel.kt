package com.example.studyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyapp.model.Question

class QuestionsViewModel : ViewModel() {
    private val _questions = MutableLiveData<List<Question>>()
    val questions : LiveData<List<Question>>
        get() = _questions

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion : LiveData<Question>
        get() = _currentQuestion

    val currentWeek = MutableLiveData("Week")


    fun getNewQuestion() {
        _questions.value?.let { questions ->
            currentQuestion.value?.let { currentQuestion ->
                if (currentQuestion.id <= questions.lastIndex)
                    _currentQuestion.postValue(questions[currentQuestion.id])
            }
        }
    }

    fun setCurrentQuestion(question : Question) {
        _currentQuestion.postValue(question)
    }

    fun setQuestions(listQuesitons: List<Question>) {
        _questions.postValue(listQuesitons)
    }
}