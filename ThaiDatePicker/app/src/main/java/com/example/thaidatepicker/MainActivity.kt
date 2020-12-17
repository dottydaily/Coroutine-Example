package com.example.thaidatepicker

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        date_picker_button.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.run {
                val dialog = DatePickerDialog(
                        this@MainActivity,
                        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                            date_text_view.text = "$dayOfMonth/${month+1}/$year"
                        },
                        get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)
                )
                dialog.show()
            }
        }
    }
}