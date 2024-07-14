package com.example.taskjmanager.views;

import static com.example.taskjmanager.data.Task.filterTasksForToday;
import static com.example.taskjmanager.data.Task.filterTasksForWeek;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.taskjmanager.R;
import com.example.taskjmanager.adapter.TaskAdapter;
import com.example.taskjmanager.data.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ImageView ivProfilePicture;
    private RelativeLayout rlProfilePicture;
    private TextView tvUserName, tvAll;
    private ListView lvTodayTasks, lvWeekTasks;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        rlProfilePicture = findViewById(R.id.rlProfilePicture);
        rlProfilePicture.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
        });

        tvAll = findViewById(R.id.tvAll);
        tvAll.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, TaskListActivity.class));
        });

        ivProfilePicture = findViewById(R.id.ivProfilePicture);
        tvUserName = findViewById(R.id.tvUserName);
        lvTodayTasks = findViewById(R.id.lvTodayTasks);
        lvWeekTasks = findViewById(R.id.lvWeekTasks);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            loadUserProfile(user);
            loadUserTasks(user.getUid());
        }
    }


    private void loadUserProfile(FirebaseUser user) {
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");
                        tvUserName.setText(mAuth.getCurrentUser().getEmail());
                        Glide.with(DashboardActivity.this)
                                .load(profilePictureUrl)
                                .placeholder(R.drawable.logo)
                                .into(ivProfilePicture);
                    }
                });
    }

    private void loadUserTasks(String userId) {
        db.collection("tasks").whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            List<DocumentSnapshot> documents = querySnapshot.getDocuments();
                            // Filter tasks for today and this week
                            List<Task> todayTasks = filterTasksForToday(documents);
                            List<Task> weekTasks = filterTasksForWeek(documents);
                            // Update ListViews
                            TaskAdapter todayAdapter = new TaskAdapter(DashboardActivity.this, todayTasks);
                            TaskAdapter weekAdapter = new TaskAdapter(DashboardActivity.this, weekTasks);
                            lvTodayTasks.setAdapter(todayAdapter);
                            lvWeekTasks.setAdapter(weekAdapter);
                        }
                    }
                });
    }
}
