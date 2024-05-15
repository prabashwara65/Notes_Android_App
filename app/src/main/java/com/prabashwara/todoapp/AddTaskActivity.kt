package com.prabashwara.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prabashwara.todoapp.databinding.ActivityAddTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: TaskDatabaseHelper
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabaseHelper(this)

        // Get the priority array from resources
        val priorityArray = resources.getStringArray(R.array.priority_array)

        // Create an adapter for the spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorityArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Set the adapter for the spinner
        binding.prioritySpinner.adapter = adapter

        // Set up date picker for deadline
        binding.deadLineTextView.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveButton.setOnClickListener {
            val title = binding.titleEditText.text.toString()
            val priority = binding.prioritySpinner.selectedItem.toString()
            val deadLine = binding.deadLineTextView.text.toString()
            val description = binding.contentEditText.text.toString()

            val task = Task(0, title, priority, deadLine, description)
            db.insertTasks(task)
            finish()
            Toast.makeText(this@AddTaskActivity, "Task Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDeadlineLabel()
        }

        DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDeadlineLabel() {
        binding.deadLineTextView.text = dateFormat.format(calendar.time)
    }
}
