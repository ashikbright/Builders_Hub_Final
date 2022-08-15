package dev.twgroups.builders_hub.ProjectModule.ManageAttendance;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.ViewHolder.DisplayAttendanceDateAdapter;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class DisplayAttendanceDate extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ImageButton imageButton;
    private RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public DatabaseReference attendanceRef;
    public DisplayAttendanceDateAdapter adapter;
    public ArrayList<String> attendanceList;
    public String projectID;
    public String formattedDate;
    public SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_attendance_date);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");
        formattedDate = mIntent.getStringExtra("formatted_date");

        attendanceRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("AttendanceInfo");
        attendanceList = new ArrayList<>();
        adapter = new DisplayAttendanceDateAdapter(this, attendanceList, projectID, formattedDate);

        imageButton = findViewById(R.id.btn_back_attendance_date);
        recyclerView = findViewById(R.id.recycler_view_attendance_date);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        checkNetworkConnection connection = new checkNetworkConnection(this);

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

        imageButton.setOnClickListener(v -> finish());
    }


    private void loadRecyclerViewData() {

        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String key = dataSnapshot.getKey();
                        attendanceList.add(key);
                        if (key != null) {
                            Log.d("attendanceDate", key);
                        } else {
                            Log.d("attendanceDate", "key is null");
                        }
                    }

                    sortOrders();
                    adapter.notifyDataSetChanged();
                    Log.d("attendanceDate", "data received successfully");
                    Log.d("attendanceDate", attendanceList.toString());
                } else {

                    Toast.makeText(DisplayAttendanceDate.this, "no records found", Toast.LENGTH_SHORT).show();
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void sortOrders() {
        Collections.sort(attendanceList, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {

                String[] formattedDate1 = str1.split("-");
                String date1 = formattedDate1[0];

                String[] formattedDate2 = str2.split("-");
                String date2 = formattedDate2[0];

                return Integer.valueOf(date1).compareTo(Integer.valueOf(date2));
            }
        });

        Collections.reverse(attendanceList);

    }


    @Override
    public void onRefresh() {
        attendanceList.clear();
        loadRecyclerViewData();
    }
}