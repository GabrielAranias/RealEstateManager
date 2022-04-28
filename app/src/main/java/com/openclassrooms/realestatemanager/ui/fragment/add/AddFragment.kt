package com.openclassrooms.realestatemanager.ui.fragment.add

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.MaterialDatePicker
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.data.model.Estate
import com.openclassrooms.realestatemanager.databinding.FragmentAddBinding
import com.openclassrooms.realestatemanager.ui.main.MainActivity
import com.openclassrooms.realestatemanager.ui.viewModel.EstateViewModel
import com.openclassrooms.realestatemanager.utils.Constants
import dev.ronnie.github.imagepicker.ImagePicker
import dev.ronnie.github.imagepicker.ImageResult
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var estateViewModel: EstateViewModel
    private val vicinity = ArrayList<String>()
    private val photoUris = ArrayList<Uri>()
    private val photoCaptions = ArrayList<String>()
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
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]
        imagePicker = ImagePicker(this)

        initDropDownMenus()
        initChipGroup()
        initDateBtn()
        initPhotoHandling()
        initFab()

        return binding.root
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

    // Set up chip group to select POIs
    private fun initChipGroup() {
        binding.addVicinityBtn.setOnClickListener {
            if (binding.addVicinity.toString().isNotEmpty()) {
                addChip(binding.addVicinity.text.toString())
                binding.addVicinity.setText("")
            }
        }
    }

    private fun addChip(text: String) {
        val chip = Chip(requireContext())
        chip.text = text
        chip.isCloseIconVisible = true
        binding.addChipGroup.addView(chip)
        vicinity.add(text)
        chip.setOnCloseIconClickListener {
            binding.addChipGroup.removeView(chip)
            vicinity.remove(text)
        }
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

    // Set up btn to pick photo in gallery or take picture w/ camera
    private fun initPhotoHandling() {
        // Init RecyclerView
        val recyclerView = binding.addPhotoList
        adapter = GridAdapter(photoUris, photoCaptions, requireContext())
        recyclerView.adapter = adapter
        // Camera btn
        binding.addPhotoCamera.setOnClickListener {
            imagePicker.takeFromCamera { imageResult ->
                imageCallback(imageResult)
            }
        }
        // Gallery btn
        binding.addPhotoGallery.setOnClickListener {
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

    // Set up fab to create new item w/ info
    private fun initFab() {
        binding.addFab.setOnClickListener {
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
                val estate = Estate(
                    0,
                    type = binding.addType.text.toString(),
                    district = binding.addDistrict.text.toString(),
                    price = Integer.parseInt(binding.addPrice.text.toString()),
                    description = binding.addDescription.text.toString(),
                    surface = Integer.parseInt("0" + binding.addSurface.text.toString()),
                    realtor = binding.addRealtor.text.toString(),
                    status = binding.addStatus.text.toString(),
                    nbRooms = Integer.parseInt("0" + binding.addRooms.text.toString()),
                    nbBedrooms = Integer.parseInt("0" + binding.addBedrooms.text.toString()),
                    nbBathrooms = Integer.parseInt("0" + binding.addBathrooms.text.toString()),
                    address = binding.addAddress.text.toString(),
                    entryDate = binding.addSelectedEntryDate.text.toString(),
                    saleDate = binding.addSelectedSaleDate.text.toString(),
                    vicinity,
                    uris,
                    photoCaptions
                )
                // Add data to db
                estateViewModel.addEstate(estate)
                // Send notification to user
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sendNotification()
                }
                // Navigate back
                findNavController().navigate(R.id.action_addFragment_to_listFragment)
            }
        }
    }

    private fun checkAllFields(): Boolean {
        binding.apply {
            // Type
            if (addType.text.toString().isEmpty()) {
                addType.error = getString(R.string.error_type)
                return false
            } else {
                addType.error = null
            }
            // Price
            if (addPrice.text.toString().isEmpty()) {
                addPrice.error = getString(R.string.error_price)
                return false
            } else {
                addPrice.error = null
            }
            // Address
            if (addAddress.text.toString().isEmpty()) {
                addAddress.error = getString(R.string.error_address)
                return false
            } else {
                addAddress.error = null
            }
            // District
            if (addDistrict.text.toString().isEmpty()) {
                addDistrict.error = getString(R.string.error_district)
                return false
            } else {
                addDistrict.error = null
            }
            // Status
            if (addStatus.text.toString() == resources.getStringArray(R.array.status)[1]
                && addSelectedSaleDate.text == getString(R.string.not_sold)
            ) {
                Toast.makeText(requireContext(), R.string.error_status, Toast.LENGTH_SHORT).show()
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendNotification() {
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}