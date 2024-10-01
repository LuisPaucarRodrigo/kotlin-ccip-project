package com.hybrid.projectarea.ui.checklist.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hybrid.projectarea.R
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentChecklistHistoryBinding
import com.hybrid.projectarea.domain.model.ChecklistHistory
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChecklistHistoryFragment : Fragment() {
    private var _binding: FragmentChecklistHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var checklistHistoryViewModel: ChecklistHistoryViewModel
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
        checklistHistoryViewModel = ViewModelProvider(this)[ChecklistHistoryViewModel::class.java]
        requestChecklistHistory()
        binding.recyclerviewChecklistHistory.recyclerview.layoutManager = LinearLayoutManager(context)
        checklistHistoryViewModel.data.observe(viewLifecycleOwner){ success ->
            binding.shimmer.beforeViewElement.isVisible = false
            binding.recyclerviewChecklistHistory.afterViewElement.isVisible = true
            binding.recyclerviewChecklistHistory.swipe.isRefreshing = false
            val adapter = AdapterChecklistHistory(success)
            binding.recyclerviewChecklistHistory.recyclerview.adapter = adapter
        }

        checklistHistoryViewModel.error.observe(viewLifecycleOwner){ error ->
            binding.recyclerviewChecklistHistory.swipe.isRefreshing = false
            Toast.makeText(requireContext(),error,Toast.LENGTH_LONG).show()
        }

        binding.recyclerviewChecklistHistory.swipe.setColorSchemeResources(
            R.color.azulccip,
            R.color.greenccip
        )
        binding.recyclerviewChecklistHistory.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewChecklistHistory.swipe.setOnRefreshListener {
            requestChecklistHistory()
        }

        binding.scrollChecklist.checkListEpps.setOnClickListener {
            openFragment(R.id.action_HistoryCheckListFragment_to_EppsCheckListFragment)
        }

        binding.scrollChecklist.checkListMobile.setOnClickListener {
            openFragment(R.id.action_HistoryCheckListFragment_to_MobileCheckListFragment)
        }

        binding.scrollChecklist.checkListTools.setOnClickListener {
            openFragment(R.id.action_HistoryCheckListFragment_to_ToolsCheckListFragment)
        }

        binding.scrollChecklist.checkListDay.setOnClickListener {
            openFragment(R.id.action_HistoryCheckListFragment_to_DayCheckListFragment)
        }

    }

    private fun openFragment(id: Int) {
        findNavController().navigate(id)
    }

    private fun requestChecklistHistory() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                checklistHistoryViewModel.getChecklist(token)
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