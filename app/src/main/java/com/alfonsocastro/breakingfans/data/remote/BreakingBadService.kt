package com.alfonsocastro.breakingfans.data.remote

import com.alfonsocastro.breakingfans.model.Character
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://www.breakingbadapi.com/api/"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface BreakingBadService {

    /**
     * Returns a [Character] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "id" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("characters/{id}")
    suspend fun getCharacter(@Path("id") heroId: Int): Character

    /**
     * Returns a [Character] list and can be called from a coroutine.
     */
    @GET("characters")
    suspend fun getAllCharacters(): List<Character>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object BreakingBadApi {
    val retrofitService: BreakingBadService by lazy {
        retrofit.create(BreakingBadService::class.java)
    }
}