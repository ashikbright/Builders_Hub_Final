package dev.twgroups.builders_hub.ViewHolder;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import dev.twgroups.builders_hub.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;

import dev.twgroups.builders_hub.Models.User;

public class orderRecyclerAdapter extends RecyclerView.Adapter<orderRecyclerAdapter.OrderViewHolder> {

    Context context;
    ArrayList<User> userList;
    private OnItemClickListener mListener;
    private String userId;
    private DatabaseReference orderRef;
    private int orderCount = 0;

    public orderRecyclerAdapter(Context context, ArrayList<User> orderList) {
        this.context = context;
        this.userList = orderList;

        clearList();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public TextView txtClientName, txtPhone, txtEmail, txtTotalOrders, txtClientID;
        public CardView mCardView;

        public OrderViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            txtClientName = itemView.findViewById(R.id.client_name);
            txtPhone = itemView.findViewById(R.id.client_phone);
            txtEmail = itemView.findViewById(R.id.client_email);
            txtTotalOrders = itemView.findViewById(R.id.order_count);
            mCardView = itemView.findViewById(R.id.orders_item_layout_card_view);

            mCardView.setOnCreateContextMenuListener(this);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });


        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");
            menu.add(this.getAdapterPosition(), 121, 0, "Call");             //groupId, itemId, order, title.
            menu.add(this.getAdapterPosition(), 122, 1, "Email");

        }
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.my_orders_list_item_layout, parent, false);
        return new OrderViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        User userInfo = userList.get(position);
        holder.txtClientName.setText(userInfo.getName());
        holder.txtPhone.setText(userInfo.getPhone());
        holder.txtEmail.setText(userInfo.getEmail());

        userId = userInfo.getUserID();

        orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        DatabaseReference ordersCountRef = orderRef.child(userId).child("orderRequests");

        ordersCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    orderCount = (int) snapshot.getChildrenCount();
                } else {
                    Log.d("ColumnExist", "Not found");
                }
                String orderText = Integer.toString(orderCount);
                holder.txtTotalOrders.setText(orderText);
                Log.d("ColumnExist", "order count: " + orderText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("ColumnExist", "error: " + error);
            }
        });


    }


    @Override
    public int getItemCount() {
        return userList.size();
    }


    public void callClient(int position) {
        User user = userList.get(position);
        String phone = user.getPhone();

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phone));
        context.startActivity(callIntent);

        Log.d("userInfo", "phone : " + phone);
    }

    public void emailClient(int position) {
        User user = userList.get(position);
        String email = user.getEmail();

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));

        context.startActivity(Intent.createChooser(intent, "Choose an Email client :"));

        Log.d("userInfo", "email : " + email);
    }


    public void clearList() {
        HashSet<User> hashSet = new HashSet<User>(userList);
        userList.clear();
        userList.addAll(hashSet);
    }


}
