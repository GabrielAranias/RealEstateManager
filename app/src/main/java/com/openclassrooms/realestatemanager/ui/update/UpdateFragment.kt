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
import com.openclassrooms.realestatemanager.data.model.Location
import com.openclassrooms.realestatemanager.data.model.Rooms
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

        initUi()
        initFab()

        return binding.root
    }

    //Set up layout w/ pre-filled info from current item
    private fun initUi() {
        binding.updateType.setText(args.currentEstate.type)
        binding.updateDistrict.setText(args.currentEstate.district)
        binding.updatePrice.setText(args.currentEstate.price.toString())
        binding.updateDescription.setText(args.currentEstate.description)
        binding.updateSurface.setText(args.currentEstate.surface.toString())
        binding.updateRealtor.setText(args.currentEstate.realtor)
        binding.updateStatus.setText(args.currentEstate.status)
        binding.updateRooms.setText(args.currentEstate.rooms.nbRooms.toString())
        binding.updateBedrooms.setText(args.currentEstate.rooms.nbBedrooms.toString())
        binding.updateBathrooms.setText(args.currentEstate.rooms.nbBathrooms.toString())
        binding.updateStreet.setText(args.currentEstate.location.street)
        binding.updateCity.setText(args.currentEstate.location.city)
        binding.updatePostalCode.setText(args.currentEstate.location.postalCode.toString())
        binding.updateCountry.setText(args.currentEstate.location.country)
    }

    // Set up fab to update item w/ new info
    private fun initFab() {
        binding.updateFab.setOnClickListener {
            val type = binding.updateType.text.toString()
            val district = binding.updateDistrict.text.toString()
            val price = binding.updatePrice.text

            if (inputCheck(type, district, price)) {
                // Create Estate object
                val rooms = Rooms(
                    nbRooms = Integer.parseInt(binding.updateRooms.text.toString()),
                    nbBedrooms = Integer.parseInt(binding.updateBedrooms.text.toString()),
                    nbBathrooms = Integer.parseInt(binding.updateBathrooms.text.toString())
                )
                val location = Location(
                    street = binding.updateStreet.text.toString(),
                    city = binding.updateCity.text.toString(),
                    postalCode = Integer.parseInt(binding.updatePostalCode.text.toString()),
                    country = binding.updateCountry.text.toString()
                )
                val updatedEstate = Estate(
                    args.currentEstate.id,
                    type,
                    district,
                    Integer.parseInt(price.toString()),
                    description = binding.updateDescription.text.toString(),
                    surface = Integer.parseInt(binding.updateSurface.text.toString()),
                    realtor = binding.updateRealtor.text.toString(),
                    status = binding.updateStatus.text.toString(),
                    rooms,
                    location
                )
                // Update current Estate
                estateViewModel.updateEstate(updatedEstate)
                // Navigate back to list
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
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