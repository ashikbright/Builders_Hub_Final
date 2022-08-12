package dev.afnan.builders_hub.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import dev.afnan.builders_hub.Interface.onRecyclerItemSelected;
import dev.afnan.builders_hub.Models.Workers;


public class ProjectAddWorkersAdapter extends RecyclerView.Adapter<ProjectAddWorkersAdapter.MyViewHolder> {
    Context context;
    ArrayList<Workers> workersList;
    ArrayList<Workers> selectedWorkers = new ArrayList<>();
    String projectID;
    onRecyclerItemSelected onRecyclerItemSelected;


    public ProjectAddWorkersAdapter(Context context, ArrayList<Workers> workersList, String projectID, onRecyclerItemSelected onRecyclerItemSelected) {
        this.context = context;
        this.workersList = workersList;
        this.projectID = projectID;
        this.onRecyclerItemSelected = onRecyclerItemSelected;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, workerType, email, address;
        private CheckBox chkItemSelected;
        private DatabaseReference workerRef;
        private ImageButton dltWorker;


        public MyViewHolder(@NonNull View itemView, Context context, String projectID) {
            super(itemView);

            name = itemView.findViewById(R.id.add_wname);
            workerType = itemView.findViewById(R.id.add_wtype);
            email = itemView.findViewById(R.id.add_wemail);
            address = itemView.findViewById(R.id.add_waddress);
            chkItemSelected = itemView.findViewById(R.id.chkWorkerSelection);
            dltWorker = itemView.findViewById(R.id.deleteworker);

        }

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_worker_add_display, parent, false);
        return new MyViewHolder(v, context, projectID);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectAddWorkersAdapter.MyViewHolder holder, int position) {
        Workers workers = workersList.get(position);
        holder.name.setText(workers.getName());
        holder.workerType.setText(workers.getWorkerType());
        holder.email.setText(workers.getEmail());
        holder.address.setText(workers.getAddress());

        if (workersList != null && workersList.size() > 0) {

            holder.chkItemSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chkItemSelected.isChecked()) {
                        selectedWorkers.add(workersList.get(position));
                    } else {
                        selectedWorkers.remove(workersList.get(position));
                    }
                    onRecyclerItemSelected.onItemSelected(selectedWorkers);
                }
            });


        }


    }

    @Override
    public int getItemCount() {
        return workersList.size();
    }

}
