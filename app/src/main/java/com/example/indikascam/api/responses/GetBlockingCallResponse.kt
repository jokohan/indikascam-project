package com.example.indikascam.api.responses

data class GetBlockingCallResponse (
    val block: Boolean,
    val is_automatic: Boolean
    )