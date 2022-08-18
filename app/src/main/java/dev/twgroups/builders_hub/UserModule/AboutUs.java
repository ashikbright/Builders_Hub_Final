package dev.twgroups.builders_hub.UserModule;

import android.os.Bundle;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import dev.twgroups.builders_hub.R;

public class AboutUs extends AppCompatActivity {

    RatingBar ratingBar;
    DatabaseReference dbRef;
    String ratingsStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);


        ratingBar = findViewById(R.id.ratingBar);

        dbRef = FirebaseDatabase.getInstance().getReference().child("Ratings");

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Map<String, Object> updates = new HashMap<String, Object>();
                updates.put("rating", String.valueOf(rating));

                dbRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(updates);
            }
        });

//        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists()) {
//                    ratingsStr = snapshot.child("rating").getValue(String.class);
//
//                    if (ratingsStr == null) {
//                        ratingsStr = "3";
//                    } else {
//                        ratingBar.setRating(Float.parseFloat(ratingsStr));
//
//                    }
//                    Log.d("rating", ratingsStr);
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }
}