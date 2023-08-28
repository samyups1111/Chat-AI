package com.sample.chatai.model

data class ChatScreenUiHandlers(
    val onSendMessage: (String) -> Unit = {},
    val onResendMessage: (Message) -> Unit = {},
)