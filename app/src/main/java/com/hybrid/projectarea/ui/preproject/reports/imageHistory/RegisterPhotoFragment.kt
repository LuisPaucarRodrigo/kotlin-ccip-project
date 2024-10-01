package com.hybrid.projectarea.ui.preproject.reports.imageHistory

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentRegisterPhotoBinding
import com.hybrid.projectarea.databinding.PhotoCodeBinding
import com.hybrid.projectarea.domain.model.Photo
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterPhotoFragment : Fragment() {

    private var _binding:FragmentRegisterPhotoBinding? = null
    private val binding get() = _binding!!

    private var code_id:String = ""
    private lateinit var imageHistoryViewModel: ImageHistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        code_id = requireArguments().getString("code_id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterPhotoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageHistoryViewModel = ViewModelProvider(this).get(ImageHistoryViewModel::class.java)
        requestRegisterPhoto()
        binding.recyclerviewRegisterPhoto.recyclerview.layoutManager = LinearLayoutManager(context)
        imageHistoryViewModel.data.observe(viewLifecycleOwner){ success ->
            binding.shimmer.beforeViewElement.isVisible = false
            binding.recyclerviewRegisterPhoto.afterViewElement.isVisible = true
            binding.recyclerviewRegisterPhoto.swipe.isRefreshing = false
            val builder = AlertDialog.Builder(requireActivity())
            val adapter = AdapterRegisterPhoto(success, object : AdapterRegisterPhoto.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val item = success[position]
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

        imageHistoryViewModel.error.observe(viewLifecycleOwner) {error ->
            Toast.makeText(requireContext(),error,Toast.LENGTH_LONG).show()
        }

        binding.recyclerviewRegisterPhoto.swipe.setColorSchemeResources(R.color.azulccip,R.color.greenccip)
        binding.recyclerviewRegisterPhoto.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewRegisterPhoto.swipe.setOnRefreshListener {
            requestRegisterPhoto()
        }
    }

    private fun requestRegisterPhoto() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                imageHistoryViewModel.getImagesHistory(token,code_id)
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