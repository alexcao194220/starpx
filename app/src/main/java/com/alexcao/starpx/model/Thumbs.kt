package com.alexcao.starpx.model

import kotlinx.serialization.Serializable

@Serializable
data class Thumbs(
    val xlarge: String,
    val large: String,
    val small: String,
    val medium: String
)