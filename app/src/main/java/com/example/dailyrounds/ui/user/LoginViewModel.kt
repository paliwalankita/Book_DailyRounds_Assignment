package com.example.dailyrounds.ui.user

import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dailyrounds.databinding.FragmentLoginBinding
import com.example.dailyrounds.repository.UserRepository
import com.example.dailyrounds.repository.ValidationResult

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application.applicationContext)

    // LiveData to hold the login validation result
    private val _loginResult = MutableLiveData<ValidationResult>()
    val loginResult: LiveData<ValidationResult> = _loginResult

    // Validate user inputs and update LiveData accordingly
    fun validateAndLogin(name: String, password: String) {
        val validationResult = userRepository.validateLogin(name, password)
        _loginResult.value = validationResult
    }

    // Clear the login validation result LiveData
    fun clearValidationResult() {
        _loginResult.value = ValidationResult(false, emptyList())
    }

    // Clear error messages for specific fields in the UI
    fun clearErrors(binding: FragmentLoginBinding, vararg fields: String) {
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
            }
        }
    }

    fun saveCurrentUser(name: String) {
        userRepository.saveCurrentUser(name)
    }

    fun checkUserExists(name: String, password: String) : Boolean {
        val validationResult = userRepository.checkUserExists(name, password)
        if(validationResult.isValid) {
            return true
        } else {
            _loginResult.value = validationResult
            return false
        }
    }

    fun clearAllErrors(binding: FragmentLoginBinding) {
        binding.nameTextInput.error = null
        binding.nameTextInput.isErrorEnabled = false
        binding.passwordTextInput.error = null
        binding.passwordTextInput.isErrorEnabled = false
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}