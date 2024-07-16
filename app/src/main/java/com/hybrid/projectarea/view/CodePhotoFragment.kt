package com.hybrid.projectarea.view

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
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodePhotoFragment : Fragment() {

    private var _binding:FragmentCodePhotoBinding? = null
    private val binding get() = _binding!!

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
        val arrayList = ArrayList<CodePhotoPreProject>()
        binding.recyclerviewCodePhoto.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.codephotopreproject(token,requireArguments().getString("id").toString(),object : AuthManager.inCodePhotoPreProject{
                    override fun onCodePhotoPreProjectSuccess(response: List<CodePhotoPreProject>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewCodePhoto.afterViewElement.isVisible = true
                        binding.recyclerviewCodePhoto.swipe.isRefreshing = false
                        response.forEach{ item ->
                            val element = CodePhotoPreProject(item.id?:"",item.code,item.status)
                            arrayList.add(element)
                        }
                        val conceptFragment = PreProjectEspecificFragment()
                        val adapter = AdapterCodePhotoPreProject(arrayList,object : AdapterCodePhotoPreProject.OnItemClickListener{
                            override fun onItemClick(position: Int) {
                                val item = arrayList[position]
                                if (item.status == "Aprobado"){
                                    Snackbar.make(binding.root,"El ${item.code} ya esta Aprobado",Snackbar.LENGTH_LONG).show()
                                } else {
                                    val args = Bundle()
                                    args.putString("id",item.id)

                                    conceptFragment.arguments = args

                                    val transition: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                                    transition.replace(R.id.contenedor, conceptFragment)
                                        .addToBackStack(null)
                                    transition.commit()
                                }
                            }
                        })
                        binding.recyclerviewCodePhoto.recyclerview.adapter = adapter
                    }

                    override fun onCodePhotoPreProjectFailed() {
                        binding.recyclerviewCodePhoto.swipe.isRefreshing = false
                        Toast.makeText(requireContext(),getString(R.string.check_connection), Toast.LENGTH_LONG).show()
                    }
                })
            }catch (e: Exception){
                Toast.makeText(requireContext(),"Error:$e",Toast.LENGTH_SHORT).show()
            }
        }
    }

}