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
import com.hybrid.projectarea.databinding.FragmentRectifiersBinding
import com.hybrid.projectarea.model.NameRectifiers
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RectifiersFragment : Fragment() {
    private var _binding: FragmentRectifiersBinding? = null
    private val binding get() = _binding!!
    private var idProjectHuawei: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        idProjectHuawei = requireArguments().getString("id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRectifiersBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDataApi()
    }

    private fun getDataApi() {
        val arrayList = ArrayList<NameRectifiers>()
        binding.recyclerviewRectifiers.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch {
            val token = TokenAuth.getToken(requireContext(),"token")
            val apiService = withContext(Dispatchers.IO){
                RetrofitClient.getClient(token).create(ApiService::class.java)
            }
            val authManager = AuthManager(apiService)
            authManager.funGetRectifiersProjectHuawei(token,idProjectHuawei!!,object :AuthManager.inGetRectifiersProjectHuawei{
                override fun onRectifiersProjectHuaweiSuccess(response: List<NameRectifiers>) {
                    binding.shimmerRectifiers.beforeViewElement.isVisible = false
                    binding.recyclerviewRectifiers.afterViewElement.isVisible = true
                    binding.recyclerviewRectifiers.swipe.isRefreshing = false
                    response.forEach { item ->
                        val element = NameRectifiers(item.id,item.brand)
                        arrayList.add(element)
                    }
//                    val conceptFragment = ImagesRectifiersFragment()
//                    val adapter = AdapterRectifiers(arrayList, object : AdapterRectifiers.OnItemClickListener {
//                        override fun onItemClick(position: Int) {
//                            val item = arrayList[position]
//                            val args = Bundle()
//                            args.putString("idRectifiers",item.id)
//                            conceptFragment.arguments = args
//
//                            val transition: FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
//                            transition.replace(R.id.contenedor, conceptFragment)
//                                .addToBackStack(null)
//                                .commit()
//                        }
//                    })
//                    binding.recyclerviewRectifiers.recyclerview.adapter = adapter
                }

                override fun onRectifiersProjectHuaweiFailed(errorMessage: String) {
                    Toast.makeText(requireContext(),errorMessage,Toast.LENGTH_LONG).show()
                }

            })
        }
    }
}