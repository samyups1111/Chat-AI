package com.sample.chatai.model

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import javax.inject.Inject

@OptIn(BetaOpenAI::class)
class OpenAIRepository @Inject constructor(
    private val openAi: OpenAI,
) {

    suspend fun sendChatRequest(
        conversation: Conversation,
    ) : Message {
        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId("gpt-3.5-turbo"),
            messages = conversation.toChatMessages(),
        )

        val chatMessage = openAi.chatCompletion(chatCompletionRequest).choices.first().message
            ?: throw NoChoiceAvailableException()

        return Message(
            text = chatMessage.content ?: "No message",
            isFromUser = chatMessage.role == ChatRole.User,
            messageStatus = MessageStatus.Sent
        )
    }

    private fun Conversation.toChatMessages() = this.list
        .filterNot { it.messageStatus == MessageStatus.Error }
        .map {
            ChatMessage(
                content = it.text,
                role = if (it.isFromUser) { ChatRole.User } else { ChatRole.Assistant }
            )
        }
}

class NoChoiceAvailableException: Exception()