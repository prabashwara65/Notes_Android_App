import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.prabashwara.todoapp.R
import com.prabashwara.todoapp.Task
import com.prabashwara.todoapp.TaskDatabaseHelper
import com.prabashwara.todoapp.UpdateTaskActivity
import java.text.SimpleDateFormat
import java.util.*

class TasksAdapter(private var tasks: List<Task>, private val context: Context) :
    RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    private val db: TaskDatabaseHelper = TaskDatabaseHelper(context)

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        val deadLineTextView: TextView = itemView.findViewById(R.id.deadLineTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
        val priorityIndicator: View = itemView.findViewById(R.id.priorityIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.priorityTextView.text = task.priority
        holder.deadLineTextView.text = task.deadLine
        holder.contentTextView.text = task.description

        when (task.priority) {
            "High" -> holder.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.highPriorityColor
                )
            )
            "Medium" -> holder.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.mediumPriorityColor
                )
            )
            "Low" -> holder.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.lowPriorityColor
                )
            )
            else -> holder.priorityIndicator.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.transparent
                )
            )
        }

        // Check if the deadline has passed
        if (isDeadlinePassed(task.deadLine)) {
            // Show reminder with task title
            showPermanentReminder(holder.itemView.context, task.title)
            // Set background color to red
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_light))
        } else {
            // Reset background color to normal
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
        }

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteTask(task.id)
            refreshData(db.getAllTasks())
            Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isDeadlinePassed(deadline: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = Date()
        val deadlineDate = dateFormat.parse(deadline)
        return currentDate.after(deadlineDate)
    }

    private fun showPermanentReminder(context: Context, taskTitle: String) {
        val toastMessage = "Deadline for '$taskTitle' has passed!"
        val toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT)
        toast.show()
        // Use a custom duration for the toast
        val handler = Handler()
        handler.postDelayed({ toast.cancel() }, 5000) // Adjust the duration as needed (5000 ms = 5 seconds)
    }

    fun refreshData(newTasks: List<Task>) {
        tasks = newTasks.sortedByDescending {
            when (it.priority) {
                "High" -> 3
                "Medium" -> 2
                "Low" -> 1
                else -> 0
            }
        }
        notifyDataSetChanged()
    }
}
