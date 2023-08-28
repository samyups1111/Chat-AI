package com.sample.chatai.domain

import com.sample.chatai.model.ConversationRepository
import com.sample.chatai.model.Message
import com.sample.chatai.model.MessageStatus
import com.sample.chatai.model.OpenAIRepository
import javax.inject.Inject

class SendChatRequestUseCase @Inject constructor(
    private val openAIRepository: OpenAIRepository,
    private val conversationRepository: ConversationRepository,
) {

    suspend operator fun invoke(
        prompt: String,
    ) {
        val message = Message(
            text = prompt,
            isFromUser = true,
            messageStatus = MessageStatus.Sending,
        )
        val conversation = conversationRepository.addMessage(message)

        try {
            val reply = openAIRepository.sendChatRequest(conversation)
            conversationRepository.setMessageStatusToSent(message.id)
            conversationRepository.addMessage(reply)
        } catch (exception: Exception) {
            conversationRepository.setMessageStatusToError(message.id)
        }
    }
}