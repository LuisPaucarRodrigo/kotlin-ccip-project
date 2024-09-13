package com.hybrid.projectarea.view.projecthuawei

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
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentHistoryImagesProjectHuaweiBinding
import com.hybrid.projectarea.databinding.FragmentRegisterPhotoBinding
import com.hybrid.projectarea.databinding.PhotoCodeBinding
import com.hybrid.projectarea.model.Photo
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.view.preproject.AdapterRegisterPhoto
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryImagesProjectHuaweiFragment : Fragment() {
    private var _binding:FragmentHistoryImagesProjectHuaweiBinding? = null
    private val binding get() = _binding!!

    private lateinit var projectHuaweiCode_id: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectHuaweiCode_id = requireArguments().getString("code_id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryImagesProjectHuaweiBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestRegisterPhoto()
        binding.recyclerviewHistoryImagesProjectHuawei.swipe.setColorSchemeResources(R.color.azulccip,R.color.greenccip)
        binding.recyclerviewHistoryImagesProjectHuawei.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewHistoryImagesProjectHuawei.swipe.setOnRefreshListener {
            requestRegisterPhoto()
        }
    }

    private fun requestRegisterPhoto() {
        val arrayList = ArrayList<Photo>()
        binding.recyclerviewHistoryImagesProjectHuawei.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funHistoryImageProjectHuawei(token,projectHuaweiCode_id,object : AuthManager.inRegisterPhoto{
                    override fun onRegisterPhotoSuccess(response: List<Photo>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewHistoryImagesProjectHuawei.afterViewElement.isVisible = true
                        binding.recyclerviewHistoryImagesProjectHuawei.swipe.isRefreshing = false
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

                        binding.recyclerviewHistoryImagesProjectHuawei.recyclerview.adapter = adapter

                    }

                    override fun onRegisterPhotoFailed(errorMessage: String) {
                        binding.recyclerviewHistoryImagesProjectHuawei.swipe.isRefreshing = false
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