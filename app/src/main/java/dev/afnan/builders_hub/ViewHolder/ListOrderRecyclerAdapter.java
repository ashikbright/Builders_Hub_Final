package dev.afnan.builders_hub.ViewHolder;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.afnan.builders_hub.Common.Common;
import dev.afnan.builders_hub.Models.Order;

import static android.content.ContentValues.TAG;

public class ListOrderRecyclerAdapter extends RecyclerView.Adapter<ListOrderRecyclerAdapter.OrderViewHolder> {

    Context context;
    ArrayList<Order> orderList;
    private int orderCount = 0;
    DatabaseReference orderRef;


    public ListOrderRecyclerAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public TextView txtOderID, txtWorkerType, txtDate, txtStatus, txtLocation;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtOderID = itemView.findViewById(R.id.order_id);
            txtWorkerType = itemView.findViewById(R.id.oder_type);
            txtDate = itemView.findViewById(R.id.order_date);
            txtStatus = itemView.findViewById(R.id.order_status);
            txtLocation = itemView.findViewById(R.id.order_place);

            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");
            menu.add(this.getAdapterPosition(), 131, 0, "Update");             //groupId, itemId, order, title
            menu.add(this.getAdapterPosition(), 132, 1, "Delete");
        }
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.my_orders_by_users_list_item_layout, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.txtOderID.setText(order.getOrderId());
        holder.txtWorkerType.setText(order.getWorkerType());
        holder.txtDate.setText(order.getDate());
        holder.txtLocation.setText(order.getAddress());

        String status_code = order.getStatus();
        String status = Common.checkStatus(status_code);
        holder.txtStatus.setText(status);

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrder(int position, String statusCode, String userID) {

        Order CurrentOrder = orderList.get(position);
        CurrentOrder.setStatus(statusCode);
        Common.setCurrentOrderStatus(statusCode);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID);
        String orderID = CurrentOrder.getOrderId();

        updateStatusInFirebase(statusCode, CurrentOrder, orderID);
        orderList.clear();
        notifyDataSetChanged();
    }

    private void updateStatusInFirebase(String statusCode, Order currentOrder, String orderID) {


        Query query = orderRef.child("orderRequests").orderByChild("orderId").equalTo(orderID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Log.d("parentKey", "order id :" + key);
                    Log.d("parentKey", "status code :" + statusCode);

                    if (key != null) {
                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("status", currentOrder.getStatus());
                        orderRef.child("orderRequests").child(key).updateChildren(updates);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);


    }

    public void deleteOrder(int position, String userID) {

        Order CurrentOrder = orderList.get(position);

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(userID);
        String orderID = CurrentOrder.getOrderId();

        updateOrderInFirebase(CurrentOrder, orderID);
        orderList.clear();
        notifyDataSetChanged();

    }

    private void updateOrderInFirebase(Order currentOrder, String orderID) {

        Query query = orderRef.child("orderRequests").orderByChild("orderId").equalTo(orderID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Log.d("parentKey", "order id :" + key);

                    if (key != null) {
                        orderRef.child("orderRequests").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("deleteStatus", "successfully deleted.");
                                } else {
                                    Log.d("deleteStatus", "not deleted.");
                                }
                            }
                        });
                    }
                    notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        };
        query.addListenerForSingleValueEvent(valueEventListener);
    }


}
