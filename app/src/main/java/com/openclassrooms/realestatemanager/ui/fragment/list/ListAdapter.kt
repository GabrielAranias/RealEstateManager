package com.openclassrooms.realestatemanager.ui.fragment.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.EstateItemBinding

class ListAdapter : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private var estateList = emptyList<Estate>()
    private lateinit var context: Context

    inner class ListViewHolder(val binding: EstateItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        context = parent.context

        return ListViewHolder(
            EstateItemBinding.inflate(LayoutInflater.from(context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = estateList[position]
        holder.binding.apply {
            // Photo
            Glide.with(context)
                .load(currentItem.photoUris[0])
                .placeholder(R.drawable.nyc_loading_screen)
                .error(R.drawable.ic_baseline_error_outline_24)
                .centerCrop()
                .into(estatePhoto)
            // Type, district x price
            estateType.text = currentItem.type
            estateDistrict.text = currentItem.district
            estatePrice.text = context.getString(R.string.price, currentItem.price.toString())
            // Display ribbon if estate has been sold
            if (currentItem.status == context.resources.getStringArray(R.array.status)[1]) {
                estateRibbon.visibility = View.VISIBLE
            }
            // Display details on click
            estateItem.setOnClickListener {
                val action = ListFragmentDirections.actionListFragmentToDetailFragment(currentItem)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return estateList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(estate: List<Estate>) {
        this.estateList = estate
        notifyDataSetChanged()
    }
}