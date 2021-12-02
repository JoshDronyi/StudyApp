package com.example.studyapp.data.factories

import android.content.Context
import com.example.studyapp.data.local.LocalDataSource
import com.example.studyapp.data.remote.AuthDataSource
import com.example.studyapp.data.remote.FirebaseDatabaseDataSource

object DataSourceFactory {
    fun getAuthDataSource() = AuthDataSource()
    fun getFirebaseDataSource() = FirebaseDatabaseDataSource()
    fun getLocalDB(context: Context) = LocalDataSource(context)
}