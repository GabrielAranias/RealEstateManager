package com.openclassrooms.realestatemanager.ui.fragment.detail

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentDetailBinding

class DetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        // Build lite mode map to display location details
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.detail_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
        binding.detailAddress.text = args.currentEstate.address
        binding.detailEntryDate.text = requireContext().getString(
            R.string.detail_entry_date, args.currentEstate.entryDate
        )
        binding.detailSaleDate.text = requireContext().getString(
            R.string.detail_sale_date, args.currentEstate.saleDate
        )
        binding.detailVicinity.text = args.currentEstate.vicinity.joinToString(", ")
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
                args.currentEstate.address,
                args.currentEstate.entryDate,
                args.currentEstate.saleDate,
                args.currentEstate.vicinity
            )
            val action =
                DetailFragmentDirections.actionDetailFragmentToUpdateFragment(currentEstate)
            findNavController().navigate(action)
        }
    }

    override fun onMapReady(map: GoogleMap) {
        // UI settings to get a static map
        map.uiSettings.isScrollGesturesEnabled = false
        map.uiSettings.isZoomGesturesEnabled = false
        // Add marker
        val marker = getLocationWithAddress(requireContext(), args.currentEstate.address)
        if (marker != null) {
            map.addMarker(MarkerOptions().position(marker))
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
        }
    }

    private fun getLocationWithAddress(context: Context, strAddress: String): LatLng? {
        val geocoder = Geocoder(context)
        try {
            val address = geocoder.getFromLocationName(strAddress, 1) ?: return null
            val location = address.first()
            return LatLng(location.latitude, location.longitude)
        } catch (e: Exception) {
            Log.w("TAG", "getLocationWithAddress: $e")
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}