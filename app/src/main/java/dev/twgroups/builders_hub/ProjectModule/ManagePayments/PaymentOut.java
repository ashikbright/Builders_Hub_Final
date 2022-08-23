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

import dev.twgroups.builders_hub.Models.OutPayment;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class PaymentOut extends AppCompatActivity {
    String projectID;
    EditText edtAmountPaid;
    EditText edtDescription;
    ArrayList<OutPayment> outList;
    EditText edtDate;
    Spinner spinner;
    Button OutSubmit;
    DatabaseReference databaseReference;
    private ImageButton imageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_out);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        edtAmountPaid = findViewById(R.id.edtAmtPaid);
        edtDescription = findViewById(R.id.edtOUTDesc);
        edtDate = findViewById(R.id.edtOUTDate);
        spinner = findViewById(R.id.spinnerout);
        OutSubmit = findViewById(R.id.btnOUTSubmit);
        outList = new ArrayList<>();
        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        imageButton = findViewById(R.id.btn_back_list_order);
        imageButton.setOnClickListener(v -> finish());


        databaseReference = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("PaymentInfo").child("OUT");

        setDate();

        OutSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payOut();
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

    private void payOut() {
        String amountPaid = edtAmountPaid.getText().toString();
        String descriptionOUT = edtDescription.getText().toString();
        String outDate = edtDate.getText().toString();
        String categories_list = spinner.getSelectedItem().toString();

        if (amountPaid.isEmpty()) {
            edtAmountPaid.setError("Required!");
            edtAmountPaid.requestFocus();
            return;
        }

        try {
            if (Integer.parseInt(amountPaid) <= 0) {
                edtAmountPaid.setError("please enter at least 1");
                edtAmountPaid.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            edtAmountPaid.setError("Please enter a valid amount!");
            edtAmountPaid.requestFocus();
            return;
        }

        if (descriptionOUT.isEmpty()) {
            edtDescription.setError("Required!");
            edtDescription.requestFocus();
            return;
        }

        if (categories_list.equals("Select Category")) {
            spinner.requestFocus();
            Toast.makeText(PaymentOut.this, "Select any one Category", Toast.LENGTH_SHORT).show();
            return;
        }

        OutPayment outPayment = new OutPayment(amountPaid, descriptionOUT, outDate, categories_list);
        databaseReference.push().setValue(outPayment);
        Toast.makeText(this, "Payment Given ...", Toast.LENGTH_SHORT).show();

        edtAmountPaid.getText().clear();
        edtDescription.getText().clear();
        edtDate.getText().clear();

        finish();
    }
}