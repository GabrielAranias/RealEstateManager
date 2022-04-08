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
import com.openclassrooms.realestatemanager.data.model.Location
import com.openclassrooms.realestatemanager.data.model.Rooms
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel

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

        initFab()

        return binding.root
    }

    // Set up fab to create new item w/ info
    private fun initFab() {
        binding.addFab.setOnClickListener {
            val type = binding.addType.text.toString()
            val district = binding.addDistrict.text.toString()
            val price = binding.addPrice.text

            if (inputCheck(type, district, price)) {
                // Create Estate object
                val rooms = Rooms(
                    nbRooms = Integer.parseInt(binding.addRooms.text.toString()),
                    nbBedrooms = Integer.parseInt(binding.addBedrooms.text.toString()),
                    nbBathrooms = Integer.parseInt(binding.addBathrooms.text.toString())
                )
                val location = Location(
                    street = binding.addStreet.text.toString(),
                    city = binding.addCity.text.toString(),
                    postalCode = Integer.parseInt(binding.addPostalCode.text.toString()),
                    country = binding.addCountry.text.toString()
                )
                val estate = Estate(
                    0,
                    type,
                    district,
                    Integer.parseInt(price.toString()),
                    description = binding.addDescription.text.toString(),
                    surface = Integer.parseInt(binding.addSurface.text.toString()),
                    realtor = binding.addRealtor.text.toString(),
                    status = binding.addStatus.text.toString(),
                    rooms,
                    location
                )
                // Add data to db
                estateViewModel.addEstate(estate)
                // Navigate back
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            } else {
                Toast.makeText(requireContext(), R.string.add_error_msg, Toast.LENGTH_SHORT).show()
            }
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