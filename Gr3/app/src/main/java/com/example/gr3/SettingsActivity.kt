package com.example.gr3

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.gr3.R

class SettingsActivity : AppCompatActivity() {

    private val PREFS_NAME = "app_preferences"
    private val DARK_MODE_KEY = "dark_mode_enabled"
    private lateinit var switchModes: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        switchModes = findViewById(R.id.switchModes)

        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isDarkModeEnabled = sharedPreferences.getBoolean(DARK_MODE_KEY, false)

        switchModes.isChecked = isDarkModeEnabled

        setAppBackgroundColor(isDarkModeEnabled)

        switchModes.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean(DARK_MODE_KEY, isChecked)
            editor.apply()

            setAppBackgroundColor(isChecked)
        }
    }

    private fun setAppBackgroundColor(isDarkMode: Boolean) {
        if (isDarkMode) {
            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.black))
        } else {
            window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
        }
    }
}