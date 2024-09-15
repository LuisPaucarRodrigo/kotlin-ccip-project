package com.hybrid.projectarea.view.checklist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentChecklistHistoryBinding
import com.hybrid.projectarea.model.ChecklistHistory
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.view.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChecklistHistoryFragment : Fragment() {
    private var _binding: FragmentChecklistHistoryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChecklistHistoryBinding.inflate(inflater, container, false)
        binding.scrollChecklist.checkListHistory.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.azulccip))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestChecklistHistory()

        binding.recyclerviewChecklistHistory.swipe.setColorSchemeResources(
            R.color.azulccip,
            R.color.greenccip
        )
        binding.recyclerviewChecklistHistory.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewChecklistHistory.swipe.setOnRefreshListener {
            requestChecklistHistory()
        }

        binding.scrollChecklist.checkListEpps.setOnClickListener {
            openFragment(EppsCheckListFragment())
        }

        binding.scrollChecklist.checkListMobile.setOnClickListener {
            openFragment(MobileUnitChecklistFragment())
        }

        binding.scrollChecklist.checkListTools.setOnClickListener {
            openFragment(ToolsCheckListFragment())
        }

        binding.scrollChecklist.checkListDay.setOnClickListener {
            openFragment(DayCheckListFragment())
        }

    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, fragment)
        transaction.commit()
    }

    private fun requestChecklistHistory() {
        val arrayList = ArrayList<ChecklistHistory>()
        binding.recyclerviewChecklistHistory.recyclerview.layoutManager =
            LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funCheckListHistory(token, object : AuthManager.inCheckListHistory {
                    override fun onStoreCheckListHistorySuccess(response: List<ChecklistHistory>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewChecklistHistory.afterViewElement.isVisible = true
                        binding.recyclerviewChecklistHistory.swipe.isRefreshing = false
                        response.forEach { item ->
                            val element = ChecklistHistory(item.type, item.created_at)
                            arrayList.add(element)
                        }
                        val adapter = AdapterChecklistHistory(arrayList)
                        binding.recyclerviewChecklistHistory.recyclerview.adapter = adapter
                    }

                    override fun onStoreCheckListHistoryNoAuthenticated() {
                        DeleteTokenAndCloseSession(this@ChecklistHistoryFragment)
                    }

                    override fun onStoreCheckListHistoryFailed(errorMessage: String) {
                        binding.recyclerviewChecklistHistory.swipe.isRefreshing = false
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    }
                })
            } catch (e: Exception) {
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