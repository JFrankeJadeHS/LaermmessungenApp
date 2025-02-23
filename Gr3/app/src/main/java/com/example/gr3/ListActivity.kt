package com.example.gr3

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val messwerteList = intent.getSerializableExtra("Messwerte") as? ArrayList<Double>
        val adapter = object : ArrayAdapter<Double>(this, android.R.layout.simple_list_item_1, messwerteList ?: emptyList()) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val messwert = getItem(position)

                if (messwert != null) {
                    val roundedValue = messwert.toInt()
                    val textView = view.findViewById<TextView>(android.R.id.text1)
                    textView.text = roundedValue.toString()
                    textView.setTextColor(Color.BLACK)

                    when {
                        roundedValue < 50 -> {
                            view.setBackgroundColor(Color.GREEN)
                        }
                        roundedValue in 50..70 -> {
                            view.setBackgroundColor(Color.YELLOW)
                        }
                        else -> {
                            view.setBackgroundColor(Color.RED)
                        }
                    }
                }
                return view
            }
        }

        val listView = findViewById<ListView>(R.id.list_view_id)
        listView.adapter = adapter
    }
}