package dev.afnan.builders_hub.ProjectModule.ManageMaterials;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
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

public class Material_Received extends AppCompatActivity {
    private EditText partyname, date, quantity, urate;
    private Spinner material;
    private TextView amount;
    private Button save;
    private ImageButton imageButton;
    private Calendar calendar;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private int maxid = 0;
    private MaterialsModel modelclass;
    private String projectID;
    private ArrayList<MaterialsModel> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_received);

        partyname = findViewById(R.id.edParty_name);
        date = findViewById(R.id.edDate);
        material = findViewById(R.id.spinner);
        quantity = findViewById(R.id.edQuantity);
        urate = findViewById(R.id.edUnitrate);
        amount = findViewById(R.id.tvAmount);
        save = findViewById(R.id.btn_save);
        imageButton = findViewById(R.id.btn_back_list_order);
        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        list = new ArrayList<>();

        imageButton.setOnClickListener(v -> finish());

        checkNetworkConnection connection = new checkNetworkConnection(this);


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!quantity.getText().toString().equals("") && !urate.getText().toString().equals("")) {
                    int templ = Integer.parseInt(quantity.getText().toString());
                    int temp2 = Integer.parseInt(urate.getText().toString());
                    amount.setText(String.valueOf(templ * temp2));
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        quantity.addTextChangedListener(textWatcher);
        urate.addTextChangedListener(textWatcher);

        modelclass = new MaterialsModel();

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("MaterialInfo").child("ReceivedInfo");
//        materialreference = reference.child(FirebaseAuth.getInstance().getUid());

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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String partynm = partyname.getText().toString();
                String purchaseddate = date.getText().toString();
                String purchasedmaterial = material.getSelectedItem().toString();
                String purchasedquantity = quantity.getText().toString();
                String unitrate = urate.getText().toString();
                String totalamount = amount.getText().toString();


                if (partynm.isEmpty()) {
                    partyname.setError("Required!");
                    partyname.requestFocus();
                    return;
                }

                if (purchaseddate.isEmpty()) {
                    date.setError("Required!");
                    date.requestFocus();
                    return;
                }

                if (purchasedquantity.isEmpty()) {
                    quantity.setError("Required!");
                    quantity.requestFocus();
                    return;
                }

                if (unitrate.isEmpty()) {
                    urate.setError("Required!");
                    urate.requestFocus();
                    return;
                }


                modelclass.setParty(partyname.getText().toString());
                modelclass.setDate(date.getText().toString());
                modelclass.setMaterial(material.getSelectedItem().toString());

                if (modelclass.getMaterial().equals("Select Material")) {
                    material.requestFocus();
                    Toast.makeText(Material_Received.this, "Select any one Material", Toast.LENGTH_SHORT).show();
                    return;
                }

                modelclass.setQuantity(quantity.getText().toString());
                modelclass.setRate(urate.getText().toString());
                modelclass.setAmount(amount.getText().toString());

                databaseReference.child(String.valueOf(maxid + 1)).setValue(modelclass);
                Toast.makeText(Material_Received.this, "Data Saved Successfully", Toast.LENGTH_LONG).show();
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

                date.setText(sdf.format(calendar.getTime()));
            }
        };

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Material_Received.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }


}