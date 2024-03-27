package com.example.Bears-kawai.ui.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("my_prefs", Context.MODE_PRIVATE)

    var currentUrl = inicialUrl

    var qrCodeUrl: String?
        get() = sharedPreferences.getString("qr_code_url", null)
        set(value) {
            sharedPreferences.edit().putString("qr_code_url", value).apply()
        }

    var inicialUrl: String
        get() = sharedPreferences.getString("inicial_url", "https://2.139.185.77/") ?: "https://2.139.185.77/"
        set(value) {
            sharedPreferences.edit().putString("inicial_url", value).apply()
        }

    var username: String
        get() = sharedPreferences.getString("username_log", "invitado") ?: "invitado"
        set(value) {
            sharedPreferences.edit().putString("username_log", value).apply()
        }

    var pass: String
        get() = sharedPreferences.getString("pass_log", "nC1shT5XMO3zd2LVLFIUkOVqvqbzp") ?: "nC1shT5XMO3zd2LVLFIUkOVqvqbzp"
        set(value) {
            sharedPreferences.edit().putString("pass_log", value).apply()
        }
}