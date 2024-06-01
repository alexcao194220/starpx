package com.alexcao.starpx.di

import android.content.Context
import com.alexcao.starpx.repository.Repository
import com.alexcao.starpx.utls.AWSClient
import com.alexcao.starpx.utls.RxPreferences
import com.amazonaws.mobile.client.AWSMobileClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun provideRepository(@ApplicationContext context: Context, rxPreferences: RxPreferences): Repository {
        val awsClient = AWSClient()
        awsClient.getAWSConfiguration(context)
        return Repository(
            context = context,
            awsClient = awsClient,
            rxPreferences = rxPreferences
        )
    }
}