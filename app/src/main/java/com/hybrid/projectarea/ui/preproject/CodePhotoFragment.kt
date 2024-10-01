package com.hybrid.projectarea.ui.preproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentCodePhotoBinding
import com.hybrid.projectarea.domain.model.PreprojectTitle
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CodePhotoFragment : Fragment() {

    private var _binding:FragmentCodePhotoBinding? = null
    private val binding get() = _binding!!
    private var preproject_id = ""

    private lateinit var codePhotoViewModel: CodePhotoViewModel

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

        codePhotoViewModel = ViewModelProvider(this).get(CodePhotoViewModel::class.java)
        requestCode()
        codePhotoViewModel.data.observe(viewLifecycleOwner){ success ->
            binding.shimmer.beforeViewElement.isVisible = false
            binding.recyclerviewCodePhoto.afterViewElement.isVisible = true
            binding.recyclerviewCodePhoto.swipe.isRefreshing = false
            val adapter = AdapterPreprojectTitle(success)
            binding.recyclerviewCodePhoto.recyclerview.adapter = adapter
        }

        codePhotoViewModel.error.observe(viewLifecycleOwner){ error ->
            lifecycleScope.launch(Dispatchers.Main) {
                binding.recyclerviewCodePhoto.swipe.isRefreshing = false
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
            }
        }

        binding.recyclerviewCodePhoto.swipe.setColorSchemeResources(R.color.azulccip,R.color.greenccip)
        binding.recyclerviewCodePhoto.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewCodePhoto.swipe.setOnRefreshListener {
            requestCode()
        }
    }

    private fun requestCode() {
        binding.recyclerviewCodePhoto.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                codePhotoViewModel.getCodePhoto(token, preproject_id)
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