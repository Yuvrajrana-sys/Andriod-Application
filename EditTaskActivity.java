package com.example.taskjmanager.views;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskjmanager.R;
import com.example.taskjmanager.data.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditTaskActivity extends AppCompatActivity {

    private EditText etTaskTitle, etTaskDescription, etTaskDueDate, etTaskPriority;
    private Button btnSaveTask;
    private FirebaseFirestore db;
    private String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskDueDate = findViewById(R.id.etTaskDueDate);
        etTaskPriority = findViewById(R.id.etTaskPriority);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        db = FirebaseFirestore.getInstance();

        taskId = getIntent().getStringExtra("taskId");
        String category = getIntent().getStringExtra("category"); // Retrieve category from intent

        if (taskId != null) {
            loadTaskDetails(taskId);
        }

        btnSaveTask.setOnClickListener(v -> saveTask(taskId, category));
    }

    private void saveTask(String taskId, String category) {
        String title = etTaskTitle.getText().toString();
        String description = etTaskDescription.getText().toString();
        String dueDateString = etTaskDueDate.getText().toString();
        String priority = etTaskPriority.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dueDate = null;
        try {
            dueDate = sdf.parse(dueDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(EditTaskActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        Task task = new Task(taskId, title, description, dueDate, priority, category);

        DocumentReference taskRef = db.collection("tasks").document(taskId);
        taskRef.set(task).addOnSuccessListener(aVoid -> {
            Toast.makeText(EditTaskActivity.this, "Task updated", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(EditTaskActivity.this, "Error updating task", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadTaskDetails(String taskId) {
        DocumentReference taskRef = db.collection("tasks").document(taskId);
        taskRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Task task = documentSnapshot.toObject(Task.class);
                if (task != null) {
                    etTaskTitle.setText(task.getTitle());
                    etTaskDescription.setText(task.getDescription());
                    etTaskDueDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(task.getDueDate()));
                    etTaskPriority.setText(task.getPriority());
                }
            }
        });
    }

}