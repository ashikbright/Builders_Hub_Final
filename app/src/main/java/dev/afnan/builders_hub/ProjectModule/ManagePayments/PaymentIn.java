package dev.afnan.builders_hub.ProjectModule.ManagePayments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import dev.afnan.builders_hub.R;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

import dev.afnan.builders_hub.Models.InPayment;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class PaymentIn extends AppCompatActivity {
    private String projectID;
    EditText edtAmountRecieved;
    EditText edtDescription;
    EditText edtDate;
    ArrayList<InPayment> inlist;
    Spinner spinner;
    Button InSubmit;
    DatePickerDialog.OnDateSetListener setListener;
    DatabaseReference databaseReference;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_in);

        edtAmountRecieved = findViewById(R.id.edtAmtRcvd);
        edtDescription = findViewById(R.id.edtINDesc);
        edtDate = findViewById(R.id.edtINDate);
        spinner = findViewById(R.id.spinnerpin);
        InSubmit = findViewById(R.id.btnINSubmit);
        inlist = new ArrayList<>();
        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        checkNetworkConnection connection = new checkNetworkConnection(this);

        imageButton = findViewById(R.id.btn_back_list_order);
        imageButton.setOnClickListener(v -> finish());

        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("PaymentInfo").child("IN");
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        PaymentIn.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month + 1;
                        String date = day + "/" + month + "/" + year;
                        edtDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });


        InSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payIN();
                edtAmountRecieved.getText().clear();
                edtDescription.getText().clear();
                edtDate.getText().clear();
            }
        });

    }

    private void payIN() {
        String amountrcvd = edtAmountRecieved.getText().toString();
        String descriptionIN = edtDescription.getText().toString();
        String INdate = edtDate.getText().toString();
        String categories_list = spinner.getSelectedItem().toString();

        InPayment inPayment = new InPayment(amountrcvd, descriptionIN, INdate, categories_list);
        databaseReference.push().setValue(inPayment);
        Toast.makeText(this, "Payment Recieved ...", Toast.LENGTH_SHORT).show();
    }
}