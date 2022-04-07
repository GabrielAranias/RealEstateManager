package com.openclassrooms.realestatemanager.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.EstateItemBinding

class EstateListAdapter : RecyclerView.Adapter<EstateListAdapter.ListViewHolder>() {

    private var estateList = emptyList<Estate>()
    private lateinit var context: Context

    class ListViewHolder(val binding: EstateItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        context = parent.context

        return ListViewHolder(
            EstateItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val currentItem = estateList[position]
        holder.binding.estateType.text = currentItem.type
        holder.binding.estateDistrict.text = currentItem.district
        holder.binding.estatePrice.text =
            context.getString(R.string.price, currentItem.price.toString())
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