package com.example.dailyrounds.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.dailyrounds.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<Pair<String, String>>
)

class UserRepository(private val context: Context){
    private val errors = mutableListOf<Pair<String, String>>()

    fun validateRegistration(name: String, password: String, confirmPassword: String, country: String)
    : ValidationResult{
        if(errors.isNotEmpty()){
            errors.clear()
        }

        validName(name)
        validPassword(password)
        validConfirmPassword(password, confirmPassword)
        isCountrySelected(country)

        return ValidationResult(errors.isEmpty(), errors)

    }

    fun validateLogin(name: String, password: String): ValidationResult{
        if(errors.isNotEmpty()) {
            errors.clear()
        }

        validName(name)
        validPassword(password)

        return ValidationResult(errors.isEmpty(), errors)
    }

    fun checkUserExists(name: String, password: String): ValidationResult{
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userListJson = sharedPreferences.getString("user_list", "[]")
        val userListType = object : TypeToken<List<User>>(){}.type
        val userList = Gson().fromJson<List<User>>(userListJson, userListType).toMutableList()

        for(user in userList){
            if(user.name == name && user.password == password){
                return ValidationResult(true, emptyList() )

            }
        }
        return ValidationResult(false, listOf(Pair("name", "Invalid username or password")) )

    }

    fun saveUserDetails(name: String, password: String, country: String){
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userListJson = sharedPreferences.getString("user_list", "[]")
        val userListType = object : TypeToken<List<User>>(){}.type
        val userList = Gson().fromJson<List<User>>(userListJson, userListType).toMutableList()

        userList.add(User(name, password, country))

        val editor = sharedPreferences.edit()
        editor.putString("user_list", Gson().toJson(userList))
        editor.apply()
    }

    fun saveCurrentUser(userName: String){
        val sharedPreference = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("current_user", userName)
        editor.apply()
    }

    private fun validName(name: String): Boolean{
        return if(name.isEmpty()) {
            errors.add(Pair("name", "Name is required"))
            false
        }else if(name.contains(Regex("[0-9]") )) {
            errors.add(Pair("name", "Name cannot contain numbers"))
            false
        }else {
            true
        }
    }

    private fun validPassword(password: String): Boolean {
        val passwordPattern =
            Regex("^(?=.*[0-9])(?=.*[!@#\$%&()\\[\\]])(?=.*[a-z])(?=.*[A-Z]).{8,}$")

        if (password.isEmpty()) {
            errors.add(Pair("password", "Password is required"))
            return false
        }

        if (!password.matches(passwordPattern)) {
            if (!password.matches(Regex(".*[0-9].*"))) {
                errors.add(Pair("password", "Password must contain at least one digit"))
            } else if (!password.matches(Regex(".*[!@#\$%&()].*"))) {
                errors.add(Pair("password", "Password must contain at least one special character"))
            } else if (!password.matches(Regex(".*[a-z].*"))) {
                errors.add(Pair("password", "Password must contain at least one lowercase letter"))
            } else if (!password.matches(Regex(".*[A-Z].*"))) {
                errors.add(Pair("password", "Password must contain at least one uppercase letter"))
            } else {
                errors.add(Pair("password", "Password must have at least 8 characters"))
            }
            return false
        }

        return true
    }

    private fun validConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (confirmPassword.isEmpty()) {
            errors.add(Pair("confirmPassword", "Confirm password is required"))
            false
        } else if (confirmPassword != password) {
            errors.add(Pair("confirmPassword", "Passwords do not match"))
            false
        } else {
            true
        }
    }

    private fun isCountrySelected(country: String): Boolean {
        return if (country.isEmpty()) {
            errors.add(Pair("country", "Country is required"))
            false
        } else {
            true
        }
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val currentUser = sharedPreferences.getString("current_user", "")
        return currentUser != ""
    }

}