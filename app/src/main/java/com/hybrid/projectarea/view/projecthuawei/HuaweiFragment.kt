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
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentHuaweiBinding
import com.hybrid.projectarea.model.ProjectHuawei
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.view.projecthuawei.AdapterProjectHuawei
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HuaweiFragment : Fragment() {
    private var _binding:FragmentHuaweiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHuaweiBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataApi()

        binding.addProjectHuawei.setOnClickListener {
            val transaction: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.contenedor, StoreProjectsHuaweiFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    private fun getDataApi() {
        val arrayList = ArrayList<ProjectHuawei>()
        binding.recyclerviewHuawei.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            val token = TokenAuth.getToken(requireContext(),"token")
            val apiService = withContext(Dispatchers.IO){
                RetrofitClient.getClient(token).create(ApiService::class.java)
            }
            val authManager = AuthManager(apiService)
            authManager.funGetProjectHuawei(token, object: AuthManager.inGetProjectHuawei{
                override fun onProjectHuaweiSuccess(response: List<ProjectHuawei>) {
                    binding.shimmer.beforeViewElement.isVisible = false
                    binding.recyclerviewHuawei.afterViewElement.isVisible = true
                    binding.recyclerviewHuawei.swipe.isRefreshing = false
                    response.forEach { item ->
                        val element = ProjectHuawei(item.id,item.site,item.elaborated,item.code,item.name,item.address,item.reference,item.access)
                        arrayList.add(element)
                    }

                    val conceptFragment = RectifiersFragment()
                    val adapter = AdapterProjectHuawei(arrayList, object : AdapterProjectHuawei.OnItemClickListener {
                        override fun onItemClick(position: Int) {
                            val item = arrayList[position]
                            val args = Bundle()
                            args.putString("id",item.id)
                            conceptFragment.arguments = args

                            val transition: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                            transition.replace(R.id.contenedor, conceptFragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    })
                    binding.recyclerviewHuawei.recyclerview.adapter = adapter
                }

                override fun onProjectHuaweiFailed(errorMessage: String) {
                    Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_LONG).show()
                }
            })
        }

    }


}