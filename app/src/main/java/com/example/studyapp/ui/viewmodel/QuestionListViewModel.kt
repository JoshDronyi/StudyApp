package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.repo.QuestionRepository
import com.example.studyapp.data.repo.RepositoryInterface
import com.example.studyapp.util.State.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class QuestionListViewModel @Inject constructor(private val repository: QuestionRepository) :
    ViewModel() {

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
        get() = _questions

    private val _currentQuestion = MutableLiveData<Question>()
    val currentQuestion: LiveData<Question>
        get() = _currentQuestion


    private val _currentProgress = MutableLiveData<StudentProgress>()
    val currentProgress: LiveData<StudentProgress>
        get() = _currentProgress

    val currentWeek = MutableLiveData<String>()

    private val _apiState =
        MutableLiveData<ApiState<Any>>(ApiState.Sleep)
    val apiState: LiveData<ApiState<Any>>
        get() = _apiState


    private val TAG = "QuestionListViewModel"


    @ExperimentalCoroutinesApi
    fun getQuestions(week: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.e(TAG, "getQuestions:  week was $week")
        _apiState.postValue(ApiState.Loading)

        Log.e(TAG, "getQuestions: Launched coroutine.")
        repository.getQuestionsByWeek(week = week).collect { dataFromTheInternet ->
            Log.e(
                TAG,
                " getQuestions (inside repo lambda): Questions from Internet -> $dataFromTheInternet"
            )
            repository.getQuestionsByWeekOnDatabase(week).collect { questionList ->
                Log.e(TAG, "getQuestions(inside repo lambda): Questions from DB -> $questionList")
                if (questionList.isNotEmpty()) {
                    Log.e(TAG, "getQuestions: Questions is not empty")
                    dataFromTheInternet.mapIndexed { index, internet ->
                        internet.questionStatus = questionList[index].questionStatus
                    }
                    repository.saveQuestionsInDatabase(dataFromTheInternet)
                } else {
                    Log.e(TAG, "getQuestions: questions was empty. $questionList")
                }
            }
            _apiState.postValue(ApiState.Success.QuestionApiSuccess(dataFromTheInternet))
        }
    }

    fun getNewQuestion(): Boolean {
        return _questions.value?.let { questions ->
            shouldShowNextQuestion(questions)
        } ?: false
    }

    fun setQuestionList(questions: List<Question>) {
        Log.e(TAG, "setQuestionList: Setting question list to $questions")
        _questions.value = questions
    }


    private fun shouldShowNextQuestion(questions: List<Question>): Boolean {
        currentQuestion.value?.let { currentQuestion ->
            Log.e("Question Number", currentQuestion.questionNumber.toString())
            Log.e("Last Number", questions.lastIndex.toString())

            return shouldShowNextQuestion(
                currentQuestion.questionNumber,
                questions.lastIndex
            ).also {
                if (it) {
                    _currentQuestion.postValue(questions[currentQuestion.questionNumber])
                }
            }
        }
        return false
    }

    fun shouldShowNextQuestion(currentQuestionNumber: Int, lastQuestionNumber: Int): Boolean =
        currentQuestionNumber <= lastQuestionNumber

    fun setCurrentProgress(currentProgress: StudentProgress) =
        _currentProgress.postValue(currentProgress)


    fun updateQuestionStatus(question: Question) {
        viewModelScope.launch {
            repository.saveQuestionsInDatabase(listOf(question))
        }
    }


    fun setCurrentQuestion(question: Question) = _currentQuestion.postValue(question)

}