package dev.twgroups.builders_hub.UserModule;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.twgroups.builders_hub.Models.Order;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.ViewHolder.bookingsRecyclerAdapter;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;


public class BookingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference order;
    bookingsRecyclerAdapter myAdapter;
    ArrayList<Order> orderList;
    public String userID;
    public SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        database = FirebaseDatabase.getInstance();
        order = database.getReference("Orders");


        recyclerView = view.findViewById(R.id.bookings_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        orderList = new ArrayList<>();
        myAdapter = new bookingsRecyclerAdapter(getActivity(), orderList);
        recyclerView.setAdapter(myAdapter);

        checkNetworkConnection connection = new checkNetworkConnection(getActivity());
        userID = FirebaseAuth.getInstance().getUid();

        mSwipeRefreshLayout = view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching data from server
                userID = FirebaseAuth.getInstance().getUid();
                loadRecyclerViewData();
            }
        });


        return view;
    }

    private void loadRecyclerViewData() {

        mSwipeRefreshLayout.setRefreshing(true);
        if (userID != null) {
            order.child(userID).child("orderRequests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    orderList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Order myOrder = dataSnapshot.getValue(Order.class);
                        orderList.add(myOrder);
                    }

                    sortOrders();
                    myAdapter.notifyDataSetChanged();
                    Log.d("orderData", "data received successfully");
                    mSwipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("orderData", "data failed");
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });

        } else {
            Log.d("userID", "userID is null");
        }
    }

    private void sortOrders() {
        Collections.sort(orderList, new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return o1.getOrderId().compareToIgnoreCase(o2.getOrderId());
            }
        });

        Collections.reverse(orderList);
    }


    @Override
    public void onRefresh() {
        orderList.clear();
        loadRecyclerViewData();
    }
}