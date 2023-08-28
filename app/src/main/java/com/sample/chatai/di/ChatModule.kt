package com.sample.chatai.di

import com.aallam.openai.client.OpenAI
import com.sample.chatai.domain.ObserveMessagesUseCase
import com.sample.chatai.domain.ResendMessageUseCase
import com.sample.chatai.domain.SendChatRequestUseCase
import com.sample.chatai.model.ConversationRepository
import com.sample.chatai.model.OpenAIRepository
import com.sample.chatai.ui.theme.ChatViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ChatModule {

    @Provides
    @Singleton
    fun provideChatViewModel(
        sendChatRequestUseCase: SendChatRequestUseCase,
        resendMessagetUseCase: ResendMessageUseCase,
        observeMessagesUseCase: ObserveMessagesUseCase,
    ) = ChatViewModel(sendChatRequestUseCase, resendMessagetUseCase, observeMessagesUseCase)

    @Provides
    @Singleton
    fun provideConversationRepository() = ConversationRepository()

    @Provides
    @Singleton
    fun provideOpenAiRepository(
        openAi: OpenAI,
    ) = OpenAIRepository(openAi)

    fun provideObserveMessageUseCase(
        conversationRepository: ConversationRepository,
    ) = ObserveMessagesUseCase(conversationRepository)

    fun provideResendMessageUseCase(
        openAIRepository: OpenAIRepository,
        conversationRepository: ConversationRepository,
    ) = ResendMessageUseCase(openAIRepository, conversationRepository)

    fun provideSendChatRequestUseCase(
        openAIRepository: OpenAIRepository,
        conversationRepository: ConversationRepository,
    ) = SendChatRequestUseCase(openAIRepository, conversationRepository)
}