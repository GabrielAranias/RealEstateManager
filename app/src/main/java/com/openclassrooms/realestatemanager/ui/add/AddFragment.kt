package com.openclassrooms.realestatemanager.ui.add

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Dates
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.data.model.Location
import com.openclassrooms.realestatemanager.data.model.Rooms
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.ui.main.MainActivity
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel
import com.openclassrooms.realestatemanager.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var estateViewModel: EstateViewModel
    private val outputDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]

        initDropDownMenus()
        initDateBtn()
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
                val dates = Dates(
                    entryDate = binding.addSelectedEntryDate.text.toString(),
                    saleDate = binding.addSelectedSaleDate.text.toString()
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
                    location,
                    dates
                )
                // Add data to db
                estateViewModel.addEstate(estate)
                // Send notification to user
                sendNotification()
                // Navigate back
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            } else {
                Toast.makeText(requireContext(), R.string.add_error_msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun sendNotification() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, 0)

        val builder = NotificationCompat.Builder(requireContext(), Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_description))
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.notification_description_long))
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(Constants.NOTIFICATION_ID, builder.build())
        }
    }

    private fun inputCheck(type: String, district: String, price: Editable?): Boolean {
        return !(TextUtils.isEmpty(type) && TextUtils.isEmpty(district) && price!!.isEmpty())
    }

    // Set up btn to pick market entry x sale date
    private fun initDateBtn() {
        // Estate's date of market entry btn
        binding.addEntryDate.setOnClickListener {
            val entryPicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.date_picker_entry)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            entryPicker.addOnPositiveButtonClickListener {
                val entrySelectedDate = outputDateFormat.format(it)
                binding.addSelectedEntryDate.text = entrySelectedDate
            }
            entryPicker.show(parentFragmentManager, "Entry Date Picker")
        }
        // Estate's date of sale btn
        binding.addSaleDate.setOnClickListener {
            val salePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.date_picker_sale)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()
            salePicker.addOnPositiveButtonClickListener {
                val saleSelectedDate = outputDateFormat.format(it)
                binding.addSelectedSaleDate.text = saleSelectedDate
            }
            salePicker.show(parentFragmentManager, "Sale Date Picker")
        }
    }

    // Set up drop-down menus for estate's type x status
    private fun initDropDownMenus() {
        // Estate's type
        val types = resources.getStringArray(R.array.types)
        val typeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, types)
        binding.addType.setAdapter(typeAdapter)
        // Estate's status
        val status = resources.getStringArray(R.array.status)
        val statusAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, status)
        binding.addStatus.setAdapter(statusAdapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}