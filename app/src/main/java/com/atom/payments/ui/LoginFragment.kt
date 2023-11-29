package com.atom.payments.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.atom.payments.R
import com.atom.payments.data.network.ApiServiceFactory.apiService
import com.atom.payments.data.network.UserManager
import com.atom.payments.data.repository.Repository
import com.atom.payments.databinding.FragmentLoginBinding


class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var _binding: FragmentLoginBinding
    private val binding get() = _binding

    private val sharedViewModel by lazy {
        val repository = Repository(apiService)
        ViewModelProvider(this, SharedViewModelFactory(repository)).get(SharedViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.editLogin.addTextChangedListener {
            sharedViewModel.validateLoginInput(it.toString())
            checkLoginButtonEnabled()
        }

        binding.editPassword.addTextChangedListener {
            sharedViewModel.validatePasswordInput(it.toString())
            checkLoginButtonEnabled()
        }

        binding.btnContinue.setOnClickListener {
            val login = binding.editLogin.text.toString().trim()
            val password = binding.editPassword.text.toString().trim()

            val isLoginValid = sharedViewModel.validateLoginInput(login)
            val isPasswordValid = sharedViewModel.validatePasswordInput(password)

            if (isLoginValid && isPasswordValid) {
                sharedViewModel.login(login, password)
            } else {
                // Отобразить ошибку о невалидных данных во вводе
//                setLoginError(!isLoginValid)
//                setPasswordError(!isPasswordValid)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        sharedViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SharedViewModel.LoginResult.Success -> {
                    val login = result.login
                    val password = result.password
                    // Отправить запрос на сервер для получения токена
                    sharedViewModel.getToken(login, password)
                }

                is SharedViewModel.LoginResult.InvalidLogin -> {
                    setLoginError(true)
                    setPasswordError(false)
                    showToast(message = "Логин введен некорректно")
                }

                is SharedViewModel.LoginResult.InvalidPassword -> {
                    setLoginError(false)
                    setPasswordError(true)
                    showToast(message = "Пароль введен некорректно")
                }

                else -> {

                }
            }
        }

        sharedViewModel.tokenResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SharedViewModel.TokenResult.Success -> {
                    val token = result.token
                    UserManager.token = token
                    Log.d("LoginFragment", "Received token: $token")
                    goToPaymentFragment()
                }

                is SharedViewModel.TokenResult.Error -> {
                    showToast("Token retrieval error: ${result.errorMessage}")
                }

            }
        }
    }

    private fun setLoginError(isError: Boolean) {
        if (isError) {
            binding.inputLogin.isErrorEnabled = true
            binding.inputLogin.error = getString(R.string.invalid_login)
        } else {
            binding.inputLogin.isErrorEnabled = false
            binding.inputLogin.error = null
        }
    }

    private fun setPasswordError(isError: Boolean) {
        if (isError) {
            binding.inputPassword.isErrorEnabled = true
            binding.inputPassword.error = getString(R.string.invalid_password)
        } else {
            binding.inputPassword.isErrorEnabled = false
            binding.inputPassword.error = null
        }
    }

    private fun checkLoginButtonEnabled() {
        val login = binding.editLogin.text.toString()
        val password = binding.editPassword.text.toString()
        binding.btnContinue.isEnabled =
            sharedViewModel.isLoginValid(login) && sharedViewModel.isPasswordValid(password)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun goToPaymentFragment() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_loginFragment_to_paymentFragment)
    }
}