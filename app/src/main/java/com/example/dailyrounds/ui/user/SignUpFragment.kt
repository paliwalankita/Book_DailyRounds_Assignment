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
import com.example.dailyrounds.databinding.FragmentSignUpBinding
import com.google.android.material.snackbar.Snackbar

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
        viewModel.initCountryDropdown(binding, requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if user is already logged in, if yes, navigate to home fragment
        val userIsLoggedIn = viewModel.isUserLoggedIn(requireContext())

        if(userIsLoggedIn) {
            findNavController().navigate(R.id.action_signUpFragment_to_dataListFragment)
        }

        // Clears errors from text input layout when user clicks on it
        clearErrorsOnFocusChange()

        // Observes validation result and navigates to login fragment if sign up is successful
        // else if user presses back button, clears validation result
        // else if validation result is not valid, shows relevant errors
        viewModel.validationResult.observe(viewLifecycleOwner) { validationResult ->
            if (validationResult.isValid) {
                viewModel.clearAllErrors(binding)

                viewModel.saveUserDetails(
                    binding.nameTextInput.editText?.text.toString().trim(),
                    binding.passwordTextInput.editText?.text.toString(),
                    binding.countryDropdown.text.toString()
                )

                Snackbar.make(requireView(), "Sign up successful", Snackbar.LENGTH_SHORT).show()

                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                viewModel.clearValidationResult()

            } else if (validationResult.errors.isEmpty()) {
                viewModel.clearAllErrors(binding)
            } else {
                validationResult.errors.forEach {
                    when (it.first) {
                        "name" -> binding.nameTextInput.error = it.second
                        "password" -> binding.passwordTextInput.error = it.second
                        "confirmPassword" -> binding.confirmPasswordTextInput.error = it.second
                        "country" -> binding.countryMenu.error = it.second
                    }
                }
            }
        }

        binding.signUpButton.setOnClickListener {
            viewModel.clearFocus(binding)
            viewModel.validateAndRegister(
                binding.nameTextInput.editText?.text.toString().trim(),
                binding.passwordTextInput.editText?.text.toString(),
                binding.confirmPasswordTextInput.editText?.text.toString(),
                binding.countryDropdown.text.toString()
            )
        }
        // Navigates to the login fragment
        binding.goToLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            viewModel.clearValidationResult()
        }
    }

    private fun clearErrorsOnFocusChange() {
        binding.countryDropdown.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.hideKeyboard(requireContext(), requireView())
                viewModel.clearErrors(binding, "country")
            }
        }

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

        binding.confirmPasswordTextInput.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                viewModel.clearErrors(binding, "confirmPassword")
            }
        }
    }
}