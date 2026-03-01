package com.rock.screenshow.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.rock.screenshow.CF
import com.rock.screenshow.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var lb: ActivitySettingsBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lb = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(lb.root)

        val apiSP = getSharedPreferences(CF.API_SP, Context.MODE_PRIVATE)
        val currentUrl = apiSP.getString(CF.SERVER_URL, "http://localhost:12566/") ?: "http://localhost:12566/"

        lb.tvCurrentUrl.text = "Current Server: $currentUrl"

        lb.btnChangeUrl.setOnClickListener {
            showChangeServerDialog(currentUrl)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showChangeServerDialog(currentUrl: String) {
        val input = EditText(this).apply {
            setPadding(16,16,16,16)
            setText(currentUrl)
            hint = "Enter server URL"
        }

        AlertDialog.Builder(this)
            .setTitle("Set Server URL")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newUrl = input.text.toString().trim()
                if (newUrl.isNotEmpty()) {
                    getSharedPreferences(CF.API_SP, Context.MODE_PRIVATE)
                        .edit {
                            putString(CF.SERVER_URL, newUrl)
                        }
                    Toast.makeText(this, "Server URL updated", Toast.LENGTH_SHORT).show()
                    lb.tvCurrentUrl.text = "Current Server: $newUrl"
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
