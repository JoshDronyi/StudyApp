package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.repo.QuestionRepository
import com.example.studyapp.util.State.QuestionApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: QuestionRepository) : ViewModel() {

    private val _apiState =
        MutableLiveData<QuestionApiState<List<Question>>>(QuestionApiState.Sleep())
    val apiState: LiveData<QuestionApiState<List<Question>>>
        get() = _apiState


    private val TAG = "MainViewModel"


    @ExperimentalCoroutinesApi
    fun getQuestions(week: String) {
        Log.e(TAG, "getQuestions:  week was $week")
        _apiState.value = QuestionApiState.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(TAG, "getQuestions: Launched coroutine.")
            repo.getQuestionsByWeek(week = week).collect { dataFromTheInternet ->
                Log.e(TAG, " getQuestions (inside repo lambda): Questions -> $dataFromTheInternet")
                val questions = repo.getQuestionsByWeekOnDatabase(week).first()
                Log.e(TAG, "getQuestions(inside repo lambda): Questions DB -> $questions")
                if (questions.isNotEmpty())
                    dataFromTheInternet.mapIndexed { index, internet ->
                        internet.questionStatus = questions[index].questionStatus
                    }
                repo.saveQuestionsInDatabase(dataFromTheInternet)
                _apiState.postValue(QuestionApiState.Success(dataFromTheInternet))
            }
        }
    }

    fun changeState() {
        _apiState.postValue(QuestionApiState.Sleep())
    }
}