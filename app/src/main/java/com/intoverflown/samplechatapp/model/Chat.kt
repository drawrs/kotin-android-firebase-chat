package com.intoverflown.samplechatapp.model

data class Chat(var sender: String? = null,
                var receiver: String? = null,
                var message: String? = null,
                var isseen: Boolean? = null)