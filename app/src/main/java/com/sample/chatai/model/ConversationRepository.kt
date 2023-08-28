package com.sample.chatai.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class ConversationRepository @Inject constructor() {

    private var messagesList = mutableListOf(
        Message(
            text = "Hi, how can I help?",
            isFromUser = false,
            messageStatus = MessageStatus.Sent,
        )
    )

    private val _conversationFlow = MutableStateFlow(
        value = Conversation(list = messagesList)
    )

    val conversationFlow = _conversationFlow.asStateFlow()

    fun addMessage(message: Message) : Conversation {
        messagesList.add(message)
        return updateConversationFlow(messagesList)
    }

    fun resendMessage(message: Message) : Conversation {
        messagesList.remove(message)
        messagesList.add(message)
        return updateConversationFlow(messagesList)
    }

    fun setMessageStatusToSent(messageId: String) {
        val index = messagesList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messagesList[index] = messagesList[index].copy(messageStatus = MessageStatus.Sent)
        }
        updateConversationFlow(messagesList)
    }

    fun setMessageStatusToError(messageId: String) {
        val index = messagesList.indexOfFirst { it.id == messageId }
        if (index != -1) {
            messagesList[index] = messagesList[index].copy(messageStatus = MessageStatus.Error)
        }

        updateConversationFlow(messagesList)
    }

    private fun updateConversationFlow(messageList: List<Message>) : Conversation {
        val conversation = Conversation(
            list = messageList,
        )
        _conversationFlow.value = conversation

        return conversation
    }
}