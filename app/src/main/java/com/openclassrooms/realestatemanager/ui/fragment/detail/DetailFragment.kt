package com.openclassrooms.realestatemanager.ui.fragment.detail

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
                args.currentEstate.nbRooms.toString()
            )
        binding.detailBedrooms.text =
            requireContext().getString(
                R.string.detail_bedrooms,
                args.currentEstate.nbBedrooms.toString()
            )
        binding.detailBathrooms.text =
            requireContext().getString(
                R.string.detail_bathrooms,
                args.currentEstate.nbBathrooms.toString()
            )
        binding.detailStreet.text = args.currentEstate.street
        binding.detailCity.text = args.currentEstate.city
        binding.detailPostalCode.text = args.currentEstate.postalCode.toString()
        binding.detailCountry.text = args.currentEstate.country
        binding.detailEntryDate.text = requireContext().getString(
            R.string.detail_entry_date, args.currentEstate.entryDate
        )
        binding.detailSaleDate.text = requireContext().getString(
            R.string.detail_sale_date, args.currentEstate.saleDate
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
                args.currentEstate.nbRooms,
                args.currentEstate.nbBedrooms,
                args.currentEstate.nbBathrooms,
                args.currentEstate.street,
                args.currentEstate.city,
                args.currentEstate.postalCode,
                args.currentEstate.country,
                args.currentEstate.entryDate,
                args.currentEstate.saleDate
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