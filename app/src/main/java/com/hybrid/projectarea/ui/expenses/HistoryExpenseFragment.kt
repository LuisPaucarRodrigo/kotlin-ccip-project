package com.hybrid.projectarea.ui.expenses

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
import com.hybrid.projectarea.api.ApiService
import com.hybrid.projectarea.api.AuthManager
import com.hybrid.projectarea.databinding.FragmentHistoryExpenseBinding
import com.hybrid.projectarea.domain.model.ExpenseHistory
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import com.hybrid.projectarea.ui.DeleteTokenAndCloseSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryExpenseFragment : Fragment() {
    private var _binding: FragmentHistoryExpenseBinding? = null
    private val binding get() = _binding!!

    private lateinit var expenseHistoryViewModel: ExpenseHistoryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryExpenseBinding.inflate(inflater, container, false)
        binding.options.btnHistory.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.azulccip))
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        expenseHistoryViewModel = ViewModelProvider(this).get(ExpenseHistoryViewModel::class.java)
        requestHistoryExpense()

        expenseHistoryViewModel.data.observe(viewLifecycleOwner){ success ->
            binding.recyclerviewHistoryExpense.recyclerview.layoutManager = LinearLayoutManager(context)
            binding.shimmer.beforeViewElement.isVisible = false
            binding.recyclerviewHistoryExpense.afterViewElement.isVisible = true
            binding.recyclerviewHistoryExpense.swipe.isRefreshing = false
            val adapter = AdapterExpenseHistory(success)
            binding.recyclerviewHistoryExpense.recyclerview.adapter = adapter
        }

        expenseHistoryViewModel.error.observe(viewLifecycleOwner){ error ->
            Toast.makeText(requireContext(),error,Toast.LENGTH_LONG).show()
        }
        binding.recyclerviewHistoryExpense.swipe.setColorSchemeResources(
            R.color.azulccip,
            R.color.greenccip
        )
        binding.recyclerviewHistoryExpense.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewHistoryExpense.swipe.setOnRefreshListener {
            requestHistoryExpense()
        }

        binding.options.btnExpenses.setOnClickListener {
            openFragment(R.id.to_ExpensesFragment)
        }
    }

    private fun openFragment(id: Int) {
        findNavController().navigate(id)
    }

    private fun requestHistoryExpense() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                expenseHistoryViewModel.getExpenses(token)
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
}