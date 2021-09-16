package com.isel.warpDelivery.exceptionHandler

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.MediaType
import java.net.URI

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProblemJsonModel(
    val detail: String? = null,
    val instance: String? = null,
    val type: URI,
    val title: String? = null,
) {
    companion object {
        val MEDIA_TYPE = MediaType.parseMediaType("application/problem+json")
    }
}