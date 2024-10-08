package com.hybrid.projectarea.view.preproject

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentRegisterPhotoBinding
import com.hybrid.projectarea.databinding.PhotoCodeBinding
import com.hybrid.projectarea.model.Photo
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.view.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterPhotoFragment : Fragment() {

    private var _binding:FragmentRegisterPhotoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterPhotoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestRegisterPhoto()
        binding.recyclerviewRegisterPhoto.swipe.setColorSchemeResources(R.color.azulccip,R.color.greenccip)
        binding.recyclerviewRegisterPhoto.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewRegisterPhoto.swipe.setOnRefreshListener {
            requestRegisterPhoto()
        }
    }

    private fun requestRegisterPhoto() {
        binding.recyclerviewRegisterPhoto.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funRegisterPhoto(token,requireArguments().getString("id").toString(),object : AuthManager.inRegisterPhoto{
                    override fun onRegisterPhotoSuccess(response: List<Photo>) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.shimmer.beforeViewElement.isVisible = false
                            binding.recyclerviewRegisterPhoto.afterViewElement.isVisible = true
                            binding.recyclerviewRegisterPhoto.swipe.isRefreshing = false
                            val builder = AlertDialog.Builder(requireActivity())
                            val adapter = AdapterRegisterPhoto(response, object : AdapterRegisterPhoto.OnItemClickListener {
                                override fun onItemClick(position: Int) {
                                    val item = response[position]
                                    val alertDialogBinding = PhotoCodeBinding.inflate(layoutInflater)
                                    val dialogView = alertDialogBinding.root
                                    builder.setView(dialogView)
                                    val dialog = builder.create()
                                    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                    dialog.show()
                                    Glide.with(requireContext())
                                        .load(item.image)
                                        .placeholder(R.drawable.baseline_downloading_24)
                                        .error(R.drawable.baseline_error_24)
                                        .into(alertDialogBinding.photo)
                                    if (item.state == "0"){
                                        alertDialogBinding.observation.apply {
                                            isVisible = true
                                            text = item.observation ?: ""
                                        }
                                    }
                                }
                            })

                            binding.recyclerviewRegisterPhoto.recyclerview.adapter = adapter
                        }
                    }

                    override fun onRegisterPhotoNoAuthenticated() {
                        DeleteTokenAndCloseSession(this@RegisterPhotoFragment)
                    }

                    override fun onRegisterPhotoFailed(errorMessage: String) {
                        binding.recyclerviewRegisterPhoto.swipe.isRefreshing = false
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