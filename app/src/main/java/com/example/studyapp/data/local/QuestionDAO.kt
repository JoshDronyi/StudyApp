package com.example.studyapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studyapp.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
abstract class QuestionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertQuestions(vararg question : Question)

    @Query("select * from question WHERE week = :week")
    abstract fun getQuestionsByWeek(week : Int) : Flow<List<Question>>

    @Query("select * from question WHERE id = :id")
    abstract fun getQuestionById(id : Int) : Flow<List<Question>>
}