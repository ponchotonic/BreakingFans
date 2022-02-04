package com.alfonsocastro.breakingfans.ui.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.load
import com.alfonsocastro.breakingfans.R
import com.alfonsocastro.breakingfans.data.CharacterRepository
import com.alfonsocastro.breakingfans.data.remote.BreakingBadApi
import com.alfonsocastro.breakingfans.databinding.FragmentCharacterDetailBinding
import com.alfonsocastro.breakingfans.model.Character
import com.alfonsocastro.breakingfans.util.BETTER_CALL_SAUL_CATEGORY
import com.alfonsocastro.breakingfans.util.BREAKING_BAD_CATEGORY

class CharacterDetailFragment : Fragment() {

    // Binding object instance corresponding to the xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentCharacterDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: CharacterSharedViewModel by activityViewModels() {
        CharacterSharedViewModel.CharacterSharedViewModelFactory(CharacterRepository(BreakingBadApi.retrofitService))
    }

    private val isInFavorites = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)

        // Observe ViewModel Character List and submit the adapter the new list.
        sharedViewModel.selectedCharacter.value?.let { displayCharacter(it) }

        return binding.root
    }

    private fun displayCharacter(character: Character) {

        binding.characterImage.load(character.image)

        binding.characterName.text = character.name

        binding.characterNickname.text = getString(R.string.nickname_format, character.nickname)
        binding.characterBirthday.text = getString(R.string.birthday_format, character.birthday)
        binding.characterStatus.text = getString(R.string.status_format, character.status)
        binding.characterActor.text = getString(R.string.actor_format, character.actor)

        binding.characterOccupation.text = character.occupation.joinToString("\n")

        // List to hold Season Appearance
        val appearance = mutableListOf<String>()

        // Manage BREAKING_BAD
        if (character.category.contains(BREAKING_BAD_CATEGORY)) {
            // Show series image
            binding.breakingBadImage.visibility = View.VISIBLE
            // Create string and append to the List to display
            val breakingString = getString(
                R.string.breaking_bad_appearance_format,
                character.appearance.joinToString()
            )
            appearance.add(breakingString)
        } else {
            // Not appearing in breaking bad. Hide Series Image
            binding.breakingBadImage.visibility = View.GONE
        }

        // Manage BETTER_CALL_SAUL
        if (character.category.contains(BETTER_CALL_SAUL_CATEGORY)) {
            // Show series image
            binding.betterCallSaulImage.visibility = View.VISIBLE
            // Create string and append to the List to display
            val betterString = getString(
                R.string.better_call_saul_appearance_format,
                character.betterCallSaulAppearance.joinToString()
            )
            appearance.add(betterString)
        } else {
            // Not appearing in BETTER CALL SAUL. Hide Series Image
            binding.betterCallSaulImage.visibility = View.GONE
        }

        // Display the season appearance list created before
        binding.characterAppearance.text = appearance.joinToString("\n")

    }

    private fun onFavoritesButtonClicked() {

    }
}