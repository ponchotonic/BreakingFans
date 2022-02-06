package com.alfonsocastro.breakingfans.data

import com.alfonsocastro.breakingfans.data.local.BreakingBadDatabase
import com.alfonsocastro.breakingfans.data.remote.BreakingBadService
import com.alfonsocastro.breakingfans.model.Character
import kotlinx.coroutines.flow.Flow

class CharacterRepository(
    private val service: BreakingBadService,
    private val database: BreakingBadDatabase
) {

    suspend fun getCharactersFromNetwork(): List<Character> {
        return service.getAllCharacters()
    }

    fun getFavorites(): Flow<List<Character>> {
        return database.characterDao().getCharacters()
    }

    fun getFavorite(id: Int): Flow<Character> {
        return database.characterDao().getCharacter(id)
    }

    suspend fun saveCharacterToFavorites(character: Character) {
        database.characterDao().insert(character)
    }

    suspend fun deleteFromFavorites(character: Character) {
        database.characterDao().delete(character)
    }

}