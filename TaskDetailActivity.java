package com.example.taskjmanager.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskjmanager.R;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taskjmanager.data.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView tvTaskTitle, tvTaskDescription, tvTaskDueDate, tvTaskPriority;
    private Button btnEditTask, btnDeleteTask;
    private FirebaseFirestore db;
    private String taskId;
    private String category="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        tvTaskTitle = findViewById(R.id.tvTaskTitle);
        tvTaskDescription = findViewById(R.id.tvTaskDescription);
        tvTaskDueDate = findViewById(R.id.tvTaskDueDate);
        tvTaskPriority = findViewById(R.id.tvTaskPriority);
        btnEditTask = findViewById(R.id.btnEditTask);
        btnDeleteTask = findViewById(R.id.btnDeleteTask);
        db = FirebaseFirestore.getInstance();

        taskId = getIntent().getStringExtra("taskId");
        if (taskId != null) {
            loadTaskDetails(taskId);
        }

        btnEditTask.setOnClickListener(v -> editTask(taskId));
        btnDeleteTask.setOnClickListener(v -> deleteTask(taskId));
    }

    private void loadTaskDetails(String taskId) {
        DocumentReference taskRef = db.collection("tasks").document(taskId);
        taskRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Task task = documentSnapshot.toObject(Task.class);
                if (task != null) {
                    tvTaskTitle.setText(task.getTitle());
                    tvTaskDescription.setText(task.getDescription());
                    tvTaskDueDate.setText(task.getDueDate().toString());
                    tvTaskPriority.setText(task.getPriority());
                    category= task.getCategory();
                }
            }else {
                Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editTask(String taskId) {
        Intent intent = new Intent(TaskDetailActivity.this, EditTaskActivity.class);
        intent.putExtra("taskId", taskId);
        intent.putExtra("category", category);
        startActivity(intent);
    }


    private void deleteTask(String taskId) {
        DocumentReference taskRef = db.collection("tasks").document(taskId);
        taskRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(TaskDetailActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(TaskDetailActivity.this, "Error deleting task", Toast.LENGTH_SHORT).show();
        });
    }
}

