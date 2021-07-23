package com.example.studyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studyapp.data.model.Question

@Database(
    entities = [Question::class],
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract fun questionDAO(): QuestionDAO
}