package com.hybrid.projectarea.ui.preproject.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hybrid.projectarea.R
import com.hybrid.projectarea.databinding.FragmentPreProjectBinding
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreProjectFragment : Fragment() {

    private var _binding: FragmentPreProjectBinding? = null
    private val binding get() = _binding!!
    private lateinit var preProjectViewModel: PreProjectViewModel
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
        preProjectViewModel = ViewModelProvider(this).get(PreProjectViewModel::class.java)
        requestPreProject()
        binding.recyclerviewPreproject.recyclerview.layoutManager = LinearLayoutManager(context)
        preProjectViewModel.data.observe(viewLifecycleOwner){ success ->
            binding.shimmer.beforeViewElement.isVisible = false
            binding.recyclerviewPreproject.afterViewElement.isVisible = true
            binding.recyclerviewPreproject.swipe.isRefreshing = false
            val adapter =  AdapterPreProjectElement(success,object : AdapterPreProjectElement.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val args = Bundle().apply {
                        putString("preproject_id", success[position].id)
                    }
                    findNavController().navigate(R.id.to_CodePhotoFragment,args)
                }
            })
            binding.recyclerviewPreproject.recyclerview.adapter = adapter
        }
        preProjectViewModel.error.observe(viewLifecycleOwner){ error ->
            println("dasdas"+error)
            Toast.makeText(requireContext(),"dasdas"+error,Toast.LENGTH_LONG).show()
        }

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
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val userId = TokenAuth.getToken(requireContext(), "userId")
                preProjectViewModel.getPreproject(token, userId)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}