package com.openclassrooms.realestatemanager.ui.fragment.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentMapBinding
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel
import com.openclassrooms.realestatemanager.utils.Constants

class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var locationPermissionGranted = false
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null
    private var lastKnownLocation: Location? = null

    // NYC coordinates
    private val defaultLocation = LatLng(40.730610, -73.935242)
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var estateViewModel: EstateViewModel
    private lateinit var allEstates: List<Estate>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                locationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION]
                    ?: locationPermissionGranted
            }

        // Retrieve location x camera position from saved instance state
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(Constants.LOCATION)
            cameraPosition = savedInstanceState.getParcelable(Constants.CAMERA_POSITION)
        }
        // Construct FusedLocationProviderClient
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        // Build map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        // Prompt user for permission
        requestLocationPermission()
        // Turn on location layer x related control on map
        updateLocationUI()
        // Get device's current location x set position of map
        getDeviceLocation()
        // Display markers to locate estates on map
        getEstates()
        // Handle click on marker to display estate's details
        map.setOnInfoWindowClickListener(this)
    }

    private fun getEstates() {
        estateViewModel.readAllData.observe(viewLifecycleOwner) { estateList ->
            map?.clear()
            allEstates = estateList
            for (estate in estateList) {
                val location = getLocationWithAddress(requireContext(), estate.address)
                if (location != null) {
                    val markerOptions = MarkerOptions()
                        .position(location)
                        .title(estate.type)
                        .snippet(estate.address)
                    val marker = map?.addMarker(markerOptions)
                    marker?.tag = estateList.indexOf(estate)
                }
            }
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

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set map's camera position to current location of device
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), 15f
                                )
                            )
                        }
                    } else {
                        Log.d("TAG", "Current location is null. Using defaults.")
                        Log.e("TAG", "Exception: %s", task.exception)
                        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.apply {
                    isMyLocationButtonEnabled = true
                    isZoomControlsEnabled = true
                    isZoomGesturesEnabled = true
                    isScrollGesturesEnabled = true
                }
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                requestLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun requestLocationPermission() {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest: MutableList<String> = ArrayList()
        if (!locationPermissionGranted) {
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (permissionRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionRequest.toTypedArray())
        }
    }

    // Save map state when activity is paused
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(Constants.CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(Constants.LOCATION, lastKnownLocation)
        }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val currentItem = allEstates[marker.tag as Int]
        val action = MapFragmentDirections.actionMapFragmentToDetailFragment(currentItem)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}