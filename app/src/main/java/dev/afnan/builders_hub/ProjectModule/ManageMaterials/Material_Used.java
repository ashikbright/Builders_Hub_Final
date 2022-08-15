package dev.afnan.builders_hub.ProjectModule.ManageMaterials;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import dev.afnan.builders_hub.R;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import dev.afnan.builders_hub.Models.MaterialsModel;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class Material_Used extends AppCompatActivity {
    private ImageButton imageButton;
    private Button saveused;
    private Spinner material;
    private EditText dateused, quantity;
    private DatabaseReference databaseReference;
    private int maxid = 0;
    private MaterialsModel materialsModel = new MaterialsModel();
    private String projectID;
    private ArrayList<MaterialsModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_used);

        imageButton = findViewById(R.id.btn_back_list_order);
        material = findViewById(R.id.spinnerused);
        dateused = findViewById(R.id.edDateused);
        quantity = findViewById(R.id.edQuantityused);
        saveused = findViewById(R.id.btn_save_used);
        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        list = new ArrayList<>();

        checkNetworkConnection connection = new checkNetworkConnection(this);

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("MaterialInfo").child("UsedInfo");

        imageButton.setOnClickListener(v -> finish());

        setDate();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    maxid = (int) snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qty = quantity.getText().toString();
                String usedDate = dateused.getText().toString();
                String usedMaterial = material.getSelectedItem().toString();

                if (qty.isEmpty()) {
                    quantity.setError("Required!");
                    quantity.requestFocus();
                    return;
                }
                if (usedDate.isEmpty()) {
                    dateused.setError("Required!");
                    dateused.requestFocus();
                    return;
                }

                if (usedMaterial.equals("Select Material")) {
                    material.requestFocus();
                    Toast.makeText(Material_Used.this, "Select any one Material", Toast.LENGTH_SHORT).show();
                    return;
                }

                materialsModel.setDate(usedDate);
                materialsModel.setMaterial(usedMaterial);
                materialsModel.setQuantity(qty);
                databaseReference.child(String.valueOf(maxid + 1)).setValue(materialsModel);
                Toast.makeText(Material_Used.this, "Data saved Successfully", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.materialsModel, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        material.setAdapter(arrayAdapter);

    }

    private void setDate() {

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + "/" + month + "/" + year;
        dateused.setText(date);
        dateused.setEnabled(false);
    }
}