package com.openclassrooms.realestatemanager.ui.update

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
import androidx.navigation.fragment.navArgs
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentUpdateBinding
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var estateViewModel: EstateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]

        binding.updateType.setText(args.currentEstate.type)
        binding.updateDistrict.setText(args.currentEstate.district)
        binding.updatePrice.setText(args.currentEstate.price.toString())

        binding.updateFab.setOnClickListener {
            updateItem()
        }

        return binding.root
    }

    private fun updateItem() {
        val type = binding.updateType.text.toString()
        val district = binding.updateDistrict.text.toString()
        val price = binding.updatePrice.text

        if (inputCheck(type, district, price)) {
            // Create Estate object
            val updatedEstate = Estate(
                args.currentEstate.id, type, district, Integer.parseInt(price.toString())
            )
            // Update current Estate
            estateViewModel.updateEstate(updatedEstate)
            // Navigate back to list
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
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