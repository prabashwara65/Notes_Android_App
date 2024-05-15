package com.prabashwara.todoapp


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper  (context: Context) : SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {

        private const val DATABASE_NAME = "Taskapp.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "Todo"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "Title"
        private const val COLUMN_PRIORITY  = "Priority"
        private const val COLUMN_DEADLINE = "DeadLine"
        private const val COLUMN_CONTENT = "Content"

    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery =
            "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT,$COLUMN_PRIORITY TEXT,$COLUMN_DEADLINE TEXT,$COLUMN_CONTENT TEXT)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertTasks(tasks: Task): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, tasks.title)
            put(COLUMN_PRIORITY, tasks.priority)
            put(COLUMN_DEADLINE, tasks.deadLine)
            put(COLUMN_CONTENT, tasks.description)
        }

        return db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
                val deadLine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val task = Task(id, title, priority, deadLine, description)
                tasks.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }

    fun  updateTask(tasks: Task){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, tasks.title)
            put(COLUMN_PRIORITY,tasks.priority)
            put(COLUMN_DEADLINE, tasks.deadLine)
            put(COLUMN_CONTENT, tasks.description)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(tasks.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getTaskById(taskID: Int): Task{
        val db =readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = $taskID"
        val cursor = db.rawQuery(query, null)
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
        val deadLine = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
        val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))

        cursor.close()
        db.close()
        return Task(id, title, priority,deadLine,description)
    }

    fun deleteTask(taskID : Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(taskID.toString())
        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }
}