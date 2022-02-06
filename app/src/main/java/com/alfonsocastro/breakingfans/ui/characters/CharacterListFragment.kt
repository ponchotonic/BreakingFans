package com.alfonsocastro.breakingfans.ui.characters

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfonsocastro.breakingfans.BreakingBadApplication
import com.alfonsocastro.breakingfans.R
import com.alfonsocastro.breakingfans.adapters.CharacterAdapter
import com.alfonsocastro.breakingfans.data.CharacterRepository
import com.alfonsocastro.breakingfans.data.remote.BreakingBadApi
import com.alfonsocastro.breakingfans.databinding.FragmentCharacterListBinding
import com.alfonsocastro.breakingfans.model.Character

class CharacterListFragment : Fragment() {

    // Binding object instance corresponding to the xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentCharacterListBinding? = null

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

    private val adapter = CharacterAdapter(
        onItemClicked = { onCharacterSelected(it) },
        onFavoriteClicked = { onFavoriteIconClicked(it) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_character_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                openFavorites()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCharacterListBinding.inflate(inflater, container, false)

        binding.charactersRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.charactersRecycler.adapter = adapter

        // Observe ViewModel API Status
        sharedViewModel.status.observe(viewLifecycleOwner) { status ->
            bindStatus(status)
        }

        // Observe ViewModel List and submit the adapter the new list.
        sharedViewModel.characters.observe(viewLifecycleOwner) { heroList ->
            (binding.charactersRecycler.adapter as CharacterAdapter).submitList(heroList)
        }


        // Observe ViewModel Favorites
        sharedViewModel.favorites.observe(viewLifecycleOwner) { favorites ->

        }

        // Set Buttons Listeners
        binding.tryAgainButton.setOnClickListener { tryLoadingListAgain() }

        return binding.root
    }

    private fun bindStatus(status: CharacterApiStatus) {
        when (status) {
            CharacterApiStatus.LOADING -> {
                // Show Status UI
                binding.statusImage.visibility = View.VISIBLE
                binding.statusImage.setImageResource(R.drawable.loading_animation)
                binding.statusTextView.visibility = View.VISIBLE
                binding.statusTextView.text = getString(R.string.loading)
                // Hide Error UI
                binding.tryAgainButton.visibility = View.GONE
                // Hide Done UI
                binding.charactersRecycler.visibility = View.GONE
            }
            CharacterApiStatus.ERROR -> {
                // Show Status UI
                binding.statusImage.visibility = View.VISIBLE
                binding.statusImage.setImageResource(R.drawable.ic_connection_error)
                binding.statusTextView.visibility = View.VISIBLE
                binding.statusTextView.text = getString(R.string.error_loading)
                // Show Error UI
                binding.tryAgainButton.visibility = View.VISIBLE
                // Hide Done UI
                binding.charactersRecycler.visibility = View.GONE
            }
            CharacterApiStatus.DONE -> {
                // Hide status UI
                binding.statusImage.visibility = View.GONE
                binding.statusTextView.visibility = View.GONE
                // Hide Error UI
                binding.tryAgainButton.visibility = View.GONE
                // Show Done UI
                binding.charactersRecycler.visibility = View.VISIBLE
            }
        }
    }

    private fun tryLoadingListAgain() {
        sharedViewModel.getCharacters()
    }

    private fun onCharacterSelected(selectedCharacter: Character) {
        // Set selected character in sharedViewModel
        sharedViewModel.setSelectedCharacter(selectedCharacter)
        // Navigate to DetailFragment
        findNavController().navigate(R.id.action_characterListFragment_to_characterDetailFragment)
    }

    private fun onFavoriteIconClicked(character: Character) {

        // Get String and method called.
        val toastStringResource: Int = if (character.isFavorite) {
            sharedViewModel.deleteFavorite(character)
            R.string.delete_from_favorites_success_message
        } else {
            sharedViewModel.saveFavorite(character)
            R.string.save_to_favorites_success_message
        }
        // Notify the adapter the item changed
        val position = adapter.currentList.indexOf(character)
        if (position != -1) {
            adapter.notifyItemChanged(position)
        }
        // Show Toast
        Toast.makeText(
            requireContext(),
            toastStringResource,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun openFavorites() {
        findNavController().navigate(R.id.action_characterListFragment_to_favoritesFragment)
    }

}