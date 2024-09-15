package com.hybrid.projectarea.view.preproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentCodePhotoBinding
import com.hybrid.projectarea.model.CodePhotoPreProject
import com.hybrid.projectarea.model.PreprojectTitle
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.view.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CodePhotoFragment : Fragment() {

    private var _binding:FragmentCodePhotoBinding? = null
    private val binding get() = _binding!!
    private var preproject_id = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preproject_id = requireArguments().getString("preproject_id").toString()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCodePhotoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestCode()
        binding.recyclerviewCodePhoto.swipe.setColorSchemeResources(R.color.azulccip,R.color.greenccip)
        binding.recyclerviewCodePhoto.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewCodePhoto.swipe.setOnRefreshListener {
            requestCode()
        }
    }

    private fun requestCode() {
        val arrayList = ArrayList<PreprojectTitle>()
        binding.recyclerviewCodePhoto.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.codephotopreproject(token,preproject_id,object : AuthManager.inCodePhotoPreProject{
                    override fun onCodePhotoPreProjectSuccess(response: List<PreprojectTitle>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewCodePhoto.afterViewElement.isVisible = true
                        binding.recyclerviewCodePhoto.swipe.isRefreshing = false
                        val adapter = AdapterPrepropjectTitle(response)
                        binding.recyclerviewCodePhoto.recyclerview.adapter = adapter
                    }

                    override fun onCodePhotoPreProjectNoAuthenticated() {
                        DeleteTokenAndCloseSession(this@CodePhotoFragment)
                    }

                    override fun onCodePhotoPreProjectFailed(errorMessage: String) {
                        binding.recyclerviewCodePhoto.swipe.isRefreshing = false
                        Toast.makeText(requireContext(),errorMessage, Toast.LENGTH_LONG).show()
                    }
                })
            }catch (e: Exception){
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Se produjo un error inesperado.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

}