package dev.afnan.builders_hub.ViewHolder;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import java.util.ArrayList;

import dev.afnan.builders_hub.ProjectModule.ManageAttendance.DisplayAttendanceDetails;

public class DisplayAttendanceDateAdapter extends RecyclerView.Adapter<DisplayAttendanceDateAdapter.DisplayAttendanceDateViewHolder> {

    Context context;
    ArrayList<String> attendanceList;
    private String projectID;
    private String formattedDate;


    public DisplayAttendanceDateAdapter(Context context, ArrayList<String> attendanceList, String projectID, String formattedDate) {
        this.context = context;
        this.attendanceList = attendanceList;
        this.projectID = projectID;
    }

    public static class DisplayAttendanceDateViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDate;

        public DisplayAttendanceDateViewHolder(@NonNull View itemView, Context context, ArrayList<String> attendanceList, String projectID) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txt_date_attendance_display);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    String date = attendanceList.get(position);
                    Intent intent = new Intent(context, DisplayAttendanceDetails.class);
                    intent.putExtra("projectID", projectID);
                    intent.putExtra("formatted_date", date);
                    context.startActivity(intent);
                }
            });


        }

    }


    @NonNull
    @Override
    public DisplayAttendanceDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.display_attendance_date_single_row, parent, false);
        return new DisplayAttendanceDateViewHolder(v, context, attendanceList, projectID);
    }

    @Override
    public void onBindViewHolder(@NonNull DisplayAttendanceDateViewHolder holder, int position) {

        formattedDate = attendanceList.get(position);
        holder.txtDate.setText(formattedDate);

    }


    @Override
    public int getItemCount() {
        return attendanceList.size();
    }


}
