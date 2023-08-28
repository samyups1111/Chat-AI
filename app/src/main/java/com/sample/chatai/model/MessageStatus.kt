package com.sample.chatai.model

sealed class MessageStatus {
    object Sending: MessageStatus()
    object Error: MessageStatus()
    object Sent: MessageStatus()
}
