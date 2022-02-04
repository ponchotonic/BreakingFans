package com.alfonsocastro.breakingfans.model

import com.squareup.moshi.Json

data class Character(
    @Json(name = "char_id") val id: Int,
    val name: String,
    val birthday: String,
    val occupation: List<String>,
    @Json(name = "img") val image: String,
    val status: String,
    val nickname: String,
    val appearance: List<Int>,
    @Json(name = "portrayed") val actor: String,
    val category: String,
    @Json(name = "better_call_saul_appearance") val betterCallSaulAppearance: List<Int>
)
