package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.repo.QuestionRepository
import com.example.studyapp.ui.composables.screen_contracts.HomeContract
import com.example.studyapp.ui.composables.screen_contracts.QuestionListContract
import com.example.studyapp.ui.composables.screen_contracts.QuestionScreenContract
import com.example.studyapp.util.Events
import com.example.studyapp.util.SideEffects
import com.example.studyapp.util.State
import com.example.studyapp.util.State.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class QuestionListViewModel @Inject constructor(private val repository: QuestionRepository) :
    ViewModel() {


    //private screen contracts
    private val _homeScreenContract: MutableStateFlow<HomeContract> =
        MutableStateFlow(HomeContract())
    private val _questionListContract: MutableStateFlow<QuestionListContract> =
        MutableStateFlow(QuestionListContract())
    private val _questionContract: MutableStateFlow<QuestionScreenContract> =
        MutableStateFlow(QuestionScreenContract())

    //observable contracts
    val homeScreenContract: StateFlow<HomeContract> get() = _homeScreenContract
    val questionListContract: StateFlow<QuestionListContract> get() = _questionListContract
    val questionContract: StateFlow<QuestionScreenContract> get() = _questionContract


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
            Log.e(TAG, "clearApiState: clearing api state")
            emit(
                value.copy(
                    screenState = value.screenState.apply {
                        apiState = ApiState.Sleep
                    }
                )
            )
        }
    }


    fun getNewQuestion(): Boolean = shouldShowNextQuestion(
        _questionListContract.value.screenState.questionList
    )


    fun setQuestionList(questions: List<Question>) {
        Log.e(TAG, "setQuestionList: Setting question list to $questions")
        _questionListContract.value.screenState.questionList = questions
    }


    private fun shouldShowNextQuestion(questions: List<Question>): Boolean =
        with(_questionContract.value) {
            Log.e("Question Number", screenState.currentQuestion.questionNumber)
            Log.e("Last Number", questions.lastIndex.toString())

            return shouldShowNextQuestion(
                screenState.currentQuestion.questionNumber.toInt(),
                questions.lastIndex
            ).also {
                if (it) {
                    screenState = screenState.copy(
                        currentQuestion =
                        questions[screenState.currentQuestion.questionNumber.toInt()]
                    )
                }
            }
        }


    private fun shouldShowNextQuestion(
        currentQuestionNumber: Int,
        lastQuestionNumber: Int
    ): Boolean =
        currentQuestionNumber <= lastQuestionNumber

    fun setCurrentProgress(currentProgress: StudentProgress) {
        _questionListContract.value.screenState.progress = currentProgress
    }


    fun updateQuestionStatus(question: Question) {
        viewModelScope.launch {
            repository.saveQuestionsInDatabase(listOf(question))
        }
    }


    fun setCurrentQuestion(question: Question) = with(_questionContract.value) {
        screenState = screenState.copy(currentQuestion = question)
    }

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
                _questionListContract.value.screenState.currentWeek = event.selectedWeek
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

    fun clearSideEffects() {
        _questionContract.value.screenSideEffects = SideEffects.QuestionScreenSideEffects()
        _questionListContract.value.sideEffects = SideEffects.QuestionListScreenSideEffects()
        _homeScreenContract.value.screenSideEffects = SideEffects.HomeScreenSideEffects()
    }

    fun clearEvents() {
        _questionContract.value.screenEvents = Events.QuestionScreenEvents()
        _questionListContract.value.screenEvent = Events.QuestionListScreenEvents()
        _homeScreenContract.value.screenEvent = Events.HomeScreenEvents()
    }
}