package com.example.studyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress

@Database(
    entities = [Question::class, StudentProgress::class],
    version = 5,
    exportSchema = true
)
abstract class Database : RoomDatabase() {
    abstract fun questionDAO(): QuestionDAO
}