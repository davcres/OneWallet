package com.davidcrespo.onewallet.data.remote.telegram.models

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(val chat_id: String, val text: String)