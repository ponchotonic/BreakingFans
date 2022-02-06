package com.alfonsocastro.breakingfans.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.alfonsocastro.breakingfans.R
import com.alfonsocastro.breakingfans.databinding.ListItemCharacterBinding
import com.alfonsocastro.breakingfans.model.Character

class CharacterAdapter(
    private val onItemClicked: (Character) -> Unit,
    private val onFavoriteClicked: (Character) -> Unit
) : ListAdapter<Character,
        CharacterAdapter.CharacterViewHolder>(CharacterViewHolder) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CharacterViewHolder {
        // We create a normal [CharacterViewHolder] inflating the Binding
        val viewHolder = CharacterViewHolder(
            ListItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onFavoriteClicked
        )
        // Then we call the onItemClicked to pass the current Hero
        viewHolder.itemView.setOnClickListener {
            val position =
                viewHolder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    // We get the current Character from the ListAdapter getItem method and call the ViewHolder.bind() method.
    override fun onBindViewHolder(holder: CharacterViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class CharacterViewHolder(
        private var binding: ListItemCharacterBinding,
        private val onFavoriteClicked: (Character) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the hero provided with the [ListItemCharacterBinding] [Character].
         * Then calls to execute pending bindings method.
         */
        fun bind(character: Character) {
            binding.characterImage.load(character.image) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }
            binding.characterName.text = character.name
            binding.characterNickname.text =
                binding.root.context.getString(R.string.nickname_format, character.nickname)
            // Get Favorite Drawable Icon
            val favoriteDrawable = if (character.isFavorite) {
                R.drawable.ic_favorite
            } else {
                R.drawable.ic_favorite_border
            }
            binding.iconFavorite.setImageResource(favoriteDrawable)
            binding.iconFavorite.setOnClickListener { onFavoriteClicked(character) }
        }

        companion object DiffCallback : DiffUtil.ItemCallback<Character>() {
            override fun areItemsTheSame(oldItem: Character, newItem: Character): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Character, newItem: Character): Boolean {
                return oldItem == newItem
            }
        }
    }
}