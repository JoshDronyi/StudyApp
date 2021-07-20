package com.example.studyapp

import android.content.Context
import androidx.room.Room
import com.example.studyapp.data.local.Database
import com.example.studyapp.data.remote.QuestionsAPI
import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.repo.QuestionRepository
import com.example.studyapp.repo.RepositoryInterface
import com.example.studyapp.util.BASE_URL
import com.example.studyapp.util.DATABASE_NAME
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*@Singleton
    @Provides
    fun provideRetrofit() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    @Singleton
    @Provides
    fun provideAPI(retrofit: Retrofit) = retrofit.create(QuestionsAPI::class.java)*/

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, Database::class.java, DATABASE_NAME).build()


    @Singleton
    @Provides
    fun provideRepository(
        dao: QuestionDAO
        //api: QuestionsAPI
    ) = QuestionRepository(dao) as RepositoryInterface

    @Singleton
    @Provides
    fun provideDao(
        database: Database
    ) = database.questionDAO()
}