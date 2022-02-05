package com.alfonsocastro.breakingfans.ui.characters

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alfonsocastro.breakingfans.R
import com.alfonsocastro.breakingfans.adapters.CharacterAdapter
import com.alfonsocastro.breakingfans.data.CharacterRepository
import com.alfonsocastro.breakingfans.data.local.BreakingBadDatabase
import com.alfonsocastro.breakingfans.data.remote.BreakingBadApi
import com.alfonsocastro.breakingfans.databinding.FragmentCharacterListBinding

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
                BreakingBadDatabase.getDatabase(requireContext().applicationContext)
            )
        )
    }

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
        binding.charactersRecycler.adapter = CharacterAdapter { selectedCharacter ->
            // Set selected character in sharedViewModel
            sharedViewModel.setSelectedCharacter(selectedCharacter)
            // Navigate to DetailFragment
            findNavController().navigate(R.id.action_characterListFragment_to_characterDetailFragment)
        }


        // Update UI when loading
        sharedViewModel.status.observe(viewLifecycleOwner) { status ->
            bindStatus(status)
        }

        // Observe ViewModel Character List and submit the adapter the new list.
        sharedViewModel.characters.observe(viewLifecycleOwner) { heroList ->
            (binding.charactersRecycler.adapter as CharacterAdapter).submitList(heroList)
        }

        return binding.root
    }

    private fun bindStatus(status: CharacterApiStatus) {
        when (status) {
            CharacterApiStatus.LOADING -> {
                // Show Status Image to Loading Icon
                binding.statusImage.visibility = View.VISIBLE
                binding.statusImage.setImageResource(R.drawable.loading_animation)
                // Hide RecyclerView and TextView
                binding.emptyListTextView.visibility = View.GONE
                binding.emptyListTextView.text = getString(R.string.loading)
            }
            CharacterApiStatus.ERROR -> {
                // Show Status Image to Error Icon
                binding.statusImage.visibility = View.VISIBLE
                binding.statusImage.setImageResource(R.drawable.ic_connection_error)
                // Hide RecyclerView
                // Show empty TextView
                binding.emptyListTextView.visibility = View.VISIBLE
                binding.emptyListTextView.text = getString(R.string.error_loading)
            }
            CharacterApiStatus.DONE -> {
                // Hide status image and empty TextView
                binding.statusImage.visibility = View.GONE
                binding.emptyListTextView.visibility = View.GONE
                // Show RecyclerView
            }
        }
    }

    private fun openFavorites() {
        findNavController().navigate(R.id.action_characterListFragment_to_favoritesFragment)
    }

}