package dev.twgroups.builders_hub.ViewHolder;

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

import dev.twgroups.builders_hub.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import dev.twgroups.builders_hub.Models.Workers;


public class ProjectManageWorkerDisplayAdapter extends RecyclerView.Adapter<ProjectManageWorkerDisplayAdapter.MyViewHolder> {
    Context context;
    ArrayList<Workers> workersList;
    String projectID;
    private DatabaseReference workerRef;


    public ProjectManageWorkerDisplayAdapter(Context context, ArrayList<Workers> workersList, String projectID) {
        this.context = context;
        this.workersList = workersList;
        this.projectID = projectID;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, workerType, email, address;
        private ImageButton dltWorker;


        public MyViewHolder(@NonNull View itemView, Context context, String projectID) {
            super(itemView);

            name = itemView.findViewById(R.id.wname);
            workerType = itemView.findViewById(R.id.wtype);
            email = itemView.findViewById(R.id.wemail);
            address = itemView.findViewById(R.id.waddress);
            dltWorker = itemView.findViewById(R.id.deleteworker);
        }


    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_worker_display, parent, false);
        return new MyViewHolder(v, context, projectID);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectManageWorkerDisplayAdapter.MyViewHolder holder, int position) {
        Workers workers = workersList.get(position);
        holder.name.setText(workers.getName());
        holder.workerType.setText(workers.getWorkerType());
        holder.email.setText(workers.getEmail());
        holder.address.setText(workers.getAddress());

        holder.dltWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workerID = workers.getWorkerID();
                alertUser(workerID, context);
            }
        });

    }

    public void alertUser(String workerID, Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Delete");
        dialog.setMessage("Are you sure you want to delete \nOnce deleted cannot be reverted.");
        dialog.setIcon(R.drawable.ic_dialog_alert);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser(workerID);
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

    private void deleteUser(String workerID) {

        workerRef = FirebaseDatabase.getInstance().getReference().child("Projects").child(projectID).child("WorkerInfo")
                .child("SelectedWorkers")
                .child(workerID);

        workerRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("deleteStatus", "successfully deleted.");
                } else {
                    Log.d("deleteStatus", "not deleted.");
                }
            }
        });

        notifyDataSetChanged();

    }


    @Override
    public int getItemCount() {
        return workersList.size();
    }

}
