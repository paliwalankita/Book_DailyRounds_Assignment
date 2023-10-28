package com.example.dailyrounds.ui.user

import android.R
import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dailyrounds.api.CountryJsonReader
import com.example.dailyrounds.databinding.FragmentSignUpBinding
import com.example.dailyrounds.repository.UserRepository
import com.example.dailyrounds.repository.ValidationResult

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application.applicationContext)

    // LiveData to hold the validation result of registration
    private val _validationResult = MutableLiveData<ValidationResult>()
    val validationResult: LiveData<ValidationResult> = _validationResult

    // Clear the validation result LiveData
    fun clearValidationResult() {
        _validationResult.value = ValidationResult(false, emptyList())
    }

    // Clear error messages for specific fields in the UI
    fun clearErrors(binding: FragmentSignUpBinding, vararg fields: String) {
        fields.forEach {
            when (it) {
                "name" -> {
                    binding.nameTextInput.error = null
                    binding.nameTextInput.isErrorEnabled = false
                }

                "password" -> {
                    binding.passwordTextInput.error = null
                    binding.passwordTextInput.isErrorEnabled = false
                }

                "confirmPassword" -> {
                    binding.confirmPasswordTextInput.error = null
                    binding.confirmPasswordTextInput.isErrorEnabled = false
                }

                "country" -> {
                    binding.countryMenu.error = null
                    binding.countryMenu.isErrorEnabled = false
                }
            }
        }
    }

    // Validate user inputs and update the LiveData accordingly
    fun validateAndRegister(
        name: String,
        password: String,
        confirmPassword: String,
        country: String
    ) {
        val result = userRepository.validateRegistration(name, password, confirmPassword, country)
        _validationResult.value = result
    }

    // Clear all error messages in the UI
    fun clearAllErrors(binding: FragmentSignUpBinding) {
        binding.nameTextInput.error = null
        binding.nameTextInput.isErrorEnabled = false
        binding.passwordTextInput.error = null
        binding.passwordTextInput.isErrorEnabled = false
        binding.confirmPasswordTextInput.error = null
        binding.confirmPasswordTextInput.isErrorEnabled = false
        binding.countryMenu.error = null
        binding.countryMenu.isErrorEnabled = false
    }

    // Initialize the country dropdown with available countries
    fun initCountryDropdown(binding: FragmentSignUpBinding, context: Context) {
        val countryList =
            CountryJsonReader(context).readCountriesFromJson().map { it.value.country }.sorted()
        val countryAdapter = ArrayAdapter(
            binding.root.context,
            R.layout.simple_dropdown_item_1line,
            countryList
        )
        binding.countryDropdown.setAdapter(countryAdapter)
    }

    fun saveUserDetails(username: String, password: String, country: String) {
        userRepository.saveUserDetails(username, password, country)
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return userRepository.isUserLoggedIn(context)
    }

    // Clear focus from UI elements
    fun clearFocus(binding: FragmentSignUpBinding) {
        binding.nameTextInput.clearFocus()
        binding.passwordTextInput.clearFocus()
        binding.confirmPasswordTextInput.clearFocus()
        binding.countryDropdown.clearFocus()
    }

    // Hide the soft keyboard
    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}