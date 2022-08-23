package dev.twgroups.builders_hub.ProjectModule.ManageMaterials;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import dev.twgroups.builders_hub.Models.MaterialsModel;
import dev.twgroups.builders_hub.Models.OutPayment;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class Material_Received extends AppCompatActivity {
    private EditText editPartyName, editRecDate, editQuantity, editUnitRate;
    private Spinner SpinnerMaterial;
    private TextView txtAmount;
    private Button save;
    private ImageButton backButton;
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

        editPartyName = findViewById(R.id.edParty_name);
        editRecDate = findViewById(R.id.edDate);
        SpinnerMaterial = findViewById(R.id.spinner);
        editQuantity = findViewById(R.id.edQuantity);
        editUnitRate = findViewById(R.id.edUnitrate);
        txtAmount = findViewById(R.id.tvAmount);
        save = findViewById(R.id.btn_save);
        backButton = findViewById(R.id.btn_back_list_order);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        list = new ArrayList<>();

        backButton.setOnClickListener(v -> finish());

        checkNetworkConnection connection = new checkNetworkConnection(this);

        setDate();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editQuantity.getText().toString().equals("") && !editUnitRate.getText().toString().equals("")) {
                    int templ = Integer.parseInt(editQuantity.getText().toString());
                    int temp2 = Integer.parseInt(editUnitRate.getText().toString());
                    txtAmount.setText(String.valueOf(templ * temp2));
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        editQuantity.addTextChangedListener(textWatcher);
        editUnitRate.addTextChangedListener(textWatcher);

        modelclass = new MaterialsModel();

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("MaterialInfo").child("ReceivedInfo");

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

                String partyName = editPartyName.getText().toString();
                String purchasedDate = editRecDate.getText().toString();
                String purchasedMaterial = SpinnerMaterial.getSelectedItem().toString();
                String purchasedQuantity = editQuantity.getText().toString();
                String unitRate = editUnitRate.getText().toString();


                if (partyName.isEmpty()) {
                    editPartyName.setError("Required!");
                    editPartyName.requestFocus();
                    return;
                }

                if (purchasedQuantity.isEmpty()) {
                    editQuantity.setError("Required!");
                    editQuantity.requestFocus();
                    return;
                }

                try {
                    if (Integer.parseInt(purchasedQuantity) <= 0) {
                        editQuantity.setError("please enter at least 1");
                        editQuantity.requestFocus();
                        return;
                    }

                } catch (NumberFormatException e) {
                    editQuantity.setError("Please enter a valid quantity!");
                    editQuantity.requestFocus();
                    return;
                }

                if (unitRate.isEmpty()) {
                    editUnitRate.setError("Required!");
                    editUnitRate.requestFocus();
                    return;
                }

                try {
                    if (Integer.parseInt(unitRate) <= 0) {
                        editUnitRate.setError("please enter at least 1");
                        editUnitRate.requestFocus();
                        return;
                    }

                } catch (NumberFormatException e) {
                    editUnitRate.setError("Please enter a valid amount!");
                    editUnitRate.requestFocus();
                    return;
                }

                modelclass.setParty(partyName);
                modelclass.setDate(purchasedDate);
                modelclass.setMaterial(purchasedMaterial);

                if (modelclass.getMaterial().equals("Select Material")) {
                    SpinnerMaterial.requestFocus();
                    Toast.makeText(Material_Received.this, "Select any one Material", Toast.LENGTH_SHORT).show();
                    return;
                }

                modelclass.setQuantity(purchasedQuantity);
                modelclass.setRate(unitRate);
                modelclass.setAmount(txtAmount.getText().toString());

                databaseReference.child(String.valueOf(maxid + 1)).setValue(modelclass);
                Toast.makeText(Material_Received.this, "Data Saved Successfully", Toast.LENGTH_LONG).show();

                updatePayments(txtAmount.getText().toString(), purchasedMaterial, purchasedDate);

                finish();
            }
        });

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this, R.array.materialsModel, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerMaterial.setAdapter(arrayAdapter);

    }

    private void setDate() {

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + "/" + month + "/" + year;
        editRecDate.setText(date);
        editRecDate.setEnabled(false);
    }

    private void updatePayments(String totalAmt, String usedMaterial, String purchasedDate) {

        DatabaseReference payOutRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("PaymentInfo").child("OUT");

        String descriptionOUT;

        descriptionOUT = usedMaterial + " purchased.";

        OutPayment outPayment = new OutPayment(totalAmt, descriptionOUT, purchasedDate, usedMaterial);
        payOutRef.push().setValue(outPayment);

    }


}