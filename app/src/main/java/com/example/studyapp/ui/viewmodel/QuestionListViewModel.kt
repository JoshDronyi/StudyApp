package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.repo.QuestionRepository
import com.example.studyapp.util.State.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
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
        MutableLiveData<ApiState<*>>(ApiState.Sleep)
    val apiState: LiveData<ApiState<*>>
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
            when (dataFromTheInternet) {
                is ApiState.Success.QuestionApiSuccess -> {
                    //Save question to the database
                    repository.getQuestionsByWeekOnDatabase(week).collect { questionList ->
                        Log.e(
                            TAG,
                            "getQuestions(inside repo lambda): Questions from DB -> $questionList"
                        )
                        if (questionList.isNotEmpty()) {
                            Log.e(TAG, "getQuestions: Questions from the database is not empty")

                            val list: List<Question> =
                                dataFromTheInternet.questionList.mapIndexed { index, internet ->
                                    internet.questionStatus = questionList[index].questionStatus
                                    internet
                                }

                            _apiState.postValue(ApiState.Success.QuestionApiSuccess(list))
                        } else {
                            Log.e(
                                TAG,
                                "getQuestions: questions from the database was empty.$questionList"
                            )
                        }
                    }
                }
                else -> {
                    Log.e(TAG, "getQuestions: dataFromTheInternet was... $dataFromTheInternet")
                }
            }
            _apiState.postValue(dataFromTheInternet)
        }
    }

    fun stopQuestions() {
        _apiState.value = ApiState.Sleep
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
            Log.e("Question Number", currentQuestion.questionNumber)
            Log.e("Last Number", questions.lastIndex.toString())

            return shouldShowNextQuestion(
                currentQuestion.questionNumber.toInt(),
                questions.lastIndex
            ).also {
                if (it) {
                    _currentQuestion.postValue(questions[currentQuestion.questionNumber.toInt()])
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
    fun addNewQuestion(week: String, question: Question) = viewModelScope.launch(Dispatchers.IO) {
        Log.e(
            TAG,
            "addNewQuestion: adding new question in VM for week  $week. Question: $question "
        )
        repository.addNewQuestionToWeek(week, question).collect { theFlow ->
            Log.e(
                TAG,
                "addNewQuestion: collecting the repo method sending in $week, $question, got theFlow($theFlow) back."
            )
            _apiState.postValue(theFlow)
        }
    }
}