package dev.afnan.builders_hub.ViewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import dev.afnan.builders_hub.Common.Common;
import dev.afnan.builders_hub.Models.Workers;

import static android.content.ContentValues.TAG;

public class DisplayWorkerDetailsAdapter extends RecyclerView.Adapter<DisplayWorkerDetailsAdapter.MyViewHolder> {
    Context context;
    ArrayList<Workers> workersList;
    String selectedItem;
    //DatabaseReference databaseReference;


    public DisplayWorkerDetailsAdapter(Context context, ArrayList<Workers> workersList, String selectedItem) {
        this.context = context;
        this.workersList = workersList;
        this.selectedItem = selectedItem;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_worker_display, parent, false);
        return new MyViewHolder(v, selectedItem, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayWorkerDetailsAdapter.MyViewHolder holder, int position) {
        Workers workers = workersList.get(position);
        holder.name.setText(workers.getName());
        holder.workerType.setText(workers.getWorkerType());
        holder.email.setText(workers.getEmail());
        holder.address.setText(workers.getAddress());


    }

    @Override
    public int getItemCount() {
        return workersList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, workerType, email, address;
        private ImageButton dltWorker;
        private DatabaseReference workerRef;


        public MyViewHolder(@NonNull View itemView, String selectedItem, Context context) {
            super(itemView);
            name = itemView.findViewById(R.id.wname);
            workerType = itemView.findViewById(R.id.wtype);
            email = itemView.findViewById(R.id.wemail);
            address = itemView.findViewById(R.id.waddress);
            dltWorker = itemView.findViewById(R.id.deleteworker);

            dltWorker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertUser(email.getText().toString(), selectedItem, context);
                }
            });

        }

        public void alertUser(String email, String selectedItem, Context context) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Delete");
            dialog.setMessage("Are you sure you want to delete \nOnce deleted cannot be reverted.");
            dialog.setIcon(R.drawable.ic_dialog_alert);

            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteUser(email, selectedItem);
                    Common.userDeleted = true;
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

        private void deleteUser(String email, String selectedItem) {
            workerRef = FirebaseDatabase.getInstance().getReference().child("Workers").child(selectedItem);

            Query query = workerRef.orderByChild("email").equalTo(email);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String key = ds.getKey();
                        Log.d("parentKey", "worker id :" + key);

                        if (key != null) {
                            updateLog(selectedItem, key);
                            workerRef.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d(TAG, databaseError.getMessage());
                }
            };
            query.addListenerForSingleValueEvent(valueEventListener);

        }

        private void updateLog(String selectedItem, String key) {

            DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Workers").child(selectedItem).child(key);
            DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Log").child("DeletedUsers").child(selectedItem);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    toPath.setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                Log.d(TAG, "Success!");
                            } else {
                                Log.d(TAG, "Copy failed!");
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d("TAG", databaseError.getMessage()); //Never ignore potential errors!
                }
            };
            fromPath.addListenerForSingleValueEvent(valueEventListener);

        }


    }
}
