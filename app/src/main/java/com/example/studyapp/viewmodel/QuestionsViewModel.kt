package com.example.studyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.model.Question
import com.example.studyapp.repo.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.collect


@HiltViewModel
class QuestionsViewModel @Inject constructor(private val repository : RepositoryInterface) : ViewModel() {
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
                if (currentQuestion.questionNumber <= questions.lastIndex)
                    _currentQuestion.postValue(questions[currentQuestion.questionNumber])
            }
        }
    }

    fun setCurrentQuestion(question : Question) {
        _currentQuestion.postValue(question)
    }

    fun setQuestions(listQuesitons: List<Question>) {
        _questions.postValue(listQuesitons)
    }

    fun getQuestions(week: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getQuestionsByWeek(week = week).collect {
                _questions.postValue(it)
            }
        }
    }
}