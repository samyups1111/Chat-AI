package com.sample.chatai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.sample.chatai.R
import com.sample.chatai.model.ChatScreenUiHandlers
import com.sample.chatai.model.Conversation
import com.sample.chatai.model.Message
import com.sample.chatai.model.MessageStatus
import com.sample.chatai.ui.theme.ChatViewModel
import com.sample.chatai.utils.HorizontalSpacer
import com.sample.chatai.utils.VerticalSpacer
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Refresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    uiHandlers: ChatScreenUiHandlers = ChatScreenUiHandlers(
        onSendMessage = viewModel::sendMessage,
        onResendMessage = viewModel::resendMessage
    ),
    conversation: LiveData<Conversation> = viewModel.conversation,
    isSendingMessage: LiveData<Boolean> = viewModel.isSendingMessage,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var inputValue by remember { mutableStateOf("") }

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val conversationState by conversation.observeAsState()
    val isSendingMessageState by isSendingMessage.observeAsState()

    fun sendMessage() {
        uiHandlers.onSendMessage(inputValue)
        inputValue = ""
        coroutineScope.launch {
            listState.animateScrollToItem(conversationState?.list?.size ?: 0)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues = paddingValues)
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                conversationState?.let {
                    MessageList(
                        messageList = it.list,
                        listState = listState,
                        onResendMessage = uiHandlers.onResendMessage,
                    )
                }
            }
            Row {
                TextField(
                    value = inputValue,
                    onValueChange = { inputValue = it },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions {
                        sendMessage()
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                HorizontalSpacer(8.dp)
                Button(
                    modifier = Modifier.height(56.dp),
                    onClick = { sendMessage() },
                    enabled = inputValue.isNotBlank() && isSendingMessageState != true,
                ) {
                    if (isSendingMessageState == true) {
                        Icon(
                            imageVector = Icons.Default.Refresh,//sync
                            contentDescription = "Sending",
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                        )
                    }
                }
            }
        }

    }
}

@Composable
private fun MessageList(
    messageList: List<Message>,
    listState: LazyListState,
    onResendMessage: (Message) -> Unit,
) {
    LazyColumn(
        state = listState,
    ) {
        items(messageList) {message ->
            Row {
               if (message.isFromUser) {
                   HorizontalSpacer(width = 16.dp)
                   Box(
                       modifier = Modifier.weight(weight = 1f),
                   )
               }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    textAlign = if (message.isFromUser) { TextAlign.End } else { TextAlign.Start },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (message.messageStatus == MessageStatus.Error) {
                                MaterialTheme.colorScheme.errorContainer
                            } else {
                                if (message.isFromUser) {
                                    MaterialTheme.colorScheme.secondaryContainer
                                } else {
                                    MaterialTheme.colorScheme.primaryContainer
                                }
                            }
                        )
                        .clickable(enabled = message.messageStatus == MessageStatus.Error) {
                            onResendMessage(message)
                        }
                        .padding(all = 8.dp)
                )
                if (!message.isFromUser) {
                    HorizontalSpacer(width = 16.dp)
                    Box(
                        modifier = Modifier.weight(weight = 1f)
                    )
                }
            }
            if (message.messageStatus == MessageStatus.Error) {
                Row(
                    modifier = Modifier
                        .clickable {
                            onResendMessage(message)
                        }
                ) {
                    Box(
                        modifier = Modifier.weight(weight = 1f)
                    )
                    Text(
                        text = stringResource(id = R.string.chat_message_error),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            VerticalSpacer(height = 8.dp)
        }
    }
}