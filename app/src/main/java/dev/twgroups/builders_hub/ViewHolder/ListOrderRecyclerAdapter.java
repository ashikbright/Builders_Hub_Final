package dev.twgroups.builders_hub.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import dev.twgroups.builders_hub.Common.Common;
import dev.twgroups.builders_hub.Models.Order;
import dev.twgroups.builders_hub.NotificationService.FcmNotificationsSender;
import dev.twgroups.builders_hub.R;

import static android.content.ContentValues.TAG;

public class ListOrderRecyclerAdapter extends RecyclerView.Adapter<ListOrderRecyclerAdapter.OrderViewHolder> {

    Context context;
    ArrayList<Order> orderList;
    private int orderCount = 0;
    DatabaseReference orderRef;
    private String title = " ";
    private String message = " ";
    private String token;
    private Activity mActivity;


    public ListOrderRecyclerAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        mActivity = (Activity) context;
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

        updateStatusInFirebase(statusCode, CurrentOrder, orderID, userID);
        notifyDataSetChanged();
    }

    private void updateStatusInFirebase(String statusCode, Order currentOrder, String orderID, String userID) {

        Query query = orderRef.child("orderRequests").orderByChild("orderId").equalTo(orderID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Log.d("parentKey", "order id :" + key);
                    Log.d("parentKey", "status code :" + statusCode);

                    Order myOrder = ds.getValue(Order.class);
                    if (myOrder != null && myOrder.getStatus().equals(statusCode)) {
                        Toast.makeText(context, "Failed to update status!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (key != null) {

                            Map<String, Object> updates = new HashMap<String, Object>();
                            updates.put("status", currentOrder.getStatus());

                            orderRef.child("orderRequests").child(key).updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("updateStatus", "child updated successfully!");
                                        sendNotification(currentOrder.getStatus(), userID);
                                    }
                                }
                            });
                        } else {
                            Log.d("keyError", "key not found");
                        }
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

    private void sendNotification(String status, String userID) {
        if (initNotificationData(status)) {
            token = "/topics/" + userID;
            FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token, title,
                    message, context.getApplicationContext(), mActivity);
            notificationsSender.SendNotifications();

        }

    }

    private boolean initNotificationData(String status) {

        if (Integer.parseInt(status) == 1) {
            title = "Thank you for your order.";
            message = "Your order is Accepted";
            return true;

        } else if (Integer.parseInt(status) == 2) {
            title = "Order Cancelled";
            message = "We are sorry to inform you that your order was Cancelled";
            return true;

        }

        return false;

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
