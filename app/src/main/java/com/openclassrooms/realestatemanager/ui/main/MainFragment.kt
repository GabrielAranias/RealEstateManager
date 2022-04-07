package com.openclassrooms.realestatemanager.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.realestatemanager.databinding.FragmentMainBinding
import com.openclassrooms.realestatemanager.ui.EstateViewModel

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private val adapter = EstateListAdapter()
    private lateinit var estateViewModel: EstateViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        initRecyclerView()
        initViewModel()

        return binding.root
    }

    private fun initRecyclerView() {

        val recyclerView = binding.estateList
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
    }

    private fun initViewModel() {
        estateViewModel = ViewModelProvider(this)[EstateViewModel::class.java]
        estateViewModel.readAllData.observe(viewLifecycleOwner) { estate ->
            adapter.setData(estate)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}