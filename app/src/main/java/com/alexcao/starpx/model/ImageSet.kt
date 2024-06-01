package com.alexcao.starpx.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class ImageSet @OptIn(ExperimentalSerializationApi::class) constructor(
    val caption: String,
    @JsonNames("set_id")
    val setId: String,
    val state: String,
    @JsonNames("image_detail")
    val imageDetail: ImageDetail
)

