package com.example.taskjmanager.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.taskjmanager.R;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taskjmanager.adapter.TaskAdapter;
import com.example.taskjmanager.data.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnSearch;
    private Spinner spinnerCategory;
    private ListView lvTasks;
    private Button btnAddTask;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        lvTasks = findViewById(R.id.lvTasks);
        btnAddTask = findViewById(R.id.btnAddTask);
        db = FirebaseFirestore.getInstance();


        btnSearch.setOnClickListener(v -> searchTasks(etSearch.getText().toString()));

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterTasksByCategory(spinnerCategory.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadTasks();

        btnAddTask.setOnClickListener(v -> startActivity(new Intent(TaskListActivity.this, AddTaskActivity.class)));

        lvTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task task = (Task) parent.getItemAtPosition(position);
            Intent intent = new Intent(TaskListActivity.this, TaskDetailActivity.class);
            intent.putExtra("taskId", task.getId());
            startActivity(intent);
        });
    }

    private void loadTasks() {
        db.collection("tasks").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Task task = document.toObject(Task.class);
                        tasks.add(task);
                    }
                    TaskAdapter adapter = new TaskAdapter(TaskListActivity.this, tasks);
                    lvTasks.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskListActivity.this, "Failed to load tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void searchTasks(String query) {
        db.collection("tasks").whereEqualTo("title", query).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Task task = document.toObject(Task.class);
                        tasks.add(task);
                    }
                    TaskAdapter adapter = new TaskAdapter(TaskListActivity.this, tasks);
                    lvTasks.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskListActivity.this, "Failed to search tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterTasksByCategory(String category) {
        db.collection("tasks").whereEqualTo("category", category).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task> tasks = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Task task = document.toObject(Task.class);
                        tasks.add(task);
                    }
                    TaskAdapter adapter = new TaskAdapter(TaskListActivity.this, tasks);
                    lvTasks.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(TaskListActivity.this, "Failed to filter tasks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


