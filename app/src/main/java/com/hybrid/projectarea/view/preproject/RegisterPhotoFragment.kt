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
import com.google.android.material.snackbar.Snackbar
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentRegisterPhotoBinding
import com.hybrid.projectarea.databinding.PhotoCodeBinding
import com.hybrid.projectarea.model.Photo
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.squareup.picasso.Picasso
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
        val arrayList = ArrayList<Photo>()
        binding.recyclerviewRegisterPhoto.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funRegisterPhoto(token,requireArguments().getString("id").toString(),object : AuthManager.inRegisterPhoto{
                    override fun onRegisterPhotoSuccess(response: List<Photo>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewRegisterPhoto.afterViewElement.isVisible = true
                        binding.recyclerviewRegisterPhoto.swipe.isRefreshing = false
                        response.forEach { item ->
                            val element = Photo(item.image,item.observation?:"",item.state?:"")
                            arrayList.add(element)
                        }
                        val builder = AlertDialog.Builder(requireActivity())
                        val adapter = AdapterRegisterPhoto(arrayList,object : AdapterRegisterPhoto.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                val item = arrayList[position]
                                val alertDialogBinding = PhotoCodeBinding.inflate(layoutInflater)
                                val dialogView = alertDialogBinding.root
                                builder.setView(dialogView)

                                val dialog = builder.create()
                                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                                dialog.show()
                                Picasso.get().load(item.image).into(alertDialogBinding.photo.photoPreview)
                                alertDialogBinding.observation.text = item.observation?:""
                            }
                        })

                        binding.recyclerviewRegisterPhoto.recyclerview.adapter = adapter

                    }

                    override fun onRegisterPhotoFailed() {
                        binding.recyclerviewRegisterPhoto.swipe.isRefreshing = false
                        Toast.makeText(requireContext(),getString(R.string.check_connection), Toast.LENGTH_LONG).show()
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