package com.hybrid.projectarea.view.checklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentDayCheckListBinding
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.utils.HideKeyboard
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.model.checklistDay
import com.hybrid.projectarea.view.DeleteTokenAndCloseSession
import com.hybrid.projectarea.view.manuals.ProcessManualsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DayCheckListFragment : Fragment() {
    private var _binding: FragmentDayCheckListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDayCheckListBinding.inflate(inflater, container, false)
        binding.scrollChecklist.checkListDay.apply{
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.azulccip))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        val optionsZone =
            arrayOf("Arequipa", "Chala", "Moquegua Pint", "Moquegua Pext", "Tacna", "MDD1", "MDD2")
        val optionsCheckDay = listOf("Bueno", "Regular", "Malo", "Solicitud")
        val adapterZone =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, optionsZone)
        binding.zone.adapter = adapterZone
        val spinnerCheckDay = listOf(
            binding.powerMeter,
            binding.ammeterClamp,
            binding.cuttingPliers,
            binding.nosePliers,
            binding.universalPliers,
            binding.tape,
            binding.cutter,
            binding.knobDriver,
            binding.screwdriverSet,
            binding.allenkeysSet,
            binding.thorScrewboard,
            binding.helmetFlashlight,
            binding.freanchKey,
            binding.pyrometer,
            binding.laptop,
            binding.consoleCables,
            binding.networkAdapter,
        )

        spinnerCheckDay.forEach { spinner ->
            spinner.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, optionsCheckDay
            )
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Checklist"
        binding.scrollChecklist.checkListEpps.setOnClickListener {
            openFragment(EppsCheckListFragment())
        }

        binding.scrollChecklist.checkListMobile.setOnClickListener {
            openFragment(MobileUnitChecklistFragment())
        }

        binding.scrollChecklist.checkListTools.setOnClickListener {
            openFragment(ToolsCheckListFragment())
        }

        binding.scrollChecklist.checkListHistory.setOnClickListener {
            openFragment(ChecklistHistoryFragment())
        }

        binding.send.buttonSend.setOnClickListener {
            val checkListDay = collectFormData()
            if (areAllFieldsFilled(checkListDay)) {
                HideKeyboard.hideKeyboard(binding.root)
                binding.send.buttonSend.isEnabled = false
                send(checkListDay)
            } else {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun send(checkListDay: checklistDay) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funCheckListDay(
                    token,
                    checkListDay,
                    object : AuthManager.inCheckListDay {
                        override fun onStoreCheckListDaySuccess() {
                            Alert.alertSuccess(requireContext(), layoutInflater)
                            dataCleaning()
                            binding.send.buttonSend.isEnabled = true
                        }

                        override fun onStoreCheckListDayNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@DayCheckListFragment)
                        }

                        override fun onStoreCheckListDayFailed(errorMessage: String) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                                .show()
                            binding.send.buttonSend.isEnabled = true
                        }

                    })
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Se produjo un error inesperado. Por favor inténtalo de nuevo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                binding.send.buttonSend.isEnabled = true
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, fragment)
        transaction.commit()
    }

    private fun collectFormData(): checklistDay {
        return checklistDay(
            personal2 = binding.personal2.text?.toString(),
            zone = binding.zone.selectedItem.toString(),
            powerMeter = binding.powerMeter.selectedItem.toString(),
            ammeterClamp = binding.ammeterClamp.selectedItem.toString(),
            cuttingPliers = binding.cuttingPliers.selectedItem.toString(),
            nosePliers = binding.nosePliers.selectedItem.toString(),
            universalPliers = binding.universalPliers.selectedItem.toString(),
            tape = binding.tape.selectedItem.toString(),
            cutter = binding.cutter.selectedItem.toString(),
            knobDriver = binding.knobDriver.selectedItem.toString(),
            screwdriverSet = binding.screwdriverSet.selectedItem.toString(),
            allenkeysSet = binding.allenkeysSet.selectedItem.toString(),
            thorScrewboard = binding.thorScrewboard.selectedItem.toString(),
            helmetFlashlight = binding.helmetFlashlight.selectedItem.toString(),
            freanchKey = binding.freanchKey.selectedItem.toString(),
            pyrometer = binding.pyrometer.selectedItem.toString(),
            laptop = binding.laptop.selectedItem.toString(),
            consoleCables = binding.consoleCables.selectedItem.toString(),
            networkAdapter = binding.networkAdapter.selectedItem.toString(),
            observations = binding.observations.text?.toString()
        )
    }

    private fun areAllFieldsFilled(formData: checklistDay): Boolean {
        return formData.zone.isNotEmpty() &&
                formData.powerMeter.isNotEmpty() &&
                formData.ammeterClamp.isNotEmpty() &&
                formData.cuttingPliers.isNotEmpty() &&
                formData.nosePliers.isNotEmpty() &&
                formData.universalPliers.isNotEmpty() &&
                formData.tape.isNotEmpty() &&
                formData.cutter.isNotEmpty() &&
                formData.knobDriver.isNotEmpty() &&
                formData.screwdriverSet.isNotEmpty() &&
                formData.allenkeysSet.isNotEmpty() &&
                formData.thorScrewboard.isNotEmpty() &&
                formData.helmetFlashlight.isNotEmpty() &&
                formData.freanchKey.isNotEmpty() &&
                formData.pyrometer.isNotEmpty() &&
                formData.laptop.isNotEmpty() &&
                formData.consoleCables.isNotEmpty() &&
                formData.networkAdapter.isNotEmpty()
    }

    private fun dataCleaning() {
        binding.personal2.text?.clear()
        binding.zone.setSelection(0)
        binding.powerMeter.setSelection(0)
        binding.ammeterClamp.setSelection(0)
        binding.cuttingPliers.setSelection(0)
        binding.nosePliers.setSelection(0)
        binding.universalPliers.setSelection(0)
        binding.tape.setSelection(0)
        binding.cutter.setSelection(0)
        binding.knobDriver.setSelection(0)
        binding.screwdriverSet.setSelection(0)
        binding.allenkeysSet.setSelection(0)
        binding.thorScrewboard.setSelection(0)
        binding.helmetFlashlight.setSelection(0)
        binding.freanchKey.setSelection(0)
        binding.pyrometer.setSelection(0)
        binding.laptop.setSelection(0)
        binding.consoleCables.setSelection(0)
        binding.networkAdapter.setSelection(0)
        binding.observations.text?.clear()
    }
}