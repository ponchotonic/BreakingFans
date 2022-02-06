package com.alfonsocastro.breakingfans.ui.characters

import android.util.Log
import androidx.lifecycle.*
import com.alfonsocastro.breakingfans.data.CharacterRepository
import com.alfonsocastro.breakingfans.model.Character
import kotlinx.coroutines.launch

// Enum for the BreakingBad Api response status
enum class CharacterApiStatus { LOADING, ERROR, DONE }

private const val TAG = "CharacterViewModel"

/**
 * ViewModel that holds all the [Character] list and the selected character.
 */
class CharacterSharedViewModel(private val repository: CharacterRepository) : ViewModel() {

    private val _status = MutableLiveData<CharacterApiStatus>()
    val status: LiveData<CharacterApiStatus> = _status

    private val _characters = MutableLiveData<List<Character>>()
    val characters: LiveData<List<Character>> = _characters

    private val _selectedCharacter = MutableLiveData<Character>()
    val selectedCharacter: LiveData<Character> = _selectedCharacter

    val favorites: LiveData<List<Character>> = repository.getFavorites().asLiveData()

    init {
        getCharacters()
    }

    /**
     * Gets Character information from the DataSource and updates the
     * [Character] [List] [LiveData].
     */
    fun getCharacters() {
        viewModelScope.launch {
            _status.value = CharacterApiStatus.LOADING
            try {
                _characters.value = repository.getCharactersFromNetwork()
                _status.value = CharacterApiStatus.DONE
                Log.d(TAG, "Loaded ${characters.value?.size} characters.")
                // Map character list to favorites list
                _characters.value?.map {
                    if (favorites.value?.contains(it) == true) {
                        it.isFavorite = true
                    }
                }
            } catch (e: Exception) {
                _status.value = CharacterApiStatus.ERROR
                _characters.value = listOf()
                Log.d(TAG, "Error loading characters.", e)
            }
        }
    }

    fun getFavorite(id: Int): LiveData<Character> {
        return repository.getFavorite(id).asLiveData()
    }

    fun saveFavorite(character: Character) {
        viewModelScope.launch {
            repository.saveCharacterToFavorites(character)
            setIsFavoriteCharacter(character, true)
        }
    }

    fun deleteFavorite(character: Character) {
        viewModelScope.launch {
            repository.deleteFromFavorites(character)
            setIsFavoriteCharacter(character, false)
        }
    }

    private fun setIsFavoriteCharacter(character: Character, isFavorite: Boolean) {
        _characters.value?.let {
            // Check if its on Characters List
            if (it.contains(character)) {
                // Get Character and assign isFavorite
                it[it.lastIndexOf(character)].isFavorite = isFavorite
            }
        }
    }

    fun setSelectedCharacter(character: Character) {
        _selectedCharacter.value = character
    }

    fun saveSelectedCharacterToFavorites() {
        selectedCharacter.value?.let { character ->
            saveFavorite(character)
        }
    }

    class CharacterSharedViewModelFactory(private val repository: CharacterRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CharacterSharedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CharacterSharedViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}