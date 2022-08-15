package dev.twgroups.builders_hub.ProjectModule.ManageAttendance;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import dev.twgroups.builders_hub.Interface.onAttendanceWorkerListSelected;
import dev.twgroups.builders_hub.Models.Workers;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.ViewHolder.ProjectMarkAttendanceAdapter;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class MarkAttendance extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, onAttendanceWorkerListSelected {

    private String projectID;
    private Button saveRecords, viewRecords;
    private RecyclerView recyclerView;
    private ArrayList<Workers> workerList;
    private DatabaseReference databaseReference;
    private ProjectMarkAttendanceAdapter adapter;
    private ImageView backButton;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView date;
    private TextView month;
    private TextView year;
    private int dd, mm, yy;
    private TextView presentCountTextView, absentCountTextView;
    private String presentCount, absentCount;
    private ConstraintLayout alertText;
    private String formatted_date;
    private DatabaseReference attendanceRef;
    private DatabaseReference presentRef;
    private DatabaseReference absentRef;
    ArrayList<Workers> presentWorkers = new ArrayList<>();
    ArrayList<Workers> absentWorker = new ArrayList<>();
    private ArrayList<Workers> newWorkerList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendance);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        saveRecords = findViewById(R.id.project_attendance_btn_save_records);
        viewRecords = findViewById(R.id.project_attendance_btn_view_records);

        recyclerView = findViewById(R.id.project_attendance_workers_recycler_view);
        backButton = findViewById(R.id.btn_back_project_mark_attendance);
        alertText = findViewById(R.id.txt_warning_layout);

        date = findViewById(R.id.attendance_txt_date);
        month = findViewById(R.id.attendance_txt_month);
        year = findViewById(R.id.attendance_txt_year);

        presentCountTextView = findViewById(R.id.txt_present);
        absentCountTextView = findViewById(R.id.txt_absent);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        setDate();

        getAttendanceCount();

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("WorkerInfo").child("SelectedWorkers");
        workerList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjectMarkAttendanceAdapter(this, workerList, projectID, this, formatted_date);

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


        saveRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int presentWorkersCount, absentWorkersCount, totalWorkersCount;

                if (presentWorkers.isEmpty() && absentWorker.isEmpty()) {
                    Toast.makeText(MarkAttendance.this, "Please mark attendance first.", Toast.LENGTH_SHORT).show();
                    return;
                }

                presentWorkersCount = presentWorkers.size();
                absentWorkersCount = absentWorker.size();
                totalWorkersCount = newWorkerList.size();

                if (presentWorkersCount + absentWorkersCount < totalWorkersCount) {
                    Toast.makeText(MarkAttendance.this, "Please mark attendance of all the workers!", Toast.LENGTH_SHORT).show();
                    return;
                }

                addRecordsToFirebase();


                Toast.makeText(MarkAttendance.this, "Data saved successfully.", Toast.LENGTH_SHORT).show();

                presentWorkers.clear();
                absentWorker.clear();

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed((Runnable) () -> {
                    finish();
                }, 2000);
            }
        });

        viewRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MarkAttendance.this, DisplayAttendanceDate.class);
                intent.putExtra("projectID", projectID);
                intent.putExtra("formatted_date", formatted_date);
                startActivity(intent);
            }
        });

        backButton.setOnClickListener(v -> finish());


    }


    private void checkDataExists() {
        attendanceRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("AttendanceInfo").child(formatted_date);
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Absent") || snapshot.hasChild("Present")) {
                    Log.d("nodeCheck", "data already exists");
                    alertText.setVisibility(View.VISIBLE);
                    saveRecords.setVisibility(View.GONE);
                    viewRecords.setText("View Attendance Report");
                } else {
                    Log.d("nodeCheck", "no records found");
                    saveRecords.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setDate() {

        Calendar c = Calendar.getInstance();
        yy = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH) + 1;
        dd = c.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM");
        String month_name = month_date.format(c.getTime());
        String year_name = String.valueOf(yy);
        String date_name = String.valueOf(dd);

        formatted_date = date_name + "-" + month_name + "-" + year_name;

        date.setText(date_name);
        month.setText(month_name);
        year.setText(year_name);
        Log.d("MarkAttendance", "date: " + date_name + "\nmonth: " + month_name + "\nyear:" + year_name);

    }

    private void getAttendanceCount() {
        attendanceRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("AttendanceInfo").child(formatted_date);
        attendanceRef.child("Present").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                presentCount = snapshot.getChildrenCount() + "";

                presentCountTextView.setText(presentCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        attendanceRef.child("Absent").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                absentCount = snapshot.getChildrenCount() + "";

                if (absentCount == null) {
                    absentCount = "0";
                }
                absentCountTextView.setText(absentCount);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addRecordsToFirebase() {
        attendanceRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("AttendanceInfo").child(formatted_date);
        presentRef = attendanceRef.child("Present");
        absentRef = attendanceRef.child("Absent");

        for (int position = 0; position < presentWorkers.size(); position++) {    //present workers

            Workers worker = presentWorkers.get(position);

            if (worker != null) {
                if (worker.getName() != null) {

                    presentRef.child(worker.getWorkerID()).setValue(worker).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        for (int position = 0; position < absentWorker.size(); position++) {       //absent workers

            Workers worker = absentWorker.get(position);

            if (worker != null) {
                if (worker.getName() != null) {

                    absentRef.child(worker.getWorkerID()).setValue(worker).addOnCompleteListener(new OnCompleteListener<Void>() {
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

        adapter.notifyDataSetChanged();
    }

    private void loadRecyclerViewData() {

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Workers workers = dataSnapshot.getValue(Workers.class);
                    workerList.add(workers);
                }

                sortOrders();
                if (workerList.isEmpty()) {
                    Toast.makeText(MarkAttendance.this, "please add at least one worker to the project", Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onStart() {
        super.onStart();
        checkDataExists();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkDataExists();
    }

    @Override
    public void onItemSelected(ArrayList<Workers> presentWorkersList, ArrayList<Workers> absentWorkersList, ArrayList<Workers> workersList) {

        if (presentWorkersList != null && presentWorkersList.size() > 0) {
            presentWorkers = presentWorkersList;
        }

        if (absentWorkersList != null && absentWorkersList.size() > 0) {
            absentWorker = absentWorkersList;
        }


        if (workersList != null && workersList.size() > 0) {
            newWorkerList = workerList;
        }

    }
}