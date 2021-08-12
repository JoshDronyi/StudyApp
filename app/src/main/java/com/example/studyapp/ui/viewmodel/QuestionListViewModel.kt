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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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


    fun getNewQuestion(): Boolean {
        return _questions.value?.let { questions ->
            shouldShowNextQuestion(questions)
        } ?: false
    }

    fun setQuestionList(questions: List<Question>) {
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