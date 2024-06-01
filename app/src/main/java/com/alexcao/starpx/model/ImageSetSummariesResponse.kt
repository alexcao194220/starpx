@file:OptIn(ExperimentalSerializationApi::class)

package com.alexcao.starpx.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class GetImageSetSummariesResponse(
    val getImageSetSummaries: GetImageSetSummaries
)

@Serializable
data class GetImageSetSummaries(
    val nextToken: String?,
    @JsonNames("image_sets")
    val imageSets: List<ImageSet>
)

