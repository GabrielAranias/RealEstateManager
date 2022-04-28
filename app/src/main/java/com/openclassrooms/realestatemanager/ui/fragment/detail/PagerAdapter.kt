package com.openclassrooms.realestatemanager.ui.fragment.detail

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.PagerItemBinding

class PagerAdapter(
    private val uris: ArrayList<Uri>,
    private val captions: ArrayList<String>,
    private val context: Context
) : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(val binding: PagerItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PagerAdapter.PagerViewHolder {
        return PagerViewHolder(
            PagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PagerAdapter.PagerViewHolder, position: Int) {
        holder.binding.apply {
            // Display photo
            Glide.with(context)
                .load(uris[position])
                .placeholder(R.drawable.nyc_loading_screen)
                .error(R.drawable.ic_baseline_error_outline_24)
                .centerCrop()
                .into(pagerImage)
            // Set caption
            pagerCaption.text = captions[position]
        }
    }

    override fun getItemCount(): Int {
        return uris.size
    }
}