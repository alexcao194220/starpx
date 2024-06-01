package com.alexcao.starpx.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames


@Serializable
data class ImageDetail @OptIn(ExperimentalSerializationApi::class) constructor(
    @JsonNames("full_height")
    val fullHeight: Int,
    @JsonNames("full_url")
    val fullUrl: String,
    @JsonNames("full_width")
    val fullWidth: Int,
    val thumbs: Thumbs
)