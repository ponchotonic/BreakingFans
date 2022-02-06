package com.alfonsocastro.breakingfans.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "characters")
data class Character(
    @PrimaryKey
    @Json(name = "char_id")
    val id: Int,
    val name: String,
    val birthday: String,
    val occupation: List<String>,
    @Json(name = "img")
    val image: String,
    val status: String,
    val nickname: String,
    val appearance: List<Int>,
    @Json(name = "portrayed")
    val actor: String,
    val category: String,
    @Json(name = "better_call_saul_appearance")
    @ColumnInfo(name = "better_call_saul_appearance")
    val betterCallSaulAppearance: List<Int>,
) {
    @Ignore
    var isFavorite: Boolean = false
}
