package com.atom.payments.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.atom.payments.R
import com.atom.payments.data.network.ApiServiceFactory
import com.atom.payments.data.repository.Repository
import com.atom.payments.databinding.FragmentPaymentBinding


class PaymentFragment : Fragment() {

    private lateinit var binding: FragmentPaymentBinding
    private lateinit var progressBar: ProgressBar
    private val sharedViewModel by lazy {
        val repository = Repository(ApiServiceFactory.apiService)
        ViewModelProvider(this, SharedViewModelFactory(repository)).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPaymentBinding.inflate(inflater, container, false)
        progressBar = binding.progressbar
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnOut.setOnClickListener {
            sharedViewModel.logout()
            goToPaymentFragment()
        }
        sharedViewModel.paymentsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SharedViewModel.PaymentsResult.Success -> {
                    val payments = result.payments
                    val adapter = PaymentAdapter(payments)
                    binding.rc.adapter = adapter
                    showLoading(false)
                }

                is SharedViewModel.PaymentsResult.Error -> {
                    val errorMessage = result.errorMessage

                    showLoading(false)
                }
            }
        }
        sharedViewModel.getPayments()
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun goToPaymentFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_paymentFragment_to_loginFragment)
    }
}