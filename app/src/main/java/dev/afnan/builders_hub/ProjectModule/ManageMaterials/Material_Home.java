package dev.afnan.builders_hub.ProjectModule.ManageMaterials;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.afnan.builders_hub.Models.MaterialsModel;
import dev.afnan.builders_hub.ViewHolder.Material_Adapter;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class Material_Home extends AppCompatActivity {
    private Button receivedMat, usedMat;
    private RecyclerView recyclerViewRec, recyclerViewUse;
    private ImageView options;
    private DatabaseReference databaseReference;
    private DatabaseReference usedRef;
    private ArrayList<MaterialsModel> receivedMaterialsList = new ArrayList<MaterialsModel>();
    private ArrayList<MaterialsModel> usedMaterialsList = new ArrayList<MaterialsModel>();
    private Material_Adapter recAdapter;
    private Material_Adapter useAdapter;
    private ImageButton backButton;
    private String projectID;
    private TextView MaterialRecTitle, MaterialUsedTitle;
    private ConstraintLayout matRecText, matUsdText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material__home);

        receivedMat = findViewById(R.id.btn_received);
        usedMat = findViewById(R.id.btn_used);
        recyclerViewRec = findViewById(R.id.recyclerviewRec);
        recyclerViewUse = findViewById(R.id.recyclerviewUse);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        backButton = findViewById(R.id.btn_back_list_order);
        MaterialRecTitle = findViewById(R.id.mat_rec_title);
        MaterialUsedTitle = findViewById(R.id.mat_used_title);
        matRecText = findViewById(R.id.mat_rec_text);
        matUsdText = findViewById(R.id.mat_used_text);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        backButton.setOnClickListener(v -> finish());

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("MaterialInfo").child("ReceivedInfo");
        usedRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("MaterialInfo").child("UsedInfo");

        recyclerViewRec.setHasFixedSize(true);
        recyclerViewRec.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewUse.setHasFixedSize(true);
        recyclerViewUse.setLayoutManager(new LinearLayoutManager(this));

        receivedMaterialsList = new ArrayList<>();
        recAdapter = new Material_Adapter(receivedMaterialsList, this, 1);
        recyclerViewRec.setAdapter(recAdapter);

        usedMaterialsList = new ArrayList<>();
        useAdapter = new Material_Adapter(usedMaterialsList, this, 2);
        recyclerViewUse.setAdapter(useAdapter);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                receivedMaterialsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MaterialsModel minfo = dataSnapshot.getValue(MaterialsModel.class);
                        minfo.setMaterialid(dataSnapshot.getKey());
                        receivedMaterialsList.add(minfo);
                    }
                    sortOrdersRec();
                    recAdapter.notifyDataSetChanged();
                    Log.d("materialData", "data received successfully");
                    Log.d("materialData", receivedMaterialsList.toString());
                } else {
                    Toast.makeText(Material_Home.this, "No records found", Toast.LENGTH_SHORT).show();
                    MaterialRecTitle.setVisibility(View.GONE);
                    MaterialUsedTitle.setVisibility(View.GONE);
                    matRecText.setVisibility(View.GONE);
                    matUsdText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        usedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usedMaterialsList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MaterialsModel muinfo = dataSnapshot.getValue(MaterialsModel.class);
                        muinfo.setMaterialid(dataSnapshot.getKey());
                        usedMaterialsList.add(muinfo);
                    }
                    sortOrdersUse();
                    useAdapter.notifyDataSetChanged();
                    Log.d("materialData", "data received successfully");
                    Log.d("materialData", receivedMaterialsList.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        receivedMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Material_Home.this, Material_Received.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

        usedMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Material_Home.this, Material_Used.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

    }

    private void sortOrdersUse() {
        Collections.sort(usedMaterialsList, new Comparator<MaterialsModel>() {
            @Override
            public int compare(MaterialsModel o1, MaterialsModel o2) {
                return o1.getMaterialid().compareToIgnoreCase(o2.getMaterialid());
            }
        });
        Collections.reverse(usedMaterialsList);
    }


    private void sortOrdersRec() {
        Collections.sort(receivedMaterialsList, new Comparator<MaterialsModel>() {
            @Override
            public int compare(MaterialsModel o1, MaterialsModel o2) {
                return o1.getMaterialid().compareToIgnoreCase(o2.getMaterialid());
            }
        });

        Collections.reverse(receivedMaterialsList);
    }


}