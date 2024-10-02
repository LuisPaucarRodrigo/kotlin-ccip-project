package com.hybrid.projectarea.ui.projecthuawei

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentProjectHuaweiTitleCodeBinding
import com.hybrid.projectarea.domain.model.ProjectHuaweiTitle
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectHuaweiTitleCodeFragment : Fragment() {
    private var _binding:FragmentProjectHuaweiTitleCodeBinding? = null
    private val binding get() = _binding!!
    private var projectHuawei_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        projectHuawei_id = requireArguments().getString("projectHuawei_id")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectHuaweiTitleCodeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestCode()
        binding.recyclerviewProjectHuaweiTitleCode.swipe.setColorSchemeResources(R.color.azulccip,R.color.greenccip)
        binding.recyclerviewProjectHuaweiTitleCode.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewProjectHuaweiTitleCode.swipe.setOnRefreshListener {
            requestCode()
        }
    }

    private fun requestCode() {
        val arrayList = ArrayList<ProjectHuaweiTitle>()
        binding.recyclerviewProjectHuaweiTitleCode.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = RetrofitClient.getClient(token)
                val authManager = AuthManager(apiService)
                authManager.funProjectHuaweiTitleCode(projectHuawei_id!!,object : AuthManager.inProjectHuaweiTitleCode{
                    override fun onProjectHuaweiTitleCodeSuccess(response: List<ProjectHuaweiTitle>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewProjectHuaweiTitleCode.afterViewElement.isVisible = true
                        binding.recyclerviewProjectHuaweiTitleCode.swipe.isRefreshing = false
                        response.forEach{ item ->
                            val element = ProjectHuaweiTitle(item.id,item.description,item.huawei_project_codes)
                            arrayList.add(element)
                        }

                        val adapter = AdapterProjectHuaweiTitle(arrayList)
                        binding.recyclerviewProjectHuaweiTitleCode.recyclerview.adapter = adapter
                    }

                    override fun onProjectHuaweiTitleCodeNoAuthenticated() {
                        DeleteTokenAndCloseSession(this@ProjectHuaweiTitleCodeFragment)
                    }

                    override fun onProjectHuaweiTitleCodeFailed(errorMessage: String) {
                        binding.recyclerviewProjectHuaweiTitleCode.swipe.isRefreshing = false
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