package com.alfonsocastro.breakingfans.ui.characters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.alfonsocastro.breakingfans.BreakingBadApplication
import com.alfonsocastro.breakingfans.R
import com.alfonsocastro.breakingfans.data.CharacterRepository
import com.alfonsocastro.breakingfans.data.remote.BreakingBadApi
import com.alfonsocastro.breakingfans.databinding.FragmentCharacterDetailBinding
import com.alfonsocastro.breakingfans.model.Character
import com.alfonsocastro.breakingfans.util.BETTER_CALL_SAUL_CATEGORY
import com.alfonsocastro.breakingfans.util.BREAKING_BAD_CATEGORY

private const val TAG = "CharacterDetailFragment"

class CharacterDetailFragment : Fragment() {

    // Binding object instance corresponding to the xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentCharacterDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: CharacterSharedViewModel by activityViewModels {
        CharacterSharedViewModel.CharacterSharedViewModelFactory(
            CharacterRepository(
                BreakingBadApi.retrofitService,
                (activity?.application as BreakingBadApplication).database
            )
        )
    }

    private val args: CharacterDetailFragmentArgs by navArgs()

    private var isInFavorites: Boolean = false
    private lateinit var character: Character

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterDetailBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isInFavorites = args.isFavorite

        // Manage if is in favorites
        if (isInFavorites) {
            if (args.characterId != -1) {
                sharedViewModel.getFavorite(args.characterId)
                    .observe(viewLifecycleOwner) { character ->
                        displayCharacter(character)
                        binding.characterFavoritesButton.apply {
                            text = getString(R.string.remove_from_favorites)
                            setOnClickListener { removeFromFavorites(character) }
                        }
                    }
            }
        } else {
            // Observe ViewModel Character List and submit the adapter the new list.
            sharedViewModel.selectedCharacter.value?.let {
                displayCharacter(it)
                binding.characterFavoritesButton.apply {
                    text = getString(R.string.save_to_favorites)
                    setOnClickListener { saveToFavorites() }
                }
            }
        }
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

    private fun saveToFavorites() {
        sharedViewModel.saveSelectedCharacterToFavorites()
        Toast.makeText(
            requireContext(),
            R.string.save_to_favorites_success_message,
            Toast.LENGTH_SHORT
        )
            .show()
        binding.characterFavoritesButton.isEnabled = false
    }

    private fun removeFromFavorites(character: Character) {
        sharedViewModel.deteletFavorite(character)
        findNavController().navigateUp()
    }
}