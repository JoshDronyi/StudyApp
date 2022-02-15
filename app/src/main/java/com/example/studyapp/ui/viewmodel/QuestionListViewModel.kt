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
import com.example.studyapp.util.ErrorType
import com.example.studyapp.util.Events
import com.example.studyapp.util.SideEffects
import com.example.studyapp.util.State.ApiState
import com.example.studyapp.util.StudyAppError
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
        setHomeScreenAPIState(ApiState.Loading)

        repository.getQuestionsByWeek(week = week).collect { dataFromTheInternet ->
            Log.e(
                TAG,
                " getQuestions (inside repo lambda): Questions from Internet -> $dataFromTheInternet"
            )
            compareRemoteQuestionSet(week, dataFromTheInternet)
        }
    }

    private suspend fun compareRemoteQuestionSet(week: String, dataFromTheInternet: ApiState<*>) {
        when (dataFromTheInternet) {
            is ApiState.Success.QuestionApiSuccess -> {
                //Save question to the database
                repository.getQuestionsByWeekOnDatabase(week).collect { localQuestionList ->
                    Log.e(
                        TAG,
                        "getQuestions(inside repo DB lambda): Questions from DB -> $localQuestionList"
                    )

                    if (localQuestionList.isNotEmpty()) {
                        val finalList: MutableList<Question> = mutableListOf()
                        val unsavedQuestions: MutableList<Question> = mutableListOf()

                        Log.e(TAG, "getQuestions: Questions from the database is not empty")

                        dataFromTheInternet.questionList.forEachIndexed { index, internetQuestion ->
                            Log.e(
                                TAG,
                                "getQuestions: index was $index, \n questionList was ${localQuestionList[index]} \n internetQuestion was $internetQuestion "
                            )
                            if (localQuestionList.contains(internetQuestion)) {
                                val questionIndex = localQuestionList.indexOf(internetQuestion)
                                internetQuestion.questionStatus =
                                    localQuestionList[questionIndex].questionStatus
                            } else {
                                unsavedQuestions.add(internetQuestion)
                            }
                            finalList.add(internetQuestion)
                        }

                        if (!unsavedQuestions.isNullOrEmpty()) {
                            Log.e(
                                TAG,
                                "compareRemoteQuestionSet: unsavedQuestions were $unsavedQuestions",
                            )
                            saveQuestionInDB(*unsavedQuestions.toTypedArray())
                        }

                        finalList.addAll(unsavedQuestions)
                        Log.e(
                            TAG,
                            "compareRemoteQuestionSet: Setting APIState -> Final list was $finalList"
                        )
                        setHomeScreenAPIState(
                            ApiState.Success.QuestionApiSuccess(finalList)
                        )
                    } else {
                        Log.e(
                            TAG,
                            "getQuestions: questions from the database was empty. \n $localQuestionList"
                        )
                        if (dataFromTheInternet.questionList.isNotEmpty()) {
                            Log.e(
                                TAG,
                                "compareRemoteQuestionSet: setting homeState to $dataFromTheInternet",
                            )
                            setHomeScreenAPIState(dataFromTheInternet)
                        } else {
                            Log.e(
                                TAG,
                                "compareRemoteQuestionSet: the question set from the internet was empty"
                            )
                            setHomeScreenAPIState(
                                ApiState.Error(
                                    StudyAppError.newBlankInstance().apply {
                                        this.data = null
                                        this.errorType = ErrorType.NETWORK
                                        this.message =
                                            "There were no questions stored under this path."
                                        this.shouldShow = true
                                    }
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

    private fun setHomeScreenAPIState(state: ApiState<*>) =
        viewModelScope.launch {
            with(_homeScreenContract) {
                emit(
                    value.copy(
                        screenState = value.screenState.copy(
                            apiState = state
                        )
                    )
                )
            }
        }

    fun clearApiState() = viewModelScope.launch {
        Log.e(TAG, "clearApiState: clearing api state")
        setHomeScreenAPIState(ApiState.Sleep)
    }

    fun setQuestionList(questions: List<Question>) {
        Log.e(TAG, "setQuestionList: Setting question list to $questions")
        with(_questionListContract) {
            value = value.copy(
                screenState = value.screenState.apply {
                    questionList = questions
                }
            )
        }
    }

    fun setCurrentProgress(currentProgress: StudentProgress) = with(_questionListContract) {
        value = value.copy(
            screenState = value.screenState.apply {
                progress = currentProgress
            }
        )
    }

    fun saveQuestionInDB(vararg question: Question) = viewModelScope.launch {
        repository.saveQuestionsInDatabase(question.asList())
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
            setHomeScreenAPIState(theApiState)
        }
    }

    suspend fun setHomeScreenEvent(event: Events.HomeScreenEvents) {
        Log.e(TAG, "setHomeScreenEvent: the event was $event")
        lateinit var sideEffect: SideEffects
        when (event) {
            is Events.HomeScreenEvents.OnWeekSelected -> {
                _questionListContract.value.screenState.currentWeek = event.selectedWeek
                sideEffect = SideEffects.HomeScreenSideEffects
                    .SetCurrentWeek(event.selectedWeek)

                with(_homeScreenContract) {
                    Log.e(
                        TAG,
                        "setHomeScreenEvent: emitting... \nEvent: $event \nSideEffect: $sideEffect"
                    )
                    emit(
                        value.copy(
                            screenSideEffects = sideEffect,
                            screenEvent = event
                        )
                    )
                }
            }
            else -> {
                Log.e(TAG, "setHomeScreenEvent: unhandled event was $event")
            }
        }
    }

    fun clearSideEffects() {
        _questionListContract.value.sideEffects = SideEffects.QuestionListScreenSideEffects()
        _homeScreenContract.value.screenSideEffects = SideEffects.HomeScreenSideEffects()
    }

    //Question page methods

    private fun shouldShowNextQuestion(questions: List<Question>): Boolean =
        with(_questionContract.value) {
            android.util.Log.e("Question Number", screenState.currentQuestion.questionNumber)
            android.util.Log.e("Last Number", questions.lastIndex.toString())

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

    fun getNewQuestion(): Boolean = shouldShowNextQuestion(
        _questionContract.value.screenState.questionList
    )

    fun setCurrentQuestion(question: Question) = with(_questionContract.value) {
        screenState = screenState.copy(currentQuestion = question)
    }

    private fun shouldShowNextQuestion(
        currentQuestionNumber: Int,
        lastQuestionNumber: Int
    ): Boolean =
        currentQuestionNumber <= lastQuestionNumber

}