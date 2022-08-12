package dev.afnan.builders_hub.ProjectModule.ManageWorkers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.afnan.builders_hub.Common.Common;
import dev.afnan.builders_hub.Interface.onRecyclerItemSelected;
import dev.afnan.builders_hub.Models.Workers;
import dev.afnan.builders_hub.ViewHolder.ProjectAddWorkersAdapter;
import dev.afnan.builders_hub.utility.checkNetworkConnection;


public class ProjectAddWorker extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, onRecyclerItemSelected {

    private String projectID;
    private Button addWorkers;
    private RecyclerView recyclerView;
    private ArrayList<Workers> workerList;
    private ArrayList<Workers> selectedWorkersList;
    private DatabaseReference databaseReference;
    private ProjectAddWorkersAdapter adapter;
    private ImageView backButton;
    public SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_add_worker);

        Intent intent = getIntent();
        projectID = intent.getStringExtra("projectID");

        addWorkers = findViewById(R.id.project_btn_add_workers);
        recyclerView = findViewById(R.id.project_add_workers_recycler_view);
        backButton = findViewById(R.id.btn_back_add_workers_project);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Workers");
        workerList = new ArrayList<>();
        selectedWorkersList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectAddWorkersAdapter(this, workerList, projectID, this);

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

        mSwipeRefreshLayout.setRefreshing(true);

        recyclerView.setAdapter(adapter);


//        updateWorker();

        addWorkers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedWorkersList != null && selectedWorkersList.size() > 0) {

                    alertUser();

                } else {
                    Toast.makeText(ProjectAddWorker.this, "select at least one worker", Toast.LENGTH_SHORT).show();
                }

            }
        });

        backButton.setOnClickListener(v -> finish());

    }

    public void alertUser() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("ADD");
        dialog.setMessage("Do you want to add selected workers \nto the project?");
        dialog.setIcon(R.drawable.ic_dialog_alert);

        dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addWorkers(selectedWorkersList);
                finish();
            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("selectedItem", "operation cancelled.");

            }
        });

        dialog.create().show();
    }

    private void addWorkers(ArrayList<Workers> selectedWorkersList) {
//        FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("WorkerInfo")
//                .child("SelectedWorkers").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Log.d("deleteStatus", "successfully deleted.");
//                } else {
//                    Log.d("deleteStatus", "not deleted.");
//                }
//            }
//        });

        for (int position = 0; position < selectedWorkersList.size(); position++) {

            Workers worker = selectedWorkersList.get(position);

            if (worker != null) {
                if (worker.getName() != null) {
                    String key = worker.getWorkerID();

                    FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("WorkerInfo")
                            .child("SelectedWorkers")
                            .child(key)
                            .setValue(worker).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("movedTo", "successfully added");
                            } else {
                                Log.d("movedTo", "failed to add");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("moveToBackup", "failure: " + e.toString());
                            e.printStackTrace();
                        }
                    });
                }
            }

        }


    }

    private void loadRecyclerViewData() {

        for (int i = 0; i < 9; i++) {

            databaseReference.child(Common.getSelectedWorkerType(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (snapshot.exists()) {
                                Workers workers = dataSnapshot.getValue(Workers.class);
                                if (workers != null) {
                                    if (workers.getName() != null) {
                                        workerList.add(workers);
                                        String key = dataSnapshot.getKey();

                                        if (key != null) {
                                            FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("WorkerInfo")
                                                    .child("Backup")
                                                    .child(key)
                                                    .setValue(workers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("moveToBackup", "success");
                                                    } else {
                                                        Log.d("moveToBackup", "failed");
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("moveToBackup", "failure: " + e.toString());
                                                    e.printStackTrace();
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                        }

                        sortOrders();
                        Log.d("workerCheck", "worker " + workerList.toString());
                        adapter.notifyDataSetChanged();
                        if (workerList.isEmpty()) {
                            Toast.makeText(ProjectAddWorker.this, "No records found", Toast.LENGTH_SHORT).show();
                        }

                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        }


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

    @Override
    public void onItemSelected(ArrayList<Workers> selectedWorkers) {
        if (selectedWorkers != null && selectedWorkers.size() > 0) {
            selectedWorkersList = selectedWorkers;
        }
    }


}