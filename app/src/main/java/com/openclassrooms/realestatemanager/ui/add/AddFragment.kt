package com.openclassrooms.realestatemanager.ui.add

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.ui.EstateViewModel

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var estateViewModel: EstateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]

        binding.addFab.setOnClickListener {
            insertDataIntoDb()
        }

        return binding.root
    }

    private fun insertDataIntoDb() {
        val type = binding.addType.text.toString()
        val district = binding.addDistrict.text.toString()
        val price = binding.addPrice.text

        if (inputCheck(type, district, price)) {
            // Create Estate object
            val estate = Estate(0, type, district, Integer.parseInt(price.toString()))
            // Add data to db
            estateViewModel.addEstate(estate)
            // Navigate back
            findNavController().navigate(R.id.action_addFragment_to_mainFragment)
        } else {
            Toast.makeText(requireContext(), R.string.add_error_msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(type: String, district: String, price: Editable?): Boolean {
        return !(TextUtils.isEmpty(type) && TextUtils.isEmpty(district) && price!!.isEmpty())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}