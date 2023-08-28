package com.sample.chatai.ui.theme

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.chatai.domain.ObserveMessagesUseCase
import com.sample.chatai.domain.ResendMessageUseCase
import com.sample.chatai.domain.SendChatRequestUseCase
import com.sample.chatai.model.Conversation
import com.sample.chatai.model.Message
import com.sample.chatai.model.MessageStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendChatRequestUseCase: SendChatRequestUseCase,
    private val resendMessageUseCase: ResendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
) : ViewModel() {

    private val _conversation = MutableLiveData<Conversation>()
    val conversation: LiveData<Conversation> = _conversation

    private val _isSendingMessage = MutableLiveData<Boolean>()
    val isSendingMessage: LiveData<Boolean> = _isSendingMessage

    init {
        observeMessageList()
    }

    private fun observeMessageList() {
        viewModelScope.launch {
            observeMessagesUseCase.invoke().collect { conversation ->
                _conversation.postValue(conversation)

                _isSendingMessage.postValue(
                    conversation.list.any() { it.messageStatus == MessageStatus.Sending }
                )
            }
        }
    }

    fun sendMessage(prompt: String) {
        viewModelScope.launch {
            sendChatRequestUseCase(
                prompt
            )
        }
    }

    fun resendMessage(message: Message) {
        viewModelScope.launch {
            resendMessageUseCase(
                message
            )
        }
    }
}