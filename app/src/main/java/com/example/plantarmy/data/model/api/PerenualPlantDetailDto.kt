package com.example.plantarmy.data.model.api
import com.example.plantarmy.data.api.PerenualImageDto
import com.google.gson.annotations.SerializedName

data class PerenualPlantDetailDto(
    val id: Int,
    @SerializedName("common_name") val commonName: String?,
    @SerializedName("scientific_name") val scientificName: List<String>?,
    @SerializedName("other_name") val otherName: List<String>?,
    val family: String?,
    val origin: List<String>?,
    val type: String?,
    val cycle: String?,
    val watering: String?,
    val sunlight: Any?,
    @SerializedName("default_image") val defaultImage: PerenualImageDto?
)