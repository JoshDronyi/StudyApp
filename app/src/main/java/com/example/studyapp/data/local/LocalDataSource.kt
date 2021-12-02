package com.example.studyapp.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.studyapp.util.DATABASE_NAME

class LocalDataSource(context: Context) {

    private val localDB by lazy {
        Room
            .databaseBuilder(context, Database::class.java, DATABASE_NAME)
            .addMigrations(migration_1_2)
            .build()
    }

    private val questionDAO by lazy {
        localDB.questionDAO()
    }

    fun getQuestionDao(): QuestionDAO {
        return questionDAO
    }


    //MIGRATIONS
    private val migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE `StudentProgress` (`week` INTEGER, `totalQuestions` INTEGER, 'answeredQuestions' INTEGER, 'correctAnswers' INTEGER " +
                        "PRIMARY KEY(`week`))"
            )
        }
    }

}