package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.ApiState
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.repo.RepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QuestionListViewModel @Inject constructor(private val repository: RepositoryInterface) :
    ViewModel() {

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>>
        get() = _questions

    private val _currentProgress = MutableLiveData<StudentProgress>()
    val currentProgress: LiveData<StudentProgress>
        get() = _currentProgress

    val currentWeek = MutableLiveData<String>()


    fun getNewQuestion(): Boolean {
        return _questions.value?.let { questions ->
            shouldShowNextQuestion(questions)
        } ?: false
    }

    fun setQuestionList( questions: List<Question>){
        _questions.value = questions
    }


    //Place holder function.
    private fun shouldShowNextQuestion(questions: List<Question>):Boolean{
        return false
    }

    fun setCurrentProgress(currentProgress: StudentProgress) =
        _currentProgress.postValue(currentProgress)



    fun updateQuestionStatus(question: Question) {
        viewModelScope.launch {
            repository.saveQuestionsInDatabase(listOf(question))
        }
    }
}