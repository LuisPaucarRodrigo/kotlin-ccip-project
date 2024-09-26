package com.hybrid.projectarea.ui.preproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentPreProjectBinding
import com.hybrid.projectarea.domain.model.ElementPreProjectRecyclerView
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.BaseActivity
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreProjectFragment : Fragment() {

    private var _binding: FragmentPreProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPreProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as BaseActivity
        activity.supportActionBar?.title = "Ante Proyectos"
        activity.requestUser()

        requestPreProject()
        binding.recyclerviewPreproject.swipe.setColorSchemeResources(
            R.color.azulccip,
            R.color.greenccip
        )
        binding.recyclerviewPreproject.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewPreproject.swipe.setOnRefreshListener {
            requestPreProject()
        }
    }

    private fun requestPreProject() {
        binding.recyclerviewPreproject.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val userId = TokenAuth.getToken(requireContext(), "userId")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.preproject(token, userId, object : AuthManager.PreProjectListener {
                    override fun onPreProjectSuccess(response: List<ElementPreProjectRecyclerView>) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.shimmer.beforeViewElement.isVisible = false
                            binding.recyclerviewPreproject.afterViewElement.isVisible = true
                            binding.recyclerviewPreproject.swipe.isRefreshing = false
                            val adapter = AdapterPreProjectElement(
                                response,
                                object : AdapterPreProjectElement.OnItemClickListener {
                                    override fun onItemClick(position: Int) {
                                        val item = response[position]
                                        val args = Bundle()
                                        args.putString("preproject_id", item.id)
                                        findNavController().navigate(
                                            R.id.to_CodePhotoFragment,
                                            args
                                        )
                                    }
                                })
                            binding.recyclerviewPreproject.recyclerview.adapter = adapter
                        }
                    }

                    override fun onPreProjectNoAuthenticated() {
                        DeleteTokenAndCloseSession(this@PreProjectFragment)
                    }

                    override fun onPreProjectFailed(error: String) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            binding.recyclerviewPreproject.swipe.isRefreshing = false
                            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show()
                        }
                    }
                })
            } catch (e: Exception) {
                println("dada $e")
                withContext(Dispatchers.Main) {

                    Toast.makeText(
                        requireContext(),
                        "Se produjo un error inesperado dd: $e",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}