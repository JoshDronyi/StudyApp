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

    private val weekQuestionCache: MutableMap<String, MutableList<Question>> = mutableMapOf()

    //observable contracts
    val homeScreenContract: StateFlow<HomeContract> get() = _homeScreenContract
    val questionListContract: StateFlow<QuestionListContract> get() = _questionListContract
    val questionContract: StateFlow<QuestionScreenContract> get() = _questionContract


    private val TAG = "QuestionListViewModel"


    @ExperimentalCoroutinesApi
    fun getQuestions(week: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.e(
            TAG,
            "getQuestions:  week was $week, currentState is ${homeScreenContract.value.screenState}" +
                    " \n the cache was ${weekQuestionCache[week]}"
        )
        if (weekQuestionCache[week].isNullOrEmpty()) {
            Log.e(TAG, "getQuestions: Cache was empty, going to get from internet")
            getQuestionsFromInternet(week)
        } else {
            weekQuestionCache[week]?.let { questionList ->
                Log.e(
                    TAG,
                    "getQuestions: Question list was $questionList, setting home screen event"
                )
                setHomeScreenEvent(
                    Events.HomeScreenEvents.GoToSelectedWeek(
                        questionList
                    )
                )
            }
        }
    }

    private suspend fun getQuestionsFromInternet(week: String) =
        repository.getQuestionsByWeek(week).collect { dataFromTheInternet ->
            Log.e(TAG, "getQuestionsFromInternet: Internet data list $dataFromTheInternet")
            when (dataFromTheInternet) {
                is ApiState.Success.QuestionApiSuccess -> {

                    //Add missing questions from the internet.
                    dataFromTheInternet.questionList.forEach { currentQuestion ->
                        if (weekQuestionCache[week]?.contains(currentQuestion) != true) {
                            if (weekQuestionCache[week].isNullOrEmpty())
                                weekQuestionCache[week] = mutableListOf()

                            Log.e(
                                TAG,
                                "getQuestionsFromInternet: Adding question $currentQuestion"
                            )
                            weekQuestionCache[week]?.add(currentQuestion)
                        }
                    }

                    //Send newly cached questions
                    Log.e(
                        TAG,
                        "getQuestionsFromInternet: cache for week $week was ${weekQuestionCache[week]}",
                    )
                    weekQuestionCache[week]?.let {
                        setHomeScreenSideEffect(
                            SideEffects.HomeScreenSideEffects.GoToQuestionSet(
                                it
                            )
                        )
                    }  // empty list if no questions from this week online
                }
                else -> {
                    //fall through
                    setHomeScreenAPIState(dataFromTheInternet)
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

    fun clearHomeApiState() = viewModelScope.launch {
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
        repository.saveQuestionsToDB(question.asList())
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

    suspend fun setHomeScreenEvent(event: Events.HomeScreenEvents) = with(_homeScreenContract) {
        Log.e(TAG, "setHomeScreenEvent: the event was $event")
        lateinit var sideEffect: SideEffects
        when (event) {
            is Events.HomeScreenEvents.OnWeekSelected -> {
                _questionListContract.value.screenState.currentWeek = event.selectedWeek
                sideEffect = SideEffects.HomeScreenSideEffects
                    .SetCurrentWeek(event.selectedWeek)
                Log.e(
                    TAG,
                    "setHomeScreenEvent: setting week select side effect for ${event.selectedWeek}",
                )
                setHomeScreenSideEffect(sideEffect)
            }
            is Events.HomeScreenEvents.GoToSelectedWeek -> {
                sideEffect = SideEffects.HomeScreenSideEffects.GoToQuestionSet(event.questionList)
                Log.e(TAG, "setHomeScreenEvent: go to week side effect for ${event.questionList}")
                setHomeScreenSideEffect(sideEffect)
            }
            is Events.HomeScreenEvents.ClearApiState -> {
                clearHomeApiState()
            }
            else -> {
                Log.e(TAG, "setHomeScreenEvent: unhandled event was $event")
            }
        }

    }

    private suspend fun setHomeScreenSideEffect(
        sideEffect: SideEffects.HomeScreenSideEffects
    ) = with(_homeScreenContract) {
        emit(
            value.copy(
                screenSideEffects = sideEffect
            )
        )
    }

    fun clearSideEffects() {
        _questionListContract.value.sideEffects = SideEffects.QuestionListScreenSideEffects()
        _homeScreenContract.value.screenSideEffects = SideEffects.HomeScreenSideEffects()
    }

    //Question page methods

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