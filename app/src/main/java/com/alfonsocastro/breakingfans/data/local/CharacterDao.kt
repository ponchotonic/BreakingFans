package com.alfonsocastro.breakingfans.data.local

import androidx.room.*
import com.alfonsocastro.breakingfans.model.Character
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(character: Character)

    @Update
    suspend fun update(character: Character)

    @Delete
    suspend fun delete(character: Character)

    @Query("SELECT * from characters WHERE id = :id")
    fun getCharacter(id: Int): Flow<Character>

    @Query("SELECT * from characters ORDER BY id ASC")
    fun getCharacters(): Flow<List<Character>>

}