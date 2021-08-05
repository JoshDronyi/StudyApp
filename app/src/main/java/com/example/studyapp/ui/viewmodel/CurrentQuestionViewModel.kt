package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.repo.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CurrentQuestionViewModel @Inject constructor(private val repo: QuestionRepository) :
    ViewModel() {

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion


    fun setCurrentQuestion(question: Question) = _currentQuestion.postValue(question)

    fun shouldShowNextQuestion(questions: List<Question>): Boolean {
        return currentQuestion.value?.let { currentQuestion ->
            Log.e("Question Number", currentQuestion.questionNumber.toString())
            Log.e("Last Number", questions.lastIndex.toString())
            if (currentQuestion.questionNumber <= questions.lastIndex) {
                _currentQuestion.postValue(questions[currentQuestion.questionNumber])
                true
            } else
                false
        } ?: false
    }


}