package com.example.studyapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.studyapp.model.Question
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(vararg settings : Question)

    @Query("select * from question WHERE week = :week")
    fun getQuestionsByWeek(week : Int) : Flow<List<Question>>

    @Query("select * from question WHERE id = :id")
    fun getQuestionById(id : Int) : Flow<List<Question>>
}