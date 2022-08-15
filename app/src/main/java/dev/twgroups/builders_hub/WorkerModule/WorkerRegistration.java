package dev.twgroups.builders_hub.WorkerModule;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import dev.twgroups.builders_hub.R;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import dev.twgroups.builders_hub.Models.Workers;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class WorkerRegistration extends AppCompatActivity {
    private TextView btnLogin, errorMsg;
    private EditText editName, editEmail, editPhone, editPassword1, editPassword2, editaddress;
    private Spinner spinner;
    private Button btnRegisterUser;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_worker);

        mAuth = FirebaseAuth.getInstance();

        checkNetworkConnection connection = new checkNetworkConnection(this);

        btnRegisterUser = findViewById(R.id.createAccount);
        editName = findViewById(R.id.name);
        spinner = findViewById(R.id.edwtypes);
        editEmail = findViewById(R.id.email);
        editPhone = findViewById(R.id.phone);
        editPassword1 = findViewById(R.id.password1);
        editPassword2 = findViewById(R.id.password2);
        progressBar = findViewById(R.id.register_progressBar);
        errorMsg = findViewById(R.id.errorMsg_register);
        editaddress = findViewById(R.id.address);
        backButton = findViewById(R.id.btn_back_workers_registration);

        Intent mIntent = getIntent();
        int selectedItem = mIntent.getIntExtra("itemSelected", 0);

        spinner.setSelection(selectedItem);

        btnRegisterUser.setOnClickListener(v -> registerUser());

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void registerUser() {
        String name = editName.getText().toString().trim();
        String workerType = spinner.getSelectedItem().toString();
        String email = editEmail.getText().toString();
        String phone = editPhone.getText().toString();
        String password1 = editPassword1.getText().toString().trim();
        String password2 = editPassword2.getText().toString().trim();
        String address = editaddress.getText().toString().trim();
        String isWorker = "1";


        if (name.isEmpty()) {
            editName.setError("Full name is required!");
            editName.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            editaddress.setError("Address is required!");
            editaddress.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editEmail.setError("Email is required!");
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editEmail.setError("Please enter a valid Email");
            editEmail.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            editPhone.setError("Phone number is required!");
            editPhone.requestFocus();
            return;
        }
        if (phone.length() != 10) {
            editPhone.setError("please enter a valid phone number");
            editPhone.requestFocus();
            return;
        }

        if (password1.isEmpty()) {
            editPassword1.setError("Password is required!");
            editPassword1.requestFocus();
            return;
        }
        if (password1.length() < 6) {
            editPassword1.setError("Password must be at least 6 character");
            editPassword1.requestFocus();
            return;
        }
        if (password2.isEmpty()) {
            errorMsg.setVisibility(View.VISIBLE);
            errorMsg.setText("Confirm your password");
            editPassword2.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_error_bg));
            editPassword2.requestFocus();
            return;
        }
        if (!password1.equals(password2)) {
            errorMsg.setText("Password does not match!");
            editPassword2.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_error_bg));
            editPassword2.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        editPassword2.setBackground(ContextCompat.getDrawable(this, R.drawable.edittext_bg));
        errorMsg.setVisibility(View.GONE);
        String password = password1;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            Workers workers = new Workers(name, workerType, email, phone, isWorker, address);
                            String workerID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference("Workers")
                                    .child(spinner.getSelectedItem().toString())
                                    .child(workerID)
                                    .setValue(workers).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(WorkerRegistration.this, "Worker has registered successfully", Toast.LENGTH_SHORT).show();

                                        addWorkerID(workerID, spinner.getSelectedItem().toString());

                                        Intent intent = new Intent(WorkerRegistration.this, WorkerDetailsHome.class);   //start activity workerRegistration
                                        startActivity(intent);
                                        finish();
                                        progressBar.setVisibility(View.VISIBLE);
                                    } else {
                                        Toast.makeText(WorkerRegistration.this, "Failed to register! try again.", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(WorkerRegistration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                        } else {
                            Toast.makeText(WorkerRegistration.this, "Failed to register! try again.", Toast.LENGTH_SHORT).show();
                            Toast.makeText(WorkerRegistration.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });


    }

    private void addWorkerID(String workerID, String selectedWorker) {

        DatabaseReference workerRef = FirebaseDatabase.getInstance().getReference().child("Workers").child(selectedWorker).child(workerID);

        workerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                Workers workers = snapshot.getValue(Workers.class);
                if (workers != null) {
                    if (key != null) {
                        Map<String, Object> updates = new HashMap<String, Object>();
                        updates.put("workerID", key);
                        workerRef.updateChildren(updates);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}