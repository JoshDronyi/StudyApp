package com.example.studyapp.util.hilt

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.studyapp.data.local.Database
import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.data.repo.QuestionRepository
import com.example.studyapp.data.repo.RepositoryInterface
import com.example.studyapp.data.repo.UserRepository
import com.example.studyapp.util.DATABASE_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room
        .databaseBuilder(context, Database::class.java, DATABASE_NAME)
        .addMigrations(migration_1_2)
        .build()


    @Singleton
    @Provides
    fun provideQuestionRepository(
        dao: QuestionDAO,
        firebaseDatabase: FirebaseDatabase
    ) = QuestionRepository(dao, firebaseDatabase)


    @Singleton
    @Provides
    fun provideAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideUserRepository(auth: FirebaseAuth) = UserRepository(auth)


    @Singleton
    @Provides
    fun provideDao(
        database: Database
    ) = database.questionDAO()

    @Singleton
    @Provides
    fun firebaseDatabase() = FirebaseDatabase.getInstance()


    private val migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE `StudentProgress` (`week` INTEGER, `totalQuestions` INTEGER, 'answeredQuestions' INTEGER, 'correctAnswers' INTEGER " +
                        "PRIMARY KEY(`week`))"
            )
        }
    }
}