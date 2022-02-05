package com.alfonsocastro.breakingfans.ui.favorites

import androidx.lifecycle.*
import com.alfonsocastro.breakingfans.data.local.CharacterDao
import com.alfonsocastro.breakingfans.model.Character
import kotlinx.coroutines.launch

private const val TAG = "FavoritesViewModel"

class FavoritesViewModel(private val characterDao: CharacterDao) : ViewModel() {

    val favorites: LiveData<List<Character>> = characterDao.getCharacters().asLiveData()

    fun deleteFavorite(character: Character) {
        viewModelScope.launch {
            characterDao.delete(character)
        }
    }

    class FavoritesViewModelFactory(private val characterDao: CharacterDao) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return FavoritesViewModel(characterDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}