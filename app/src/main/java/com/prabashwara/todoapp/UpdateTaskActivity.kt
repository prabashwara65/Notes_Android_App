package com.prabashwara.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.prabashwara.todoapp.databinding.ActivityUpdateTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTaskBinding
    private lateinit var db: TaskDatabaseHelper
    private var taskID: Int = -1
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabaseHelper(this)

        taskID = intent.getIntExtra("task_id", -1)
        if (taskID == -1) {
            finish()
            return
        }

        val task = db.getTaskById(taskID)
        binding.updateTitleEditText.setText(task.title)
        binding.updateDeadLineTextView.text = task.deadLine
        binding.updateContentEditText.setText(task.description)

        // Set up the ArrayAdapter for the Spinner
        val priorities = arrayOf("High", "Medium", "Low")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.updatePrioritySpinner.adapter = adapter

        // Set selected priority
        val priorityIndex = priorities.indexOf(task.priority)
        if (priorityIndex != -1) {
            binding.updatePrioritySpinner.setSelection(priorityIndex)
        }

        // Set up date picker for deadline
        binding.updateDeadLineTextView.setOnClickListener {
            showDatePickerDialog()
        }

        binding.updateSaveButton.setOnClickListener {
            val newTitle = binding.updateTitleEditText.text.toString()
            val newPriority = binding.updatePrioritySpinner.selectedItem.toString()
            val newDeadLine = binding.updateDeadLineTextView.text.toString()
            val newDescription = binding.updateContentEditText.text.toString()
            val updateTask = Task(taskID, newTitle, newPriority, newDeadLine, newDescription)
            db.updateTask(updateTask)
            finish()
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
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
        binding.updateDeadLineTextView.text = dateFormat.format(calendar.time)
    }
}
