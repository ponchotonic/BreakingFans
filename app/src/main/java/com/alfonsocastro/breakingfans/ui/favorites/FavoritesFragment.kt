package com.alfonsocastro.breakingfans.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfonsocastro.breakingfans.BreakingBadApplication
import com.alfonsocastro.breakingfans.adapters.FavoritesAdapter
import com.alfonsocastro.breakingfans.databinding.FragmentFavoritesBinding
import com.alfonsocastro.breakingfans.model.Character

class FavoritesFragment : Fragment() {

    // Binding object instance corresponding to the xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentFavoritesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val viewModel: FavoritesViewModel by viewModels {
        FavoritesViewModel.FavoritesViewModelFactory(
            (activity?.application as BreakingBadApplication).database.characterDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FavoritesAdapter(
            onItemClicked = { viewFavorite(it) },
            onDeleteClicked = { deleteFavorite(it) }
        )
        binding.favoritesRecycler.layoutManager = LinearLayoutManager(this.context)
        binding.favoritesRecycler.adapter = adapter

        // Observe ViewModel Character List and submit the adapter the new list.
        viewModel.favorites.observe(this.viewLifecycleOwner) { characters ->
            characters.let {
                adapter.submitList(it)
                binding.emptyListTextView.visibility = if (it.isEmpty()) {
                    View.VISIBLE
                } else {
                    View.GONE
                }
            }
        }
    }

    private fun viewFavorite(character: Character) {
        // Navigate to DetailFragment
        val action =
            FavoritesFragmentDirections.actionFavoritesFragmentToCharacterDetailFragment(
                isFavorite = true, characterId = character.id
            )
        findNavController().navigate(action)
    }

    private fun deleteFavorite(character: Character) {
        viewModel.deleteFavorite(character)
    }
}