package com.hybrid.projectarea.view

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentStoreProjectsHuaweiBinding
import com.hybrid.projectarea.databinding.SuccessfulRequestBinding
import com.hybrid.projectarea.utils.Alert
import com.hybrid.projectarea.model.FormStoreProjectHuawei
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoreProjectsHuaweiFragment : Fragment() {

    private var _binding:FragmentStoreProjectsHuaweiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreProjectsHuaweiBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.send.buttonSend.setOnClickListener {
            val formData = getFormData()
            if (areAllFieldsFilled(formData)) {
                sendFormData(formData)
            } else {
                Toast.makeText(requireContext(),"Complete todo los Campos",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendFormData(formData: FormStoreProjectHuawei) {
        println(formData)
        lifecycleScope.launch {
            val token = TokenAuth.getToken(requireContext(),"token")
            val apiService = withContext(Dispatchers.IO){
                RetrofitClient.getClient(token).create(ApiService::class.java)
            }
            val authManager = AuthManager(apiService)
            authManager.funStorePtojectHuawei(token,formData,object: AuthManager.inStoreProjectHuawei{
                override fun onStoreProjectHuaweiSuccess() {
                    Alert.alertSuccess(requireContext(),layoutInflater)
                }

                override fun onStoreProjectHuaweiFailed(errorMessage: String) {
                    Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_LONG).show()
                }

            })
        }

    }

    private fun getFormData(): FormStoreProjectHuawei {
        return FormStoreProjectHuawei(
            site = binding.addSite.text.toString(),
            elaborated = binding.addElaborated.text.toString(),
            code = binding.addCode.text.toString(),
            name = binding.addName.text.toString(),
            address = binding.addAddress.text.toString(),
            reference = binding.addReference.text.toString(),
            access = binding.addAccess.text.toString()
        )
    }

    private fun areAllFieldsFilled(formData: FormStoreProjectHuawei): Boolean {
        return with(formData) {
            site.isNotEmpty() && elaborated.isNotEmpty() && code.isNotEmpty() && name.isNotEmpty() &&
                    address.isNotEmpty() && reference.isNotEmpty() && access.isNotEmpty()
        }
    }

    private fun alertSuccess() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialogBinding = SuccessfulRequestBinding.inflate(layoutInflater)
        val dialogView = alertDialogBinding.root
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        },1500)
    }


}