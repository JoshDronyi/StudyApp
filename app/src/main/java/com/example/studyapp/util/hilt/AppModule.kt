package com.example.studyapp.util.hilt

import android.content.Context
import com.example.studyapp.data.local.LocalDataSource
import com.example.studyapp.data.local.QuestionDAO
import com.example.studyapp.data.remote.AuthDataSource
import com.example.studyapp.data.factories.DataSourceFactory
import com.example.studyapp.data.remote.FirebaseDatabaseDataSource
import com.example.studyapp.data.repo.QuestionRepository
import com.example.studyapp.data.repo.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import javax.inject.Singleton

@ExperimentalCoroutinesApi
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = DataSourceFactory.getLocalDB(context)


    @Singleton
    @Provides
    fun provideQuestionRepository(
        dao: QuestionDAO,
        firebase: FirebaseDatabaseDataSource
    ) = QuestionRepository(firebase, dao)


    @Singleton
    @Provides
    fun provideAuth() = DataSourceFactory.getAuthDataSource()

    @InternalCoroutinesApi
    @Singleton
    @Provides
    fun provideUserRepository(auth: AuthDataSource) =
        UserRepository(auth, DataSourceFactory.getFirebaseDataSource())


    @Singleton
    @Provides
    fun provideDao(
        database: LocalDataSource
    ) = database.getQuestionDao()


    @Provides
    @Singleton
    fun providesFirebaseDataSource() = FirebaseDatabaseDataSource()


}