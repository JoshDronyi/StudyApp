package com.example.studyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.model.ApiState
import com.example.studyapp.model.Question
import com.example.studyapp.repo.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.collect


@HiltViewModel
class QuestionsViewModel @Inject constructor(private val repository: RepositoryInterface) :
    ViewModel() {
    private val _apiState = MutableLiveData<ApiState<List<Question>>>(ApiState.Sleep)
    val apiState: LiveData<ApiState<List<Question>>>
        get() = _apiState

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
        get() = _questions

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion

    val currentWeek = MutableLiveData<String>()


    fun getNewQuestion() {
        _questions.value?.let { questions ->
            currentQuestion.value?.let { currentQuestion ->
                if (currentQuestion.questionNumber <= questions.lastIndex)
                    _currentQuestion.postValue(questions[currentQuestion.questionNumber])
            }
        }
    }

    fun setCurrentQuestion(question: Question) {
        _currentQuestion.postValue(question)
    }

    fun setQuestions(listQuestions: List<Question>) {
        _questions.postValue(listQuestions)
    }


    fun getQuestions(week: String) {
        currentWeek.postValue(week)
        viewModelScope.launch {
            repository.getQuestionsByWeek(week = week).collect {
                repository.saveQuestionsInDatabase(it)
                repository.getQuestionsByWeekOnDatabase(week).collect { questions ->
                    _apiState.postValue(questions)
                }
            }
        }
    }

    fun changeState(sleep: ApiState.Sleep) {
        _apiState.postValue(sleep)
    }
}