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
                String useddate = dateused.getText().toString();
                String usedmaterial = material.getSelectedItem().toString();

                if (qty.isEmpty()) {
                    quantity.setError("Required!");
                    quantity.requestFocus();
                    return;
                }
                if (useddate.isEmpty()) {
                    dateused.setError("Required!");
                    dateused.requestFocus();
                    return;
                }

                materialsModel.setDate(dateused.getText().toString());
                materialsModel.setMaterial(material.getSelectedItem().toString());

                if (materialsModel.getMaterial().equals("Select Material")) {
                    material.requestFocus();
                    Toast.makeText(Material_Used.this, "Select any one Material", Toast.LENGTH_SHORT).show();
                    return;
                }

                materialsModel.setQuantity(quantity.getText().toString());
                databaseReference.child(String.valueOf(maxid + 1)).setValue(materialsModel);
                Toast.makeText(Material_Used.this, "Data saved Successfully", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.materialsModel, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        material.setAdapter(arrayAdapter);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateCalendar();
            }

            private void updateCalendar() {
                String Format = "MM/dd/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(Format, Locale.US);

                dateused.setText(sdf.format(calendar.getTime()));
            }
        };

        dateused.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Material_Used.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }
}