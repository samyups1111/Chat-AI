package com.sample.chatai.di

import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import com.sample.chatai.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideOpenAI(): OpenAI {
        val config = OpenAIConfig(
            token = BuildConfig.OPENAI_API_KEY,
        )
        return OpenAI(config)
    }
}