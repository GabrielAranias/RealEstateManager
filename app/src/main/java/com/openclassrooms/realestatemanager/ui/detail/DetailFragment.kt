package com.openclassrooms.realestatemanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        initUi()
        initFab()

        return binding.root
    }

    // Set up layout w/ estate's details
    private fun initUi() {
        binding.detailDescription.text = args.currentEstate.description
        binding.detailSurface.text =
            requireContext().getString(
                R.string.detail_surface,
                args.currentEstate.surface.toString()
            )
        binding.detailRealtor.text =
            requireContext().getString(R.string.detail_realtor, args.currentEstate.realtor)
        binding.detailStatus.text =
            requireContext().getString(R.string.detail_status, args.currentEstate.status)
        binding.detailRooms.text =
            requireContext().getString(
                R.string.detail_rooms,
                args.currentEstate.rooms.nbRooms.toString()
            )
        binding.detailBedrooms.text =
            requireContext().getString(
                R.string.detail_bedrooms,
                args.currentEstate.rooms.nbBedrooms.toString()
            )
        binding.detailBathrooms.text =
            requireContext().getString(
                R.string.detail_bathrooms,
                args.currentEstate.rooms.nbBathrooms.toString()
            )
        binding.detailStreet.text = args.currentEstate.location.street
        binding.detailCity.text = args.currentEstate.location.city
        binding.detailPostalCode.text = args.currentEstate.location.postalCode.toString()
        binding.detailCountry.text = args.currentEstate.location.country
        binding.detailEntryDate.text = requireContext().getString(
            R.string.detail_entry_date, args.currentEstate.dates.entryDate
        )
        binding.detailSaleDate.text = requireContext().getString(
            R.string.detail_sale_date, args.currentEstate.dates.saleDate
        )
    }

    // Set up fab to navigate to UpdateFragment on click
    private fun initFab() {
        binding.detailFab.setOnClickListener {
            val currentEstate = Estate(
                args.currentEstate.id,
                args.currentEstate.type,
                args.currentEstate.district,
                args.currentEstate.price,
                args.currentEstate.description,
                args.currentEstate.surface,
                args.currentEstate.realtor,
                args.currentEstate.status,
                args.currentEstate.rooms,
                args.currentEstate.location,
                args.currentEstate.dates
            )
            val action =
                DetailFragmentDirections.actionDetailFragmentToUpdateFragment(currentEstate)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}