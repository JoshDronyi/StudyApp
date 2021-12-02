package com.example.studyapp.data.repo

import androidx.room.RoomDatabase
import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.remote.FirebaseDatabaseDataSource
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    val firebaseDatabase: FirebaseDatabaseDataSource

    fun onDestroy()
}