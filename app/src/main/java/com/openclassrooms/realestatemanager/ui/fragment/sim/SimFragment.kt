package com.openclassrooms.realestatemanager.ui.fragment.sim

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.databinding.FragmentSimBinding
import com.openclassrooms.realestatemanager.utils.Constants
import kotlin.math.pow

class SimFragment : Fragment() {

    private var _binding: FragmentSimBinding? = null
    private val binding get() = _binding!!
    private var currentLoanAmount = 0.00
    private var currentInterestRate = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSimBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) {
            currentLoanAmount = 0.00
            currentInterestRate = 5
        } else {
            currentLoanAmount = savedInstanceState.getDouble(Constants.LOAN_AMOUNT)
            currentInterestRate = savedInstanceState.getInt(Constants.INTEREST_RATE)
        }
        binding.simLoanAmountEt.addTextChangedListener(loanAmountEditTextWatcher)
        binding.simInterestRateSb.setOnSeekBarChangeListener(interestRateSeekBarListener)
    }

    private fun updateValues() {
        val yearFiveEMI = calculateEMI(5)
        val yearFiveTotal = yearFiveEMI * 5.00 * 12.00
        binding.simTotalYear5Et.setText(String.format("%.02f", yearFiveTotal))
        binding.simEmiYear5Et.setText(String.format("%.02f", yearFiveEMI))

        val yearTenEMI = calculateEMI(10)
        val yearTenTotal = yearTenEMI * 10.00 * 12.00
        binding.simTotalYear10Et.setText(String.format("%.02f", yearTenTotal))
        binding.simEmiYear10Et.setText(String.format("%.02f", yearTenEMI))

        val yearFifteenEMI = calculateEMI(15)
        val yearFifteenTotal = yearFifteenEMI * 15.00 * 12.00
        binding.simTotalYear15Et.setText(String.format("%.02f", yearFifteenTotal))
        binding.simEmiYear15Et.setText(String.format("%.02f", yearFifteenEMI))

        val yearTwentyEMI = calculateEMI(20)
        val yearTwentyTotal = yearTwentyEMI * 20.00 * 12.00
        binding.simTotalYear20Et.setText(String.format("%.02f", yearTwentyTotal))
        binding.simEmiYear20Et.setText(String.format("%.02f", yearTwentyEMI))

        val yearTwentyFiveEMI = calculateEMI(25)
        val yearTwentyFiveTotal = yearTwentyFiveEMI * 25.00 * 12.00
        binding.simTotalYear25Et.setText(String.format("%.02f", yearTwentyFiveTotal))
        binding.simEmiYear25Et.setText(String.format("%.02f", yearTwentyFiveEMI))

        val yearThirtyEMI = calculateEMI(30)
        val yearThirtyTotal = yearThirtyEMI * 30.00 * 12.00
        binding.simTotalYear30Et.setText(String.format("%.02f", yearThirtyTotal))
        binding.simEmiYear30Et.setText(String.format("%.02f", yearThirtyEMI))
    }

    private fun calculateEMI(years: Int): Double {
        if (currentInterestRate == 0) {
            return 0.00
        }
        val r = currentInterestRate / 1200.00
        val n = years * 12
        val power = ((1.00 + r)).pow(n)
        return (currentLoanAmount * r * power) / (power - 1.00)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(Constants.LOAN_AMOUNT, currentLoanAmount)
        outState.putInt(Constants.INTEREST_RATE, currentInterestRate)
    }

    private val interestRateSeekBarListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

            currentInterestRate = progress
            binding.simInterestPercent.text =
                resources.getString(R.string.sim_percent, currentInterestRate.toString())
            updateValues()
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }
    private val loanAmountEditTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            currentLoanAmount = try {
                text.toString().toDouble()
            } catch (e: NumberFormatException) {
                0.00
            }

            updateValues()
        }

        override fun afterTextChanged(editable: Editable?) {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}