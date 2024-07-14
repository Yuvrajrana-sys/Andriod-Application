package com.example.taskjmanager.views;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskjmanager.R;
import com.example.taskjmanager.data.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTaskTitle, etTaskDescription, etTaskDueDate, etTaskPriority;
    private Spinner spinnerCategory;
    private Button btnSaveTask;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        etTaskTitle = findViewById(R.id.etTaskTitle);
        etTaskDescription = findViewById(R.id.etTaskDescription);
        etTaskDueDate = findViewById(R.id.etTaskDueDate);
        etTaskPriority = findViewById(R.id.etTaskPriority);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        db = FirebaseFirestore.getInstance();

        setupCategorySpinner();

        btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void saveTask() {
        String title = etTaskTitle.getText().toString();
        String description = etTaskDescription.getText().toString();
        String dueDateString = etTaskDueDate.getText().toString();
        String priority = etTaskPriority.getText().toString();
        String category = spinnerCategory.getSelectedItem().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date dueDate;
        try {
            dueDate = sdf.parse(dueDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(AddTaskActivity.this, "Invalid date format", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique ID for the task
        String taskId = UUID.randomUUID().toString();

        Task task = new Task(taskId, title, description, dueDate, priority, category);

        db.collection("tasks")
                .document(taskId)
                .set(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddTaskActivity.this, "Task added", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddTaskActivity.this, "Error adding task", Toast.LENGTH_SHORT).show());
    }
}

