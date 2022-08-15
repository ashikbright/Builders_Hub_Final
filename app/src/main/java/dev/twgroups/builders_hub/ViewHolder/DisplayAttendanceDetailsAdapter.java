package dev.twgroups.builders_hub.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.twgroups.builders_hub.R;

import java.util.ArrayList;

import dev.twgroups.builders_hub.Models.Workers;

public class DisplayAttendanceDetailsAdapter extends RecyclerView.Adapter<DisplayAttendanceDetailsAdapter.ViewHolder> {

    ArrayList<Workers> workerList;
    Context context;
    int selected = 1;


    public DisplayAttendanceDetailsAdapter(ArrayList<Workers> workerList, Context context, int i) {
        this.workerList = workerList;
        this.context = context;
        selected = i;
    }

    @NonNull
    @Override
    public DisplayAttendanceDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (selected == 1) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_attendance_display_single_row_true, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_attendance_display_single_row_false, parent, false);
        }

        return new DisplayAttendanceDetailsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayAttendanceDetailsAdapter.ViewHolder holder, int position) {
        Workers worker = workerList.get(position);

        holder.txtWorkerName.setText(worker.getName());
        holder.txtWorkerType.setText(worker.getWorkerType());

    }


    @Override
    public int getItemCount() {
        return workerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtWorkerName;
        public TextView txtWorkerType;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtWorkerName = itemView.findViewById(R.id.worker_name_attendance_single_row);
            txtWorkerType = itemView.findViewById(R.id.worker_type_attendance_single_row);


        }

    }
}
