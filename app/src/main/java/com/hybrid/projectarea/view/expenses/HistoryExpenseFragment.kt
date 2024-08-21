package com.hybrid.projectarea.view.expenses

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
import com.hybrid.projectarea.databinding.FragmentHistoryExpenseBinding
import com.hybrid.projectarea.model.ExpenseHistory
import com.hybrid.projectarea.model.RetrofitClient
import com.hybrid.projectarea.model.TokenAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryExpenseFragment : Fragment() {
    private var _binding: FragmentHistoryExpenseBinding? = null
    private val binding get() = _binding!!
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

        requestHistoryExpense()
        binding.recyclerviewHistoryExpense.swipe.setColorSchemeResources(
            R.color.azulccip,
            R.color.greenccip
        )
        binding.recyclerviewHistoryExpense.swipe.setProgressBackgroundColorSchemeResource(R.color.white)
        binding.recyclerviewHistoryExpense.swipe.setOnRefreshListener {
            requestHistoryExpense()
        }

        binding.options.btnExpenses.setOnClickListener {
            openFragment(ExpensesFragment())
        }
    }

    private fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction =
            requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.contenedor, fragment)
        transaction.commit()
    }

    private fun requestHistoryExpense() {
        val arrayList = ArrayList<ExpenseHistory>()
        binding.recyclerviewHistoryExpense.recyclerview.layoutManager = LinearLayoutManager(context)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = TokenAuth.getToken(requireContext(), "token")
                val apiService = RetrofitClient.getClient(token).create(ApiService::class.java)
                val authManager = AuthManager(apiService)
                authManager.funExpenseHistory(token, object : AuthManager.inExpenseHistory {
                    override fun onExpenseHistorySuccess(response: List<ExpenseHistory>) {
                        binding.shimmer.beforeViewElement.isVisible = false
                        binding.recyclerviewHistoryExpense.afterViewElement.isVisible = true
                        binding.recyclerviewHistoryExpense.swipe.isRefreshing = false
                        response.forEach { item ->
                            val element = ExpenseHistory(
                                item.zone,
                                item.expense_type,
                                item.amount
                            )
                            arrayList.add(element)
                        }
                        val adapter = AdapterExpenseHistory(arrayList)
                        binding.recyclerviewHistoryExpense.recyclerview.adapter = adapter
                    }

                    override fun onExpenseHistoryFailed(errorMessage: String) {
                        binding.recyclerviewHistoryExpense.swipe.isRefreshing = false
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                    }
                })
            } catch (e: Exception) {
                // Manejar errores
            }
        }
    }
}