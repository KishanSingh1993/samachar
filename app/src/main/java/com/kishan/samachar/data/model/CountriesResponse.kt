package com.kishan.samachar.data.model

data class CountriesResponse(
    val pageSize: Int,
    val pageNo: Int,
    val totalItems: Int,
    val countries: List<Country>
)

data class Country(
    val code: String,
    val name: String,
    val flag: String
)