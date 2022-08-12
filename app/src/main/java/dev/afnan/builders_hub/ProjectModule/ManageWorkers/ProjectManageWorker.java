package dev.afnan.builders_hub.ProjectModule.ManageWorkers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import dev.afnan.builders_hub.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.afnan.builders_hub.Common.Common;
import dev.afnan.builders_hub.Models.Workers;
import dev.afnan.builders_hub.ViewHolder.ProjectManageWorkerDisplayAdapter;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class ProjectManageWorker extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private String projectID;
    private Button addWorker;
    private RecyclerView recyclerView;
    private ArrayList<Workers> workerList;
    private DatabaseReference databaseReference;
    private ProjectManageWorkerDisplayAdapter adapter;
    private ImageView backButton;
    public SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_manage_worker);

        addWorker = findViewById(R.id.project_btn_add);
        recyclerView = findViewById(R.id.project_workers_recycler_view);
        backButton = findViewById(R.id.btn_back_workers_registration_project);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");
        Common.projectID = projectID;

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("WorkerInfo").child("SelectedWorkers");
        workerList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectManageWorkerDisplayAdapter(this, workerList, projectID);

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /*
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {

                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching data from server
                loadRecyclerViewData();

            }
        });

        recyclerView.setAdapter(adapter);


        addWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectManageWorker.this, ProjectAddWorker.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);          //start activity workerRegistration
            }
        });

        backButton.setOnClickListener(v -> finish());


    }

    private void loadRecyclerViewData() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                workerList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Workers workers = dataSnapshot.getValue(Workers.class);
                    workerList.add(workers);
                }

                sortOrders();
                if (workerList.isEmpty()) {
                    Toast.makeText(ProjectManageWorker.this, "add atleast one worker", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });


    }

    private void sortOrders() {
        Collections.sort(workerList, new Comparator<Workers>() {
            @Override
            public int compare(Workers o1, Workers o2) {
                return o1.getWorkerType().compareToIgnoreCase(o2.getWorkerType());
            }
        });

    }

    @Override
    public void onRefresh() {
        workerList.clear();
        loadRecyclerViewData();
    }
}