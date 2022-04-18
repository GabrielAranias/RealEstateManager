package com.openclassrooms.realestatemanager.ui.fragment.update

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentUpdateBinding
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel
import java.text.SimpleDateFormat
import java.util.*

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<UpdateFragmentArgs>()
    private lateinit var estateViewModel: EstateViewModel
    private var vicinity = ArrayList<String>()

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

        initUi()
        initDropDownMenus()
        initChipGroup()
        initDateBtn()
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
        binding.updateStreet.setText(args.currentEstate.street)
        binding.updateCity.setText(args.currentEstate.city)
        binding.updatePostalCode.setText(args.currentEstate.postalCode.toString())
        binding.updateCountry.setText(args.currentEstate.country)
        binding.updateSelectedEntryDate.text = args.currentEstate.entryDate
        binding.updateSelectedSaleDate.text = args.currentEstate.saleDate
        // Get POI chips
        getChips()
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

    // Set up fab to update item w/ new info
    private fun initFab() {
        binding.updateFab.setOnClickListener {
            val type = binding.updateType.text.toString()
            val district = binding.updateDistrict.text.toString()
            val price = binding.updatePrice.text

            if (inputCheck(type, district, price)) {
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
                    street = binding.updateStreet.text.toString(),
                    city = binding.updateCity.text.toString(),
                    postalCode = Integer.parseInt(binding.updatePostalCode.text.toString()),
                    country = binding.updateCountry.text.toString(),
                    entryDate = binding.updateSelectedEntryDate.text.toString(),
                    saleDate = binding.updateSelectedSaleDate.text.toString(),
                    vicinity
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