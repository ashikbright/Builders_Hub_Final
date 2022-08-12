package dev.afnan.builders_hub.ProjectModule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import dev.afnan.builders_hub.R;

import dev.afnan.builders_hub.ProjectModule.ManageAttendance.MarkAttendance;
import dev.afnan.builders_hub.ProjectModule.ManageMaterials.Material_Home;
import dev.afnan.builders_hub.ProjectModule.ManagePayments.ProjectPayment;
import dev.afnan.builders_hub.ProjectModule.ManagePhotos.ProjectPhotos;
import dev.afnan.builders_hub.ProjectModule.ManageWorkers.ProjectManageWorker;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class ProjectsHome extends AppCompatActivity {

    private String projectID;
    private ImageButton backButton;
    private ImageButton manageWorker;
    private ImageButton manageMaterials;
    private ImageButton manageAttendance;
    private ImageButton managePayments;
    private ImageButton managePhotos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_home);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        backButton = findViewById(R.id.project_home__back_button);
        manageWorker = findViewById(R.id.img_btn_worker);
        manageMaterials = findViewById(R.id.img_btn_material);
        manageAttendance = findViewById(R.id.img_btn_attendance);
        managePayments = findViewById(R.id.img_btn_payments);
        managePhotos = findViewById(R.id.img_btn_photos);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        manageWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectsHome.this, ProjectManageWorker.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

        manageMaterials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectsHome.this, Material_Home.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

        manageAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectsHome.this, MarkAttendance.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

        managePayments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectsHome.this, ProjectPayment.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

        managePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectsHome.this, ProjectPhotos.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });


        backButton.setOnClickListener(v -> finish());
    }

}