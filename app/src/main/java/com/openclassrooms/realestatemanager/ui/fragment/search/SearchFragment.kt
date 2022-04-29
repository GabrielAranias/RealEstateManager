package com.openclassrooms.realestatemanager.ui.fragment.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentSearchBinding
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel
import java.text.SimpleDateFormat
import java.util.*

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var estateViewModel: EstateViewModel
    private val adapter = SearchAdapter()
    private var allFieldsChecked = false

    // Date format for date picker
    private val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        initDropDownMenus()
        initDateBtn()
        initRefreshBtn()
        initFab()

        return binding.root
    }

    // Set up drop-down menus for estate's type x status
    private fun initDropDownMenus() {
        // Estate's type
        val types = resources.getStringArray(R.array.types)
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.searchType.setAdapter(typeAdapter)
        // Estate's status
        val status = resources.getStringArray(R.array.status)
        val statusAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, status)
        binding.searchStatus.setAdapter(statusAdapter)
    }

    // Set up btn to pick availability date
    private fun initDateBtn() {
        binding.searchDateBtn.setOnClickListener {
            val entryPicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.date_picker_entry)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            entryPicker.addOnPositiveButtonClickListener {
                val entrySelectedDate = outputDateFormat.format(it)
                binding.searchSelectedDate.text = entrySelectedDate
            }
            entryPicker.show(parentFragmentManager, "Entry Date Picker")
        }
    }

    // Set up fab to empty fields
    private fun initRefreshBtn() {
        binding.searchRefreshBtn.setOnClickListener {
            resetFilters()
        }
    }

    // Set up fab to filter estates w/ filled-in info
    private fun initFab() {
        binding.searchFab.setOnClickListener {
            // Check if some required fields are empty x prompt user to fill them in
            allFieldsChecked = checkAllFields()
            // After validation
            if (allFieldsChecked) {
                binding.searchResults.visibility = View.VISIBLE
                initRecyclerView()
                initViewModel()
            }
        }
    }

    private fun initRecyclerView() {
        val recyclerView = binding.searchResultList
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        )
    }

    private fun initViewModel() {
        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]
        estateViewModel.readAllData.observe(viewLifecycleOwner) { estateList ->

            val filters = filterEstates(estateList, generateFilters())

            if (filters.isNotEmpty()) {
                adapter.setData(filters)
                binding.searchResultList.visibility = View.VISIBLE
                binding.searchNoResult.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    getString(R.string.search_result_msg, filters.size),
                    Toast.LENGTH_SHORT
                ).show()
                resetFilters()
            } else {
                binding.searchResultList.visibility = View.GONE
                binding.searchNoResult.visibility = View.VISIBLE
            }
        }
    }

    private fun generateFilters() = listOf<(Estate) -> Boolean>(
        { it.type == binding.searchType.text.toString() },
        { it.status == binding.searchStatus.text.toString() },
        {
            it.surface >= Integer.parseInt(binding.searchSurfaceMin.text.toString()) &&
                    it.surface <= Integer.parseInt(binding.searchSurfaceMax.text.toString())
        },
        {
            it.nbRooms >= Integer.parseInt(binding.searchRoomsMin.text.toString()) &&
                    it.nbRooms <= Integer.parseInt(binding.searchRoomsMax.text.toString())
        },
        {
            it.price >= Integer.parseInt(binding.searchPriceMin.text.toString()) &&
                    it.price <= Integer.parseInt(binding.searchPriceMax.text.toString())
        },
        {
            outputDateFormat.parse(it.entryDate)!! <=
                    outputDateFormat.parse(binding.searchSelectedDate.text.toString())
        }
    )

    private fun filterEstates(estates: List<Estate>, filters: List<(Estate) -> Boolean>) =
        estates.filter { estate -> filters.all { filter -> filter(estate) } }

    private fun resetFilters() {
        binding.searchType.setText("")
        binding.searchStatus.setText("")
        binding.searchSurfaceMin.setText("")
        binding.searchSurfaceMax.setText("")
        binding.searchRoomsMin.setText("")
        binding.searchRoomsMax.setText("")
        binding.searchPriceMin.setText("")
        binding.searchPriceMax.setText("")
        binding.searchSelectedDate.setText(R.string.no_date_selected)
    }

    private fun checkAllFields(): Boolean {
        binding.apply {
            // Type
            if (searchType.text.toString().isEmpty()) {
                searchType.error = getString(R.string.error_type)
                return false
            } else {
                searchType.error = null
            }
            // Status
            if (searchStatus.text.toString().isEmpty()) {
                searchStatus.error = getString(R.string.error_status_search)
                return false
            } else {
                searchStatus.error = null
            }
            // Surface
            if (searchSurfaceMin.text.toString().isEmpty()) {
                searchSurfaceMin.error = getString(R.string.error_surface_min)
                return false
            } else {
                searchSurfaceMin.error = null
            }
            if (searchSurfaceMax.text.toString().isEmpty()) {
                searchSurfaceMax.error = getString(R.string.error_surface_max)
                return false
            } else {
                searchSurfaceMax.error = null
            }
            // No. of rooms
            if (searchRoomsMin.text.toString().isEmpty()) {
                searchRoomsMin.error = getString(R.string.error_rooms_min)
                return false
            } else {
                searchRoomsMin.error = null
            }
            if (searchRoomsMax.text.toString().isEmpty()) {
                searchRoomsMax.error = getString(R.string.error_rooms_max)
                return false
            } else {
                searchRoomsMax.error = null
            }
            // Price
            if (searchPriceMin.text.toString().isEmpty()) {
                searchPriceMin.error = getString(R.string.error_price_min)
                return false
            } else {
                searchPriceMin.error = null
            }
            if (searchPriceMax.text.toString().isEmpty()) {
                searchPriceMax.error = getString(R.string.error_price_max)
                return false
            } else {
                searchPriceMax.error = null
            }
            // Date
            if (searchSelectedDate.text.toString() == getString(R.string.no_date_selected)) {
                Toast.makeText(
                    requireContext(),
                    R.string.error_availability_date,
                    Toast.LENGTH_SHORT
                ).show()
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