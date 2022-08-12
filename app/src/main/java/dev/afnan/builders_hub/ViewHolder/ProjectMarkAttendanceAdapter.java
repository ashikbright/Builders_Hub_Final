package dev.afnan.builders_hub.ViewHolder;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dev.afnan.builders_hub.Interface.onAttendanceWorkerListSelected;
import dev.afnan.builders_hub.Models.Workers;


public class ProjectMarkAttendanceAdapter extends RecyclerView.Adapter<ProjectMarkAttendanceAdapter.MyViewHolder> {
    Context context;
    ArrayList<Workers> workersList;
    ArrayList<Workers> presentWorkerList = new ArrayList<>();
    ArrayList<Workers> absentWorkerList = new ArrayList<>();
    String projectID;
    String formatted_date;
    DatabaseReference attendanceRef;
    dev.afnan.builders_hub.Interface.onAttendanceWorkerListSelected onAttendanceWorkerListSelected;

    public ProjectMarkAttendanceAdapter(Context context, ArrayList<Workers> workersList, String projectID, onAttendanceWorkerListSelected onAttendanceWorkerListSelected, String formatted_date) {
        this.context = context;
        this.workersList = workersList;
        this.projectID = projectID;
        this.formatted_date = formatted_date;
        this.onAttendanceWorkerListSelected = onAttendanceWorkerListSelected;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, workerType;
        private RadioGroup radioGroup;
        private RadioButton radioPresent, radioAbsent;
        private DatabaseReference attendanceRef;

        public MyViewHolder(@NonNull View itemView, String projectID, String formatted_date) {
            super(itemView);

            name = itemView.findViewById(R.id.worker_name_attendance_single_row);
            workerType = itemView.findViewById(R.id.worker_type_attendance_single_row);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            radioPresent = itemView.findViewById(R.id.radio_present);
            radioAbsent = itemView.findViewById(R.id.radio_absent);

            checkDataExists(projectID, formatted_date);

        }

        private void checkDataExists(String projectID, String formatted_date) {
            attendanceRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("AttendanceInfo").child(formatted_date);
            attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("Absent") || snapshot.hasChild("Present")) {
                        Log.d("nodeCheck", "data already exists");
                        radioGroup.setVisibility(View.GONE);
                    } else {
                        Log.d("nodeCheck", "no records found");
//                    radioGroup.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.worker_attendance_single_row, parent, false);
        return new MyViewHolder(v, projectID, formatted_date);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectMarkAttendanceAdapter.MyViewHolder holder, int position) {
        Workers workers = workersList.get(position);
        holder.name.setText(workers.getName());
        holder.workerType.setText(workers.getWorkerType());

        if (workersList != null && workersList.size() > 0) {

            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    if (holder.radioPresent.isChecked()) {
                        presentWorkerList.add(workersList.get(position));
                    } else {
                        presentWorkerList.remove(workersList.get(position));
                    }

                    if (holder.radioAbsent.isChecked()) {
                        absentWorkerList.add(workersList.get(position));
                    } else {
                        absentWorkerList.remove(workersList.get(position));
                    }

                    onAttendanceWorkerListSelected.onItemSelected(presentWorkerList, absentWorkerList);


                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return workersList.size();
    }


}
