package dev.afnan.builders_hub.AdminModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import dev.afnan.builders_hub.Models.User;
import dev.afnan.builders_hub.ViewHolder.orderRecyclerAdapter;
import dev.afnan.builders_hub.utility.checkNetworkConnection;


public class OrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public FirebaseDatabase database;
    public DatabaseReference order;
    public orderRecyclerAdapter myAdapter;
    public ArrayList<User> userList;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //  Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        database = FirebaseDatabase.getInstance();
        order = database.getReference("Orders");

        checkNetworkConnection connection = new checkNetworkConnection(getActivity());

        recyclerView = view.findViewById(R.id.BookingsRecycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        userList = new ArrayList<>();
        myAdapter = new orderRecyclerAdapter(getActivity(), userList);

        registerForContextMenu(recyclerView);


        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
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

        recyclerView.setAdapter(myAdapter);


        myAdapter.setOnItemClickListener(new orderRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                User userInfo = userList.get(position);
                String userID = userInfo.getUserID();

                Log.d("clickListener", "position: " + position);
                Log.d("clickListener", "userID : " + userID);

                Intent intent = new Intent(requireActivity(), ListOrders.class);
                intent.putExtra("userID", userID);
                startActivity(intent);

            }
        });


        return view;
    }


    private void sortOrders() {
        Collections.sort(userList, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });


    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case 121:
                myAdapter.callClient(item.getGroupId());
                return true;
            case 122:
                myAdapter.emailClient(item.getGroupId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }

    }


    @Override
    public void onRefresh() {
        userList.clear();
        loadRecyclerViewData();
    }

    private void loadRecyclerViewData() {
        mSwipeRefreshLayout.setRefreshing(true);

        order.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User currentUser = dataSnapshot.child("userInfo").getValue(User.class);
                    userList.add(currentUser);

                    if (currentUser != null) {
                        Common.CurrentUser = currentUser;
                    }

                }
                sortOrders();
                myAdapter.notifyDataSetChanged();
                Log.d("userData", "data received successfully");
                Log.d("userData", userList.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("userData", "data failed");
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}