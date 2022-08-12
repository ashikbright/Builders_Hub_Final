package dev.afnan.builders_hub.ProjectModule.ManageAttendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.afnan.builders_hub.Models.Workers;
import dev.afnan.builders_hub.ViewHolder.DisplayAttendanceDetailsAdapter;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class DisplayAttendanceDetails extends AppCompatActivity {

    private String projectID;
    private String formattedDate;
    private RecyclerView presentRecyclerView, absentRecyclerView;
    private DatabaseReference attendanceRef;
    private DatabaseReference presentRef;
    private DatabaseReference absentRef;
    private ArrayList<Workers> presentWorkersList;
    private ArrayList<Workers> absentWorkersList;
    private DisplayAttendanceDetailsAdapter presAdapter;
    private DisplayAttendanceDetailsAdapter absAdapter;
    private ImageButton backButton;
    private TextView presentTittle, absentTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_attendance_details);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        presentRecyclerView = findViewById(R.id.recyclerviewPresent);
        absentRecyclerView = findViewById(R.id.recyclerviewAbsent);
        backButton = findViewById(R.id.btn_back_attendance_details);
        presentTittle = findViewById(R.id.present_title);
        absentTitle = findViewById(R.id.absent_title);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");
        formattedDate = mIntent.getStringExtra("formatted_date");

        backButton.setOnClickListener(v -> finish());

        attendanceRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("AttendanceInfo").child(formattedDate);
        presentRef = attendanceRef.child("Present");
        absentRef = attendanceRef.child("Absent");

        presentWorkersList = new ArrayList<>();
        absentWorkersList = new ArrayList<>();

        presentRecyclerView.setHasFixedSize(true);
        presentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        presAdapter = new DisplayAttendanceDetailsAdapter(presentWorkersList, this, 1);
        presentRecyclerView.setAdapter(presAdapter);

        absentRecyclerView.setHasFixedSize(true);
        absentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        absAdapter = new DisplayAttendanceDetailsAdapter(absentWorkersList, this, 2);
        absentRecyclerView.setAdapter(absAdapter);


//        checkDataExists();
        presentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Workers worker = dataSnapshot.getValue(Workers.class);
                        if (worker != null) {
                            presentWorkersList.add(worker);
                        } else {
                            Log.d("attData", "worker not found");
                        }
                    }

                    sortPresentWorkersList();
                    presAdapter.notifyDataSetChanged();
                    Log.d("attData", "data received successfully");

                } else {
                    Log.d("attData", "snapshot does not exist");
                    presentTittle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("attData", "data failed to receive");

            }
        });

        absentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Workers worker = dataSnapshot.getValue(Workers.class);
                        if (worker != null) {
                            absentWorkersList.add(worker);
                        } else {
                            Log.d("attData", "worker not found");
                        }
                    }

                    sortAbsentWorkersList();
                    absAdapter.notifyDataSetChanged();
                    Log.d("attData", "data received successfully");

                } else {
                    Log.d("attData", "snapshot does not exist");
                    absentTitle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("attData", "data failed to receive");

            }
        });


        absentRecyclerView.setAdapter(absAdapter);


    }

    private void checkDataExists() {

    }

    private void sortPresentWorkersList() {
        Collections.sort(presentWorkersList, new Comparator<Workers>() {
            @Override
            public int compare(Workers o1, Workers o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });


    }

    private void sortAbsentWorkersList() {
        Collections.sort(absentWorkersList, new Comparator<Workers>() {
            @Override
            public int compare(Workers o1, Workers o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });


    }

}