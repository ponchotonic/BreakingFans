package com.alfonsocastro.breakingfans.data

import com.alfonsocastro.breakingfans.data.remote.BreakingBadService
import com.alfonsocastro.breakingfans.model.Character

class CharacterRepository(private val service: BreakingBadService) {

    suspend fun getCharactersFromNetwork(): List<Character> {
        return service.getAllCharacters()
    }

}