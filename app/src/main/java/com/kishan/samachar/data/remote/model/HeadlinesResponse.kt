package com.kishan.samachar.data.remote.model

import com.kishan.samachar.data.remote.model.Headline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeadlinesResponse(
    @SerialName("status")
    val status: String = "",
    @SerialName("totalResults")
    val totalResults: Int = 0,
    @SerialName("articles")
    val headlines: List<Headline> = ArrayList(),
)