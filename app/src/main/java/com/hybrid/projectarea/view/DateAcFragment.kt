package com.hybrid.projectarea.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentDateAcBinding
import com.hybrid.projectarea.model.FormDataACHuawei
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DateAcFragment : Fragment() {
    private var _binding:FragmentDateAcBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDateAcBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.send.buttonSend.setOnClickListener {
            val formDataACHuawei = collectFormData()
            if (areAllFieldsFilled(formDataACHuawei)) {
                sendDateAC(formDataACHuawei)
            } else {
                Toast.makeText(context, "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendDateAC(formData: FormDataACHuawei) {
        lifecycleScope.launch {
            val token = TokenAuth.getToken(requireContext())
            val apiService = withContext(Dispatchers.IO){
                RetrofitClient.getClient(token).create(ApiService::class.java)
            }
            val authManager = AuthManager(apiService)
            authManager.funFormDataACHuawei(token,formData,object: AuthManager.inFormDataACHuawei{
                override fun onStoreFormDataACHuaweiSuccess() {

                }

                override fun onStoreFormDataACHuaweiFailed() {

                }

            })
        }
    }

    private fun collectFormData(): FormDataACHuawei {
        return FormDataACHuawei(
            power = binding.addPower.text.toString(),
            concessionaire = binding.addConcessionaire.text.toString(),
            supply = binding.addSupply.text.toString(),
            type = binding.addType.text.toString(),
            caliber = binding.addCaliber.text.toString(),
            fuses = binding.addFuses.text.toString(),
            calibertg = binding.addCaliberTG.text.toString(),
            itm = binding.addITM.text.toString(),
            powere = binding.addPowerE.text.toString(),
            brand = binding.addBrand.text.toString(),
            tankCapacity = binding.addTankCapacity.text.toString(),
            typee = binding.addTypeE.text.toString(),
            tableTransfer = binding.addTableTransfer.text.toString(),
            capacity = binding.addCapacity.text.toString(),
            fijacion = binding.addFixation.text.toString(),
            typet = binding.addTypeT.text.toString(),
            section = binding.addSection.text.toString(),
            itmMajor = binding.addITMMajor.text.toString(),
            rs = binding.addRS.text.toString(),
            rt = binding.addRT.text.toString(),
            st = binding.addST.text.toString(),
            r = binding.addR.text.toString(),
            s = binding.addS.text.toString(),
            t = binding.addT.text.toString()
        )
    }

    private fun areAllFieldsFilled(formData: FormDataACHuawei): Boolean {
        return formData.power.isNotEmpty() && formData.concessionaire.isNotEmpty() &&
                formData.supply.isNotEmpty() && formData.type.isNotEmpty() &&
                formData.caliber.isNotEmpty() && formData.fuses.isNotEmpty() &&
                formData.calibertg.isNotEmpty() && formData.itm.isNotEmpty() &&
                formData.powere.isNotEmpty() && formData.brand.isNotEmpty() &&
                formData.tankCapacity.isNotEmpty() && formData.typee.isNotEmpty() &&
                formData.tableTransfer.isNotEmpty() && formData.capacity.isNotEmpty() &&
                formData.fijacion.isNotEmpty() && formData.typet.isNotEmpty() &&
                formData.section.isNotEmpty() && formData.itmMajor.isNotEmpty() &&
                formData.rs.isNotEmpty() && formData.rt.isNotEmpty() &&
                formData.st.isNotEmpty() && formData.r.isNotEmpty() &&
                formData.s.isNotEmpty() && formData.t.isNotEmpty()
    }


}