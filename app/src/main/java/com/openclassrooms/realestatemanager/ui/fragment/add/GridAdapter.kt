package com.openclassrooms.realestatemanager.ui.fragment.add

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.databinding.PhotoItemBinding

class GridAdapter(private var photoUris: ArrayList<Uri>, private var context: Context) :
    RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    class GridViewHolder(val binding: PhotoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        return GridViewHolder(
            PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        // Display photo
        Glide.with(context)
            .load(photoUris[position])
            .centerCrop()
            .into(holder.binding.photoImage)
        holder.binding.apply {
            // Remove item
            photoDeleteBtn.setOnClickListener {
                photoUris.removeAt(holder.adapterPosition)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, photoUris.size)
            }
        }
    }

    override fun getItemCount(): Int {
        return photoUris.size
    }
}