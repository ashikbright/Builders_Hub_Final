package dev.twgroups.builders_hub.WorkerModule;

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

import dev.twgroups.builders_hub.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.twgroups.builders_hub.Common.Common;
import dev.twgroups.builders_hub.Models.Workers;
import dev.twgroups.builders_hub.ViewHolder.DisplayWorkerDetailsAdapter;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class WorkersDetails extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    ArrayList<Workers> workerList;
    DatabaseReference databaseReference;
    DisplayWorkerDetailsAdapter adapter;
    Button btnAdd;
    private ImageView backButton;
    public SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workers_details);
        recyclerView = findViewById(R.id.workers_recycler_view);
        btnAdd = findViewById(R.id.btnadd);
        backButton = findViewById(R.id.btn_back_workers_registration);

        Intent mIntent = getIntent();
        int selectedItem = mIntent.getIntExtra("itemSelected", 0);
        String selectedItemString = Common.getSelectedWorkerType(selectedItem);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Workers").child(selectedItemString);
        workerList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DisplayWorkerDetailsAdapter(this, workerList, selectedItemString);

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


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkersDetails.this, WorkerRegistration.class);
                intent.putExtra("itemSelected", selectedItem);
                startActivity(intent);          //start activity workerRegistration
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }


    @Override
    public void onRefresh() {
        workerList.clear();
        loadRecyclerViewData();
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
                adapter.notifyDataSetChanged();
                if (workerList.isEmpty()) {
                    Toast.makeText(WorkersDetails.this, "No records found", Toast.LENGTH_SHORT).show();
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
        Collections.sort(workerList, new Comparator<Workers>() {
            @Override
            public int compare(Workers o1, Workers o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });


    }
}