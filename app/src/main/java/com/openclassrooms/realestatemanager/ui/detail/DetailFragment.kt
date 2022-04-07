package com.openclassrooms.realestatemanager.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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

        binding.detailType.text = args.currentEstate.type
        binding.detailDistrict.text = args.currentEstate.district
        binding.detailPrice.text = args.currentEstate.price.toString()

        binding.detailFab.setOnClickListener {
            val currentEstate = Estate(
                args.currentEstate.id,
                args.currentEstate.type,
                args.currentEstate.district,
                args.currentEstate.price
            )
            val action =
                DetailFragmentDirections.actionDetailFragmentToUpdateFragment(currentEstate)
            findNavController().navigate(action)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}