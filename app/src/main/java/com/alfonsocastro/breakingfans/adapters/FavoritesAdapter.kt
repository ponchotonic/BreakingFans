package com.alfonsocastro.breakingfans.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.alfonsocastro.breakingfans.R
import com.alfonsocastro.breakingfans.databinding.ListItemFavoriteBinding
import com.alfonsocastro.breakingfans.model.Character

class FavoritesAdapter(
    private val onItemClicked: (Character) -> Unit,
    private val onDeleteClicked: (Character) -> Unit
) : ListAdapter<Character,
        FavoritesAdapter.FavoriteViewHolder>(FavoriteViewHolder) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        // We create a normal [FavoriteViewHolder] inflating the Binding
        val viewHolder = FavoriteViewHolder(
            ListItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onDeleteClicked
        )
        // Then we call the onItemClicked to pass the current Hero
        viewHolder.itemView.setOnClickListener {
            val position =
                viewHolder.bindingAdapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class FavoriteViewHolder(
        private val binding: ListItemFavoriteBinding,
        private val onDeleteClicked: (Character) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the character provided with the [ListItemFavoriteBinding] [Character].
         * Then calls to execute pending bindings method.
         */
        fun bind(character: Character) {
            binding.favoriteImage.load(character.image) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.ic_broken_image)
            }
            binding.iconDelete.setOnClickListener { onDeleteClicked(character) }
            binding.favoriteName.text = character.name
            binding.favoriteNickname.text =
                binding.root.context.getString(R.string.nickname_format, character.nickname)
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