package com.example.dailyrounds.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dailyrounds.R
import com.example.dailyrounds.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clears errors from text input layout when user clicks on it
        clearErrorsOnFocusChange()

        // Observes login result and navigates to home fragment if login is successful
        // else if user presses back button, clears validation result
        // else if validation result is not valid, shows errors
        viewModel.loginResult.observe(viewLifecycleOwner) { validationResult ->
            if (validationResult.isValid) {
                binding.passwordTextInput.clearFocus()
                viewModel.clearAllErrors(binding)
                viewModel.hideKeyboard(requireContext(), requireView())

                val isValidUser = viewModel.checkUserExists(
                    binding.nameTextInput.editText?.text.toString().trim(),
                    binding.passwordTextInput.editText?.text.toString()
                )

                if(isValidUser) {
                    viewModel.saveCurrentUser(binding.nameTextInput.editText?.text.toString().trim())
                    findNavController().navigate(R.id.action_loginFragment_to_dataListFragment)
                    Snackbar.make(requireView(), "Login successful", Snackbar.LENGTH_SHORT).show()
                }

            } else if (validationResult.errors.isEmpty()) {
                viewModel.clearAllErrors(binding)
            } else {
                validationResult.errors.forEach {
                    when (it.first) {
                        "name" -> binding.nameTextInput.error = it.second
                        "password" -> binding.passwordTextInput.error = it.second
                    }
                }
            }
        }

        binding.loginButton.setOnClickListener {
            binding.nameTextInput.clearFocus()

            viewModel.validateAndLogin(
                binding.nameTextInput.editText?.text.toString().trim(),
                binding.passwordTextInput.editText?.text.toString()
            )
        }

        // Navigates to sign up fragment
        binding.goToSignUpButton.setOnClickListener {
            findNavController().popBackStack()
            viewModel.clearValidationResult()
        }
    }

    // Clears errors from text input layout when user clicks on it
    private fun clearErrorsOnFocusChange() {
        binding.nameTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.clearErrors(binding, "name")
            }
        }

        binding.passwordTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.clearErrors(binding, "password")
            }
        }
    }
}