package com.hybrid.projectarea.view.project

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
import com.hybrid.projectarea.databinding.FragmentProjectBinding
import com.hybrid.projectarea.model.ProjectRecycler
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectFragment : Fragment() {
    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =  FragmentProjectBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestProject()
        binding.recyclerviewProject.swipe.setColorSchemeResources(R.color.azulccip,R.color.greenccip)
        binding.recyclerviewProject.swipe.setProgressBackgroundColorSchemeResource(R.color.white)

        binding.recyclerviewProject.swipe.setOnRefreshListener {
            requestProject()
        }
    }

    private fun requestProject() {
        val arrayList = ArrayList<ProjectRecycler>()
        binding.recyclerviewProject.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(),"token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.project(token,object : AuthManager.ProjectListener {
                    override fun onProjectSuccess(response: List<ProjectRecycler>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewProject.afterViewElement.isVisible = true
                        binding.recyclerviewProject.swipe.isRefreshing = false
                        response.forEach { item ->
                            val id = item.id
                            val code = item.code
                            val description = item.description
                            val element = ProjectRecycler(id,code,description)
                            arrayList.add(element)
                        }
                        val adapter = AdapterProject(arrayList, object : AdapterProject.OnItemClickListener {
                            override fun onItemClick(position: Int) {
                                val item = arrayList[position]
                                val args = Bundle()
                                args.putString("id",item.id)
                                val conceptFragment = ProjectShowFragment()
                                conceptFragment.arguments = args

                                val transition: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                                transition.replace(R.id.contenedor, conceptFragment)
                                    .addToBackStack(null)
                                transition.commit()
                            }
                        })
                        binding.recyclerviewProject.recyclerview.adapter = adapter

                    }
                    override fun onProjectFailed(error: String) {
                        binding.recyclerviewProject.swipe.isRefreshing = false
                        Toast.makeText(requireContext(),error,Toast.LENGTH_SHORT).show()
                    }
                })
            }catch (e: Exception){
                Toast.makeText(requireContext(),"error",Toast.LENGTH_SHORT).show()

            }
        }
    }
}