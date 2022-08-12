package dev.afnan.builders_hub.ViewHolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

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

import dev.afnan.builders_hub.Models.Projects;
import dev.afnan.builders_hub.ProjectModule.ProjectsHome;
import dev.afnan.builders_hub.R;

import static android.content.ContentValues.TAG;

public class HomeFragmentProjectAdapter extends RecyclerView.Adapter<HomeFragmentProjectAdapter.MyViewHolder> {

    Context context;
    ArrayList<Projects> projectList;
    DatabaseReference projectRef;

    public HomeFragmentProjectAdapter(Context context, ArrayList<Projects> projectList) {
        this.context = context;
        this.projectList = projectList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView projectName, projectCity, amountIn, amountOut;
        ImageView optionMenuButton;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            projectName = itemView.findViewById(R.id.project_name);
            projectCity = itemView.findViewById(R.id.project_city);
            amountIn = itemView.findViewById(R.id.amountin);
            amountOut = itemView.findViewById(R.id.amountout);
            optionMenuButton = itemView.findViewById(R.id.btn_editInfo);

        }

        @Override
        public void onClick(View v) {

            int position = this.getAdapterPosition();
            Projects selectedProject = projectList.get(position);
            String projectID = selectedProject.getProjectID();
            String projectName = selectedProject.getName();

            Intent intent = new Intent(context, ProjectsHome.class);
            intent.putExtra("projectID", projectID);
            intent.putExtra("projectName", projectName);
            context.startActivity(intent);

        }
    }


    @NonNull
    @Override
    public HomeFragmentProjectAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.project_single_row, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeFragmentProjectAdapter.MyViewHolder holder, int position) {

        Projects projects = projectList.get(position);
        holder.projectName.setText(projects.getName());
        holder.projectCity.setText(projects.getCity());
        holder.amountIn.setText(projects.getAmountIn());
        holder.amountOut.setText(projects.getAmountOut());


        holder.optionMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popupMenu = new PopupMenu(context, holder.optionMenuButton);
                popupMenu.inflate(R.menu.project_list_options_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {

                            case R.id.menu_item_update:
                                updateProject(position);
                                break;

                            case R.id.menu_item_delete:
                                alertUser(context, position);
                                break;

                            default:
                                break;
                        }

                        return false;
                    }
                });
                popupMenu.show();

            }
        });
    }

    private void updateProject(int position) {

        Projects selectedProject = projectList.get(position);
        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects");
        String projectID = selectedProject.getProjectID();

        showDialog(projectID);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return projectList.size();
    }


    public void alertUser(Context context, int position) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Delete");
        dialog.setMessage("Are you sure you want to delete \nOnce deleted cannot be reverted.");
        dialog.setIcon(R.drawable.ic_dialog_alert);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteOrder(position);
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

    public void deleteOrder(int position) {

        Projects selectedProject = projectList.get(position);
        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects");

        String projectID = selectedProject.getProjectID();
        Log.d("projectID", "id: " + projectID);

        deleteProjectFromFirebase(projectID);
        notifyDataSetChanged();

    }

    private void deleteProjectFromFirebase(String projectID) {

        Query query = projectRef.orderByKey().equalTo(projectID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();
                    Log.d("parentKey", "order id :" + key);

                    if (key != null) {
                        updateLog(key);
                        projectRef.child(key).child("ProjectInfo").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
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
        query.addValueEventListener(valueEventListener);
    }

    private void updateLog(String key) {

        DatabaseReference fromPath = FirebaseDatabase.getInstance().getReference().child("Projects").child(key).child("ProjectInfo");
        DatabaseReference toPath = FirebaseDatabase.getInstance().getReference().child("Log").child("DeletedProjects").child(key);

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

    private void showDialog(String projectID) {

        EditText pr_name, pr_address, pr_city;
        Button create_project;
        TextView closeButton;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.create_project_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);


        pr_name = dialogView.findViewById(R.id.p_name);
        pr_address = dialogView.findViewById(R.id.p_address);
        pr_city = dialogView.findViewById(R.id.p_city);

        pr_address.setEnabled(false);
        pr_address.setVisibility(View.GONE);
        pr_city.setVisibility(View.GONE);


        create_project = dialogView.findViewById(R.id.btn_create);
        create_project.setText("UPDATE");
        closeButton = dialogView.findViewById(R.id.btn_close);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        create_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String projectName = pr_name.getText().toString();

                updateProjectToFirebase(projectID, projectName);
                alertDialog.dismiss();
            }
        });


    }

    private void updateProjectToFirebase(String projectID, String projectName) {
        Query query = projectRef.orderByKey().equalTo(projectID);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key = ds.getKey();

                    Log.d("parentKey", "order id :" + key);

                    if (key != null) {
                        if (!projectName.isEmpty()) {
                            Map<String, Object> updates = new HashMap<String, Object>();
                            updates.put("name", projectName);

                            projectRef.child(key).child("ProjectInfo").updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("updateStatus", "updated successfully");
                                    } else {
                                        Log.d("updateStatus", "Failed to  update!");
                                    }
                                    notifyDataSetChanged();
                                }
                            });

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

}
