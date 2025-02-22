package dev.twgroups.builders_hub.UserModule;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import dev.twgroups.builders_hub.Common.Common;
import dev.twgroups.builders_hub.Models.Order;
import dev.twgroups.builders_hub.Models.UserProfile;
import dev.twgroups.builders_hub.NotificationService.FcmNotificationsSender;
import dev.twgroups.builders_hub.NotificationService.SharedPrefManager;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class ConfirmOrder extends AppCompatActivity {
    EditText editTotalWorkers;
    EditText editNoDays;
    EditText editLocation;
    Button placeOrderButton, btnHome, Status;
    Spinner spinner;
    FirebaseDatabase database;
    DatabaseReference orderReference;
    DatabaseReference requestReference;
    FirebaseAuth mAuth;
    public int counter = 1;
    Dialog confirmDialog;
    private String title;
    private String message;
    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        editTotalWorkers = findViewById(R.id.edtnWorkers);
        editNoDays = findViewById(R.id.edtNDays);
        editLocation = findViewById(R.id.edtLocation);
        placeOrderButton = findViewById(R.id.BtnSave);
        spinner = findViewById(R.id.wtype);
        mAuth = FirebaseAuth.getInstance();

        checkNetworkConnection connection = new checkNetworkConnection(this);

        database = FirebaseDatabase.getInstance();
        requestReference = database.getReference().child("Orders");
        orderReference = requestReference.child(FirebaseAuth.getInstance().getUid());

        String[] workerType = getResources().getStringArray(R.array.worker_type);

        Intent mIntent = getIntent();
        int selectedItem = mIntent.getIntExtra("itemSelected", 0);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter =
                new ArrayAdapter<String>(this, R.layout.worker_type_spinner_layout, workerType);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attaching data adapter to spinner.
        spinner.setAdapter(dataAdapter);
        spinner.setSelection(selectedItem);


        orderReference.child("orderRequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("ColumnExist", "column found");
                    int count = (int) snapshot.getChildrenCount();
                    if (count == 0) {
                        counter = 1;
                    } else {
                        counter = count + 1;
                    }
                    Log.d("ColumnExist", "count : " + count);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNetworkConnection connection = new checkNetworkConnection(getApplicationContext());

                Boolean res = checkoutData();
                if (res) {
                    editTotalWorkers.getText().clear();
                    editNoDays.getText().clear();
                    editLocation.getText().clear();

                    showSuccessDialog();
                    editLocation.clearFocus();
                }
            }
        });


    }

    private void showSuccessDialog() {
        confirmDialog = new Dialog(ConfirmOrder.this);
        confirmDialog.setContentView(R.layout.confirm_dialog);
        confirmDialog.setCancelable(false); //Optional

        btnHome = confirmDialog.findViewById(R.id.btn_goto_home);

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                finish();
            }
        });
        confirmDialog.show();

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                confirmDialog.dismiss();
            }
        }, 3000);
    }


    private Boolean checkoutData() {

        String worker_type = spinner.getSelectedItem().toString();
        String totalWorkers = editTotalWorkers.getText().toString();
        String totalDays = editNoDays.getText().toString();
        String address = editLocation.getText().toString();
        String orderId = String.valueOf(System.currentTimeMillis());
        String date = getDate();


        if (totalWorkers.isEmpty()) {
            editTotalWorkers.setError("Required!");
            editTotalWorkers.requestFocus();
            return false;
        }

        try {
            if (Integer.parseInt(totalWorkers) <= 0) {
                editTotalWorkers.setError("please enter at least 1");
                editTotalWorkers.requestFocus();
                return false;
            }
            if (Integer.parseInt(totalWorkers) > 20) {
                editTotalWorkers.setError("Not more than 20 workers!");
                editTotalWorkers.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editTotalWorkers.setError("Please enter positive numbers only!");
            editTotalWorkers.requestFocus();
            return false;
        }


        if (totalDays.isEmpty()) {
            editNoDays.setError("Required!");
            editNoDays.requestFocus();
            return false;
        }

        try {
            if (Integer.parseInt(totalDays) <= 0) {
                editNoDays.setError("please enter at least 1");
                editNoDays.requestFocus();
                return false;
            }

            if (Integer.parseInt(totalDays) > 30) {
                editNoDays.setError("Maximum limit is 30 days!");
                editNoDays.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            editNoDays.setError("Please enter positive numbers only!");
            editNoDays.requestFocus();
            return false;
        }


        if (address.isEmpty()) {
            editLocation.setError("Required!");
            editLocation.requestFocus();
            return false;
        }

        if (!address.matches("^[a-zA-Z _,.]+")) {
            editLocation.setError("Enter a valid address");
            editLocation.requestFocus();
            return false;
        }


        UserProfile request = new UserProfile(
                Common.CurrentUser.getName(),
                Common.CurrentUser.getEmail(),
                Common.CurrentUser.getPhone(),
                mAuth.getUid()
        );


        Order workInfo = new Order(
                orderId,
                worker_type,
                totalWorkers,
                totalDays,
                address,
                date
        );

        orderReference.child("userInfo").setValue(request);

        DatabaseReference currentOrder = orderReference.child("orderRequests").child(String.valueOf(counter));
        currentOrder.setValue(workInfo);

        initNotificationData();
        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/admin", title,
                message, getApplicationContext(), ConfirmOrder.this);

        notificationsSender.SendNotifications();

        return true;
    }

    private void initNotificationData() {
        title = "New Order Request";
        message = "new order from " + Common.CurrentUser.getName() + " waiting for you!";
        token = SharedPrefManager.getInstance(this).getDeviceToken();
//        token = Common.adminToken;
        Log.d("Order", "token is : " + token);
    }

    private String getDate() {
        Date c = Calendar.getInstance().getTime();
        Log.d("currentTime", "Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        return formattedDate;
    }

}