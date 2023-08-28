package com.sample.chatai.domain

import com.sample.chatai.model.ConversationRepository
import com.sample.chatai.model.Message
import com.sample.chatai.model.OpenAIRepository
import javax.inject.Inject

class ResendMessageUseCase @Inject constructor(
    private val openAIRepository: OpenAIRepository,
    private val conversationRepository: ConversationRepository
) {
    suspend operator fun invoke(
        message: Message,
    ) {
        val conversation = conversationRepository.resendMessage(message)

        try {
            val reply = openAIRepository.sendChatRequest(conversation)
            conversationRepository.setMessageStatusToSent(message.id)
            conversationRepository.addMessage(reply)
        } catch (exception: Exception) {
            conversationRepository.setMessageStatusToError(message.id)
        }
    }
}