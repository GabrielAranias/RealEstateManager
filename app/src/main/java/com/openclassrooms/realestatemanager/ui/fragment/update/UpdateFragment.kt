package com.openclassrooms.realestatemanager.ui.fragment.update

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentUpdateBinding
import com.openclassrooms.realestatemanager.ui.fragment.add.GridAdapter
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var estateViewModel: EstateViewModel
    private var vicinity = ArrayList<String>()
    private val photoUris = ArrayList<Uri>()
    private var photoCaptions = ArrayList<String>()
    private lateinit var adapter: GridAdapter
    private lateinit var imagePicker: ImagePicker
    private var allFieldsChecked = false

    // Date format for date picker
    private val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)

        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]
        imagePicker = ImagePicker(this)

        initUi()
        initDropDownMenus()
        initChipGroup()
        initDateBtn()
        initPhotoHandling()
        initFab()

        return binding.root
    }

    //Set up layout w/ pre-filled info from current item
    private fun initUi() {
        // Get details
        binding.apply {
            updateType.setText(args.currentEstate.type)
            updateDistrict.setText(args.currentEstate.district)
            updatePrice.setText(args.currentEstate.price.toString())
            updateDescription.setText(args.currentEstate.description)
            updateSurface.setText(args.currentEstate.surface.toString())
            updateRealtor.setText(args.currentEstate.realtor)
            updateStatus.setText(args.currentEstate.status)
            updateRooms.setText(args.currentEstate.nbRooms.toString())
            updateBedrooms.setText(args.currentEstate.nbBedrooms.toString())
            updateBathrooms.setText(args.currentEstate.nbBathrooms.toString())
            updateAddress.setText(args.currentEstate.address)
            updateSelectedEntryDate.text = args.currentEstate.entryDate
            updateSelectedSaleDate.text = args.currentEstate.saleDate
        }
        // Get POI chips
        getChips()
        // Get photos
        getPhotos()
    }

    private fun getChips() {
        vicinity = args.currentEstate.vicinity
        for (value in vicinity) {
            val chip = Chip(requireContext())
            chip.text = value
            chip.isCloseIconVisible = true
            binding.updateChipGroup.addView(chip)
            chip.setOnCloseIconClickListener {
                binding.updateChipGroup.removeView(chip)
                vicinity.remove(value)
            }
        }
    }

    private fun getPhotos() {
        // Get uris
        for (strUri in args.currentEstate.photoUris) {
            val uri = Uri.parse(strUri)
            photoUris.add(uri)
        }
        // Get captions
        photoCaptions = args.currentEstate.photoCaptions
        // Init RecyclerView
        val recyclerView = binding.updatePhotoList
        adapter = GridAdapter(photoUris, photoCaptions, requireContext())
        recyclerView.adapter = adapter
    }

    // Set up drop-down menus for estate's type x status
    private fun initDropDownMenus() {
        // Estate's type
        val types = resources.getStringArray(R.array.types)
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.updateType.setAdapter(typeAdapter)
        // Estate's status
        val status = resources.getStringArray(R.array.status)
        val statusAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, status)
        binding.updateStatus.setAdapter(statusAdapter)
    }

    // Set up chip group to select POIs
    private fun initChipGroup() {
        binding.updateVicinityBtn.setOnClickListener {
            if (binding.updateVicinity.toString().isNotEmpty()) {
                addChip(binding.updateVicinity.text.toString())
                binding.updateVicinity.setText("")
            }
        }
    }

    private fun addChip(text: String) {
        val chip = Chip(requireContext())
        chip.text = text
        chip.isCloseIconVisible = true
        binding.updateChipGroup.addView(chip)
        vicinity.add(text)
        chip.setOnCloseIconClickListener {
            binding.updateChipGroup.removeView(chip)
            vicinity.remove(text)
        }
    }

    // Set up btn to pick market entry x sale date
    private fun initDateBtn() {
        // Estate's date of market entry btn
        binding.updateEntryDate.setOnClickListener {
            val entryPicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.date_picker_entry)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            entryPicker.addOnPositiveButtonClickListener {
                val entrySelectedDate = outputDateFormat.format(it)
                binding.updateSelectedEntryDate.text = entrySelectedDate
            }
            entryPicker.show(parentFragmentManager, "Entry Date Picker")
        }
        // Estate's date of sale btn
        binding.updateSaleDate.setOnClickListener {
            val salePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.date_picker_sale)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            salePicker.addOnPositiveButtonClickListener {
                val saleSelectedDate = outputDateFormat.format(it)
                binding.updateSelectedSaleDate.text = saleSelectedDate
            }
            salePicker.show(parentFragmentManager, "Sale Date Picker")
        }
    }

    // Set up btn to pick photo in gallery or take picture w/ camera
    private fun initPhotoHandling() {
        // Camera btn
        binding.updatePhotoCamera.setOnClickListener {
            imagePicker.takeFromCamera { imageResult ->
                imageCallback(imageResult)
            }
        }
        // Gallery btn
        binding.updatePhotoGallery.setOnClickListener {
            imagePicker.pickFromStorage { imageResult ->
                imageCallback(imageResult)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun imageCallback(imageResult: ImageResult<Uri>) {
        when (imageResult) {
            is ImageResult.Success -> {
                val builder = AlertDialog.Builder(requireContext())
                val editText = EditText(context)
                builder.setTitle(R.string.alert_dialog_title)
                    .setView(editText)
                    .setPositiveButton(R.string.toolbar_add) { _, _ ->
                        // Add photo w/ caption
                        val uri = imageResult.value
                        photoUris.add(uri)
                        photoCaptions.add(editText.text.toString())
                        adapter.notifyDataSetChanged()
                    }
                    .show()
            }
            is ImageResult.Failure -> {
                val errorString = imageResult.errorString
                Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Set up fab to update item w/ new info
    private fun initFab() {
        binding.updateFab.setOnClickListener {
            // Check if some required fields are empty x prompt user to fill them in
            allFieldsChecked = checkAllFields()
            // After validation
            if (allFieldsChecked) {
                // Convert uris to strings
                val uris = ArrayList<String>()
                for (uri in photoUris) {
                    val strUri = uri.toString()
                    uris.add(strUri)
                }
                // Create Estate object
                val updatedEstate = Estate(
                    args.currentEstate.id,
                    type = binding.updateType.text.toString(),
                    district = binding.updateDistrict.text.toString(),
                    price = Integer.parseInt(binding.updatePrice.text.toString()),
                    description = binding.updateDescription.text.toString(),
                    surface = Integer.parseInt("0" + binding.updateSurface.text.toString()),
                    realtor = binding.updateRealtor.text.toString(),
                    status = binding.updateStatus.text.toString(),
                    nbRooms = Integer.parseInt("0" + binding.updateRooms.text.toString()),
                    nbBedrooms = Integer.parseInt("0" + binding.updateBedrooms.text.toString()),
                    nbBathrooms = Integer.parseInt("0" + binding.updateBathrooms.text.toString()),
                    address = binding.updateAddress.text.toString(),
                    entryDate = binding.updateSelectedEntryDate.text.toString(),
                    saleDate = binding.updateSelectedSaleDate.text.toString(),
                    vicinity,
                    uris,
                    photoCaptions
                )
                // Update current Estate
                estateViewModel.updateEstate(updatedEstate)
                // Navigate back to list
                findNavController().navigate(R.id.action_updateFragment_to_listFragment)
            }
        }
    }

    private fun checkAllFields(): Boolean {
        binding.apply {
            // Type
            if (updateType.text.toString().isEmpty()) {
                updateType.error = getString(R.string.error_type)
                return false
            } else {
                updateType.error = null
            }
            // Price
            if (updatePrice.text.toString().isEmpty()) {
                updatePrice.error = getString(R.string.error_price)
                return false
            } else {
                updatePrice.error = null
            }
            // Address
            if (updateAddress.text.toString().isEmpty()) {
                updateAddress.error = getString(R.string.error_address)
                return false
            } else {
                updateAddress.error = null
            }
            // District
            if (updateDistrict.text.toString().isEmpty()) {
                updateDistrict.error = getString(R.string.error_district)
                return false
            } else {
                updateDistrict.error = null
            }
            // Status
            if (updateStatus.text.toString() == resources.getStringArray(R.array.status)[1]
                && updateSelectedSaleDate.text == getString(R.string.not_sold)
            ) {
                Toast.makeText(requireContext(), R.string.error_status, Toast.LENGTH_SHORT).show()
                return false
            }
            // Dates
            if (updateSelectedEntryDate.text.toString() == getString(R.string.no_date_selected)) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_entry_date),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
            if (updateSelectedSaleDate.text.toString() != getString(R.string.not_sold) && (updateStatus.text.toString()
                    .isEmpty() || updateStatus.text.toString() == resources.getStringArray(R.array.status)[0])
            ) {
                Toast.makeText(requireContext(), R.string.error_sale_date, Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            // Photo
            if (photoUris.isEmpty()) {
                Toast.makeText(requireContext(), R.string.error_photo, Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}