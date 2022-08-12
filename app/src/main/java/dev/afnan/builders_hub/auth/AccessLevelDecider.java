package dev.afnan.builders_hub.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.afnan.builders_hub.AdminModule.AdminActivity;
import dev.afnan.builders_hub.R;
import dev.afnan.builders_hub.UserModule.UserActivity;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class AccessLevelDecider extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private String userID;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_level_decider);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();
        progressBar = findViewById(R.id.access_level_progress_bar);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        Intent intent = getIntent();
        boolean LogStatus = intent.getBooleanExtra("statusCode", false);

        if (LogStatus) {
            userID = intent.getStringExtra("uid");
        } else {
            if (user != null) {
                userID = user.getUid();
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUserAccessLevel(userID);
            }
        }, 10);

    }


    private void checkUserAccessLevel(String uid) {

        myRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child("isAdmin").exists()) {
                    Intent intent = new Intent(AccessLevelDecider.this, AdminActivity.class);
                    startActivity(intent);
//                    Toast.makeText(AccessLevelDecider.this, "ADMIN", Toast.LENGTH_SHORT).show();
                    finish();
                }

//                else if (snapshot.child("isWorker").exists()){
//                    Intent intent = new Intent(AccessLevelDecider.this, UserActivity.class);
//                    startActivity(intent);
//                    Toast.makeText(AccessLevelDecider.this, "Worker", Toast.LENGTH_SHORT).show();
//                    finish();
//                }

                else if (snapshot.child("isUser").exists()) {
                    Intent intent = new Intent(AccessLevelDecider.this, UserActivity.class);
                    startActivity(intent);
//                    Toast.makeText(AccessLevelDecider.this, "USER", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AccessLevelDecider.this, "Something went wrong\n" + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }

}