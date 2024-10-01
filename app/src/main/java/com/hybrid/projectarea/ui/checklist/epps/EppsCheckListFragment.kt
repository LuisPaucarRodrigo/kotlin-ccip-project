package com.hybrid.projectarea.ui.checklist.epps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentEppsCheckListBinding
import com.hybrid.projectarea.domain.model.checklistEpps
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.utils.HideKeyboard
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EppsCheckListFragment : Fragment() {
    private var _binding: FragmentEppsCheckListBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEppsCheckListBinding.inflate(inflater, container, false)
        binding.scrollChecklist.checkListEpps.apply{
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.azulccip))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        val optionsEpps = listOf("Bueno", "Regular", "Solicitud")
        val spinnerEpps = listOf(
            binding.helmet,
            binding.chinStrap,
            binding.windbreaker,
            binding.vest,
            binding.safetyShoes,
            binding.tshirtLs,
            binding.shirtLs,
            binding.jeans,
            binding.coveralls,
            binding.jacket,
            binding.darkGlasses,
            binding.clearGlasses,
            binding.overglasses,
            binding.dustMask,
            binding.earplugs,
            binding.latexOilGloves,
            binding.nitrileOilGloves,
            binding.splitLeatherGloves,
            binding.precisionGloves,
            binding.cutResistantGloves,
            binding.doubleLanyard,
            binding.harness,
            binding.positioningLanyard,
            binding.carabiners,
            binding.ascenders,
            binding.ascenders,
            binding.sunscreen,
            binding.ccip,
            binding.claro,
            binding.vericom,
        )

        spinnerEpps.forEach { spinner ->
            spinner.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, optionsEpps
            )
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.scrollChecklist.checkListDay.setOnClickListener {
            openFragment(R.id.action_EppsCheckListFragment_to_DayCheckListFragment)
        }

        binding.scrollChecklist.checkListMobile.setOnClickListener {
            openFragment(R.id.action_EppsCheckListFragment_to_MobileCheckListFragment)
        }

        binding.scrollChecklist.checkListTools.setOnClickListener {
            openFragment(R.id.action_EppsCheckListFragment_to_ToolsCheckListFragment)
        }

        binding.scrollChecklist.checkListHistory.setOnClickListener {
            openFragment(R.id.action_EppsCheckListFragment_to_HistoryCheckListFragment)
        }

        binding.send.buttonSend.setOnClickListener {
            val checkListEpps = collectFormData()
            if (areAllFieldsFilled(checkListEpps)) {
                HideKeyboard.hideKeyboard(binding.root)
                binding.send.buttonSend.isEnabled = false
                send(checkListEpps)
            } else {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun send(checkListEpps: checklistEpps) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token)
                val authManager = AuthManager(apiService)
                authManager.funCheckListEpps(
                    token,
                    checkListEpps,
                    object : AuthManager.inCheckListEpps {
                        override fun onStoreCheckListEppsSuccess() {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Alert.alertSuccess(requireContext(), layoutInflater)
                                dataCleaning()
                                binding.send.buttonSend.isEnabled = true
                            }
                        }

                        override fun onStoreCheckListEppsNoAuthenticated() {
                            DeleteTokenAndCloseSession(this@EppsCheckListFragment)
                        }

                        override fun onStoreCheckListEppsFailed(errorMessage: String) {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                                    .show()
                                binding.send.buttonSend.isEnabled = true
                            }
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

    private fun openFragment(id: Int) {
        findNavController().navigate(id)
    }

    private fun collectFormData(): checklistEpps {
        return checklistEpps(
            helmet = binding.helmet.selectedItem.toString(),
            chinStrap = binding.chinStrap.selectedItem.toString(),
            windbreaker = binding.windbreaker.selectedItem.toString(),
            vest = binding.vest.selectedItem.toString(),
            safetyShoes = binding.safetyShoes.selectedItem.toString(),
            tshirtLs = binding.tshirtLs.selectedItem.toString(),
            shirtLs = binding.shirtLs.selectedItem.toString(),
            jeans = binding.jeans.selectedItem.toString(),
            coveralls = binding.coveralls.selectedItem.toString(),
            jacket = binding.jacket.selectedItem.toString(),
            darkGlasses = binding.darkGlasses.selectedItem.toString(),
            clearGlasses = binding.clearGlasses.selectedItem.toString(),
            overglasses = binding.overglasses.selectedItem.toString(),
            dustMask = binding.dustMask.selectedItem.toString(),
            earplugs = binding.earplugs.selectedItem.toString(),
            latexOilGloves = binding.latexOilGloves.selectedItem.toString(),
            nitrileOilGloves = binding.nitrileOilGloves.selectedItem.toString(),
            splitLeatherGloves = binding.splitLeatherGloves.selectedItem.toString(),
            precisionGloves = binding.precisionGloves.selectedItem.toString(),
            cutResistantGloves = binding.cutResistantGloves.selectedItem.toString(),
            doubleLanyard = binding.doubleLanyard.selectedItem.toString(),
            harness = binding.harness.selectedItem.toString(),
            positioningLanyard = binding.positioningLanyard.selectedItem.toString(),
            carabiners = binding.carabiners.selectedItem.toString(),
            ascenders = binding.ascenders.selectedItem.toString(),
            sunscreen = binding.sunscreen.selectedItem.toString(),
            ccip = binding.ccip.selectedItem.toString(),
            claro = binding.claro.selectedItem.toString(),
            vericom = binding.vericom.selectedItem.toString()
        )
    }

    private fun areAllFieldsFilled(formData: checklistEpps): Boolean {
        return formData.helmet.isNotEmpty() &&
                formData.chinStrap.isNotEmpty() &&
                formData.windbreaker.isNotEmpty() &&
                formData.vest.isNotEmpty() &&
                formData.safetyShoes.isNotEmpty() &&
                formData.tshirtLs.isNotEmpty() &&
                formData.shirtLs.isNotEmpty() &&
                formData.jeans.isNotEmpty() &&
                formData.coveralls.isNotEmpty() &&
                formData.jacket.isNotEmpty() &&
                formData.darkGlasses.isNotEmpty() &&
                formData.clearGlasses.isNotEmpty() &&
                formData.overglasses.isNotEmpty() &&
                formData.dustMask.isNotEmpty() &&
                formData.earplugs.isNotEmpty() &&
                formData.latexOilGloves.isNotEmpty() &&
                formData.nitrileOilGloves.isNotEmpty() &&
                formData.splitLeatherGloves.isNotEmpty() &&
                formData.precisionGloves.isNotEmpty() &&
                formData.cutResistantGloves.isNotEmpty() &&
                formData.doubleLanyard.isNotEmpty() &&
                formData.harness.isNotEmpty() &&
                formData.positioningLanyard.isNotEmpty() &&
                formData.carabiners.isNotEmpty() &&
                formData.ascenders.isNotEmpty() &&
                formData.sunscreen.isNotEmpty() &&
                formData.ccip.isNotEmpty() &&
                formData.claro.isNotEmpty() &&
                formData.vericom.isNotEmpty()
    }

    private fun dataCleaning() {
        binding.helmet.setSelection(0)
        binding.chinStrap.setSelection(0)
        binding.windbreaker.setSelection(0)
        binding.vest.setSelection(0)
        binding.safetyShoes.setSelection(0)
        binding.tshirtLs.setSelection(0)
        binding.shirtLs.setSelection(0)
        binding.jeans.setSelection(0)
        binding.coveralls.setSelection(0)
        binding.jacket.setSelection(0)
        binding.darkGlasses.setSelection(0)
        binding.clearGlasses.setSelection(0)
        binding.overglasses.setSelection(0)
        binding.dustMask.setSelection(0)
        binding.earplugs.setSelection(0)
        binding.latexOilGloves.setSelection(0)
        binding.nitrileOilGloves.setSelection(0)
        binding.splitLeatherGloves.setSelection(0)
        binding.precisionGloves.setSelection(0)
        binding.cutResistantGloves.setSelection(0)
        binding.doubleLanyard.setSelection(0)
        binding.harness.setSelection(0)
        binding.positioningLanyard.setSelection(0)
        binding.carabiners.setSelection(0)
        binding.ascenders.setSelection(0)
        binding.ascenders.setSelection(0)
        binding.sunscreen.setSelection(0)
        binding.ccip.setSelection(0)
        binding.claro.setSelection(0)
        binding.vericom.setSelection(0)
    }
}