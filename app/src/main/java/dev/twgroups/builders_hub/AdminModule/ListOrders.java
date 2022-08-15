package dev.twgroups.builders_hub.AdminModule;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import dev.twgroups.builders_hub.ViewHolder.ListOrderRecyclerAdapter;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class ListOrders extends AppCompatActivity {

    private String userID;
    private ImageButton imageButton;
    private RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public FirebaseDatabase database;
    public DatabaseReference order;
    public ListOrderRecyclerAdapter myAdapter;
    public ArrayList<Order> orderList;
    public String statusCode = "0";
    public DatabaseReference orderRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_orders);

        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");

        checkNetworkConnection connection = new checkNetworkConnection(this);

        imageButton = findViewById(R.id.btn_back_list_order);
        database = FirebaseDatabase.getInstance();
        order = database.getReference("Orders");
        orderRef = order.child(userID);

        recyclerView = findViewById(R.id.list_order_recycler);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        orderList = new ArrayList<>();
        myAdapter = new ListOrderRecyclerAdapter(this, orderList);
        recyclerView.setAdapter(myAdapter);

        imageButton.setOnClickListener(v -> finish());


        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Please Wait");
        dialog.setMessage("Loading...");
        dialog.show();

        if (userID != null) {

            order.child(userID).child("orderRequests").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        orderList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Order myOrder = dataSnapshot.getValue(Order.class);
                            orderList.add(myOrder);
                        }

                        sortOrders();
                        myAdapter.notifyDataSetChanged();
                        Log.d("orderData", "data received successfully");
                        Log.d("orderData", orderList.toString());
                    } else {
                        Log.d("orderData", "not found");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("orderData", "data failed");
                }
            });

        } else {
            Log.d("userId", "user ID is null");
        }

        Handler handler = new Handler();
        handler.postDelayed(dialog::dismiss, 500);

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
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 131:
                Log.d("selectedItem", "updating order...");
                showUpdateSpinner(item);
                return true;

            case 132:
                deleteOrder(item);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    private void showUpdateSpinner(MenuItem item) {

        String[] order_status = {
                "Accept Order", "Cancel Order"
        };

        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this, R.layout.order_update_status_layout, order_status);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final Spinner spinner = new Spinner(ListOrders.this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinner.setPadding(3, 3, 3, 3);
        spinner.setAdapter(dataAdapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(ListOrders.this);
        builder.setView(spinner);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String selectedStatus = spinner.getSelectedItem().toString();
                statusCode = updateStatus(selectedStatus);
                myAdapter.updateOrder(item.getGroupId(), statusCode, userID);
                Log.d("selectedStatus ", "status: " + statusCode);

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("selectedItem", "operation cancelled.");
            }
        });

        builder.create().show();

    }

    private String updateStatus(String selectedStatus) {

        if (selectedStatus.equals("Pending")) {
            statusCode = "0";
            Log.d("selectedStatus ", "status: " + statusCode);
        } else if (selectedStatus.equals("Accept Order")) {
            statusCode = "1";
            Log.d("selectedStatus ", "status: " + statusCode);
        } else if (selectedStatus.equals("Cancel Order")) {
            statusCode = "2";
            Log.d("selectedStatus ", "status: " + statusCode);
        }

        return statusCode;
    }

    private void deleteOrder(MenuItem item) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ListOrders.this);
        dialog.setTitle("Delete");
        dialog.setMessage("Are you sure you want to delete \nOnce deleted cannot be reverted.");
        dialog.setIcon(R.drawable.ic_dialog_alert);


        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                myAdapter.deleteOrder(item.getGroupId(), userID);
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
}