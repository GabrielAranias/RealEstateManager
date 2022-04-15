package com.openclassrooms.realestatemanager.ui.fragment.sim

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.openclassrooms.realestatemanager.databinding.FragmentSearchBinding
import com.openclassrooms.realestatemanager.databinding.FragmentSimBinding

class SimFragment : Fragment() {

    private var _binding: FragmentSimBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimBinding.inflate(inflater, container, false)
        return binding.root
    }
}