package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.repo.QuestionRepository
import com.example.studyapp.ui.composables.screen_contracts.HomeContract
import com.example.studyapp.util.Events
import com.example.studyapp.util.SideEffects
import com.example.studyapp.util.State.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class QuestionListViewModel @Inject constructor(private val repository: QuestionRepository) :
    ViewModel() {


    //private screen contracts
    private val _homeScreenContract: MutableStateFlow<HomeContract> =
        MutableStateFlow(HomeContract())

    //observable contracts
    val homeScreenContract: StateFlow<HomeContract> get() = _homeScreenContract

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

    private val TAG = "QuestionListViewModel"


    @ExperimentalCoroutinesApi
    fun getQuestions(week: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.e(TAG, "getQuestions:  week was $week")
        with(_homeScreenContract) {
            emit(
                value.copy(
                    screenState = value.screenState.apply {
                        apiState = ApiState.Loading
                    }
                )
            )
        }

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
                            "getQuestions(inside repo DB lambda): Questions from DB -> $questionList"
                        )
                        if (questionList.isNotEmpty()) {
                            Log.e(TAG, "getQuestions: Questions from the database is not empty")

                            val list: List<Question> =
                                dataFromTheInternet
                                    .questionList
                                    .mapIndexed { index, internet ->
                                        internet.questionStatus = questionList[index].questionStatus
                                        internet
                                    }

                            Log.e(TAG, "getQuestions: value of the list is $list")
                            with(_homeScreenContract) {
                                emit(
                                    value.copy(
                                        screenState = value.screenState.copy(
                                            apiState = ApiState.Success.QuestionApiSuccess(list)
                                        )
                                    )
                                )
                            }
                        } else {
                            Log.e(
                                TAG,
                                "getQuestions: questions from the database was empty.$questionList"
                            )

                            repository.saveQuestionsInDatabase(dataFromTheInternet.questionList)

                            with(_homeScreenContract) {
                                emit(
                                    value.copy(
                                        screenState = value.screenState.copy(
                                            apiState = dataFromTheInternet
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
                else -> {
                    Log.e(TAG, "getQuestions: dataFromTheInternet was... $dataFromTheInternet")
                }
            }
        }
    }

    fun clearApiState() = viewModelScope.launch {
        with(_homeScreenContract) {
            emit(
                value.copy(
                    screenState = value.screenState.apply {
                        apiState = ApiState.Sleep
                    }
                )
            )
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
        repository.addNewQuestionToWeek(week, question).collect { theApiState ->
            Log.e(
                TAG,
                "addNewQuestion: collecting the repo method sending in $week, $question, got theFlow($theApiState) back."
            )
            with(_homeScreenContract) {
                emit(
                    value.copy(
                        screenState = value.screenState.copy(apiState = theApiState)
                    )
                )
            }
        }
    }

    suspend fun setHomeScreenEvent(event: Events.HomeScreenEvents) {
        Log.e(TAG, "setHomeScreenEvent: the event was $event")
        lateinit var sideEffect: SideEffects
        when (event) {
            is Events.HomeScreenEvents.onWeekSelected -> {
                currentWeek.value = event.selectedWeek
                sideEffect = SideEffects.HomeScreenSideEffects
                    .SetCurrentWeek(event.selectedWeek)

                with(_homeScreenContract) {
                    Log.e(
                        TAG,
                        "setHomeScreenEvent: emitting... \nEvent: $event \nSideEffect: $sideEffect"
                    )
                    emit(
                        value.copy(screenSideEffects = sideEffect)
                    )
                }
            }
            else -> {
                Log.e(TAG, "setHomeScreenEvent: unhandled event was $event")
            }
        }
    }
}