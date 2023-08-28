package com.sample.chatai.domain

import com.sample.chatai.model.ConversationRepository
import javax.inject.Inject

class ObserveMessagesUseCase @Inject constructor(
    private val conversationRepository: ConversationRepository,
) {

    operator fun invoke() = conversationRepository.conversationFlow
}