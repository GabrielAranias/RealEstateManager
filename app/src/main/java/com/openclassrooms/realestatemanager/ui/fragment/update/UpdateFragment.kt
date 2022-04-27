package com.openclassrooms.realestatemanager.ui.fragment.update

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
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
        binding.updateType.setText(args.currentEstate.type)
        binding.updateDistrict.setText(args.currentEstate.district)
        binding.updatePrice.setText(args.currentEstate.price.toString())
        binding.updateDescription.setText(args.currentEstate.description)
        binding.updateSurface.setText(args.currentEstate.surface.toString())
        binding.updateRealtor.setText(args.currentEstate.realtor)
        binding.updateStatus.setText(args.currentEstate.status)
        binding.updateRooms.setText(args.currentEstate.nbRooms.toString())
        binding.updateBedrooms.setText(args.currentEstate.nbBedrooms.toString())
        binding.updateBathrooms.setText(args.currentEstate.nbBathrooms.toString())
        binding.updateAddress.setText(args.currentEstate.address)
        binding.updateSelectedEntryDate.text = args.currentEstate.entryDate
        binding.updateSelectedSaleDate.text = args.currentEstate.saleDate
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
        // Get uris x captions
        val strUris = args.currentEstate.photoUris
        for (strUri in strUris) {
            val uri = Uri.parse(strUri)
            photoUris.add(uri)
        }
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
            val type = binding.updateType.text.toString()
            val district = binding.updateDistrict.text.toString()
            val price = binding.updatePrice.text

            if (inputCheck(type, district, price)) {
                // Convert uris to strings
                val uris = ArrayList<String>()
                for (uri in photoUris) {
                    val strUri = uri.toString()
                    uris.add(strUri)
                }
                // Create Estate object
                val updatedEstate = Estate(
                    args.currentEstate.id,
                    type,
                    district,
                    Integer.parseInt(price.toString()),
                    description = binding.updateDescription.text.toString(),
                    surface = Integer.parseInt(binding.updateSurface.text.toString()),
                    realtor = binding.updateRealtor.text.toString(),
                    status = binding.updateStatus.text.toString(),
                    nbRooms = Integer.parseInt(binding.updateRooms.text.toString()),
                    nbBedrooms = Integer.parseInt(binding.updateBedrooms.text.toString()),
                    nbBathrooms = Integer.parseInt(binding.updateBathrooms.text.toString()),
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