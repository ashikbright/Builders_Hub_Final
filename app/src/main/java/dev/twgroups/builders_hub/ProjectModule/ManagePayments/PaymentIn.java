package dev.twgroups.builders_hub.ProjectModule.ManagePayments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import dev.twgroups.builders_hub.Models.InPayment;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class PaymentIn extends AppCompatActivity {
    private String projectID;
    EditText edtAmountReceived;
    EditText edtDescription;
    EditText edtDate;
    ArrayList<InPayment> inlLst;
    Spinner spinner;
    Button InSubmit;
    DatabaseReference databaseReference;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_in);

        edtAmountReceived = findViewById(R.id.edtAmtRcvd);
        edtDescription = findViewById(R.id.edtINDesc);
        edtDate = findViewById(R.id.edtINDate);
        spinner = findViewById(R.id.spinnerpin);
        InSubmit = findViewById(R.id.btnINSubmit);
        inlLst = new ArrayList<>();
        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        checkNetworkConnection connection = new checkNetworkConnection(this);

        imageButton = findViewById(R.id.btn_back_list_order);
        imageButton.setOnClickListener(v -> finish());

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("PaymentInfo").child("IN");

        setDate();

        InSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payIN();
            }
        });

    }

    private void setDate() {

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String date = day + "/" + month + "/" + year;
        edtDate.setText(date);
        edtDate.setEnabled(false);
    }

    private void payIN() {
        String amountRcvd = edtAmountReceived.getText().toString();
        String descriptionIN = edtDescription.getText().toString();
        String inDate = edtDate.getText().toString();
        String categories_list = spinner.getSelectedItem().toString();

        if (amountRcvd.isEmpty()) {
            edtAmountReceived.setError("Required!");
            edtAmountReceived.requestFocus();
            return;
        }
        if (descriptionIN.isEmpty()) {
            edtDescription.setError("Required!");
            edtDescription.requestFocus();
            return;
        }

        if (categories_list.equals("Select Category")) {
            spinner.requestFocus();
            Toast.makeText(PaymentIn.this, "Select any one Category", Toast.LENGTH_SHORT).show();
            return;
        }

        InPayment inPayment = new InPayment(amountRcvd, descriptionIN, inDate, categories_list);
        databaseReference.push().setValue(inPayment);
        Toast.makeText(this, "Payment Received ...", Toast.LENGTH_SHORT).show();

        edtAmountReceived.getText().clear();
        edtDescription.getText().clear();
        edtDate.getText().clear();

        finish();

    }
}