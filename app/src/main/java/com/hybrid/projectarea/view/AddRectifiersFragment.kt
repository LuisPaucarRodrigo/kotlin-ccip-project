package com.hybrid.projectarea.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hybrid.projectarea.R
import com.hybrid.projectarea.databinding.FragmentAddRectifiersBinding

class AddRectifiersFragment : Fragment() {
    private var _binding:FragmentAddRectifiersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddRectifiersBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val voltage = binding.addVoltage.text.toString()
        val current = binding.addCurrent.text.toString()
        val mr = binding.addMR.text.toString()
        val powerMR = binding.addPowerMR.text.toString()
        val spaceAvailable = binding.addSpaceAvailable.text.toString()
        val mr2 = binding.addMR2.text.toString()
        val powerMr2 = binding.addPowerMR2.text.toString()
        val rechargeBatteries = binding.addRechargeBatteries.text.toString()
        val bb = binding.addBB.text.toString()
        val capacity = binding.addCapacity.text.toString()
        val ageFactor = binding.addAgeFactor.text.toString()
        val bb2 = binding.addBB2.text.toString()
        val capacity2 = binding.addCapacity2.text.toString()
        val ageFactor2 = binding.addAgeFactor2.text.toString()
        val incrementPot = binding.addIncrementPOT.text.toString()
        val hoursAutonomy = binding.addHoursAutonomy.text.toString()
        val incrementBb = binding.addIncrementBB.text.toString()
        val addNewCapacityBb = binding.addNewCapacityBB.text.toString()
        val addNewFactorBb = binding.addNewFactorBB.text.toString()
    }
}