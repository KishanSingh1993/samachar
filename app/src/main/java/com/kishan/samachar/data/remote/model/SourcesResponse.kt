package com.kishan.samachar.data.remote.model

import com.kishan.samachar.data.remote.model.Source
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SourcesResponse(
    @SerialName("status")
    val status: String = "",
    @SerialName("sources")
    val sources: List<Source> = ArrayList(),
)