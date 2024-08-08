package com.kishan.samachar.data.remote.model

import com.kishan.samachar.data.local.entity.SourceEntity
import com.kishan.samachar.data.model.SourceContract
import com.kishan.samachar.utils.AppConstants.DEFAULT_SOURCE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Source(
    @SerialName("id")
    override val sourceId: String = DEFAULT_SOURCE,
    @SerialName("name")
    override val sourceName: String = DEFAULT_SOURCE,
) : SourceContract

fun Source.toSourceEntity(): SourceEntity {
    return SourceEntity(
        sourceId = sourceId,
        sourceName = sourceName
    )
}