package dev.twgroups.builders_hub.ProjectModule.ManagePayments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.twgroups.builders_hub.Models.InPayment;
import dev.twgroups.builders_hub.Models.OutPayment;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.ViewHolder.paymentInAdapter;
import dev.twgroups.builders_hub.ViewHolder.paymentOutAdapter;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

import static java.lang.String.valueOf;

public class ProjectPayment extends AppCompatActivity {
    private String projectID;
    private paymentInAdapter paymentInAdapter;
    private paymentOutAdapter paymentOutAdapter;
    private ArrayList<InPayment> inList;
    private ArrayList<OutPayment> outList;
    private ValueEventListener mDBListener;
    private DatabaseReference databaseRef;
    private DatabaseReference inRef;
    private DatabaseReference outRef;
    private DatabaseReference projectRef;
    private Button btnIn, btnOut;
    private TextView totalIn, totalOut, balAmt;
    private int totalInAmt = 0;
    private int totalOutAmt = 0;
    private int balanceAmount = 0;
    private ImageButton imageButton;
    private TextView txtPayInText, txtPayOutText, txtPayInTitle, txtPayOutTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_project);

        btnIn = findViewById(R.id.btnpayin);
        btnOut = findViewById(R.id.btnpayout);
        totalIn = findViewById(R.id.txt_tot_in);
        totalOut = findViewById(R.id.txt_tot_out);
        balAmt = findViewById(R.id.txtbalamt);

        imageButton = findViewById(R.id.btn_back_list_order);
        txtPayInText = findViewById(R.id.pay_in_text);
        txtPayInTitle = findViewById(R.id.pay_in_title);
        txtPayOutText = findViewById(R.id.pay_out_text);
        txtPayOutTitle = findViewById(R.id.pay_out_title);

        imageButton.setOnClickListener(v -> finish());

        checkNetworkConnection connection = new checkNetworkConnection(this);

        Intent mIntent = getIntent();
        projectID = mIntent.getStringExtra("projectID");

        databaseRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("PaymentInfo");

        RecyclerView recyclerViewIn = findViewById(R.id.recyclerin);
        recyclerViewIn.setHasFixedSize(true);
        recyclerViewIn.setLayoutManager(new LinearLayoutManager(this));
        inList = new ArrayList<>();
        paymentInAdapter = new paymentInAdapter(this, inList);
        recyclerViewIn.setAdapter(paymentInAdapter);

        RecyclerView recyclerViewOut = findViewById(R.id.recyclerout);
        recyclerViewOut.setHasFixedSize(true);
        recyclerViewOut.setLayoutManager(new LinearLayoutManager(this));
        outList = new ArrayList<>();
        paymentOutAdapter = new paymentOutAdapter(this, outList);
        recyclerViewOut.setAdapter(paymentOutAdapter);

        databaseRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("PaymentInfo");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    Toast.makeText(ProjectPayment.this, "No records found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProjectPayment.this, PaymentIn.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

        btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectPayment.this, PaymentOut.class);
                intent.putExtra("projectID", projectID);
                startActivity(intent);
            }
        });

    }

    private void updateInAmt(String amtIN) {

        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects").child(projectID).child("ProjectInfo");

        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("amountIn", amtIN);
        projectRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("moveStatus", "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("moveStatus", "failed" + e.toString());
            }
        });

    }

    private void updateOutAmt(String amtOut) {

        projectRef = FirebaseDatabase.getInstance().getReference().child("Projects").child(projectID).child("ProjectInfo");

        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("amountOut", amtOut);
        projectRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("moveStatus", "success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("moveStatus", "failed" + e.toString());
            }
        });

    }

    private void loadRecyclerViewData() {

        databaseRef = FirebaseDatabase.getInstance().getReference("Projects").child(projectID).child("PaymentInfo");
        inRef = databaseRef.child("IN");
        outRef = databaseRef.child("OUT");
        totalInAmt = 0;
        totalOutAmt = 0;
        balanceAmount = 0;

        inRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        InPayment inPayment = dataSnapshot.getValue(InPayment.class);
                        if (inPayment != null) {
                            inPayment.setKey(dataSnapshot.getKey());
                            try {
                                int cost = Integer.parseInt(inPayment.getAmtReceived());
                                totalInAmt += cost;
                                Log.d("paymentCheck", "in payment is : " + cost);
                                totalIn.setText(new StringBuilder().append("₹").append(valueOf(totalInAmt)).toString());
                                inList.add(inPayment);

                                balanceAmount = totalInAmt - totalOutAmt;
                                balAmt.setText((new StringBuilder().append("₹").append(valueOf(balanceAmount)).toString()));

                            } catch (NumberFormatException e) {
                                Log.d("paymentCheck", "cost is null ");
                                e.printStackTrace();
                            }


                        } else {
                            Log.d("paymentCheck", "in payment is null");
                        }


                    }
                    updateInAmt(String.valueOf(totalInAmt));
                    paymentInAdapter.notifyDataSetChanged();

                    txtPayInText.setVisibility(View.VISIBLE);
                    txtPayInTitle.setVisibility(View.VISIBLE);
                    totalIn.setVisibility(View.VISIBLE);

                } else {
                    txtPayInText.setVisibility(View.GONE);
                    txtPayInTitle.setVisibility(View.GONE);
                    totalIn.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProjectPayment.this, "Failed To Load!!!", Toast.LENGTH_SHORT).show();
            }
        });

        outRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                outList.clear();
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        OutPayment outPayment = dataSnapshot.getValue(OutPayment.class);
                        if (outPayment != null) {
                            outPayment.setKey(dataSnapshot.getKey());
                            int cost = Integer.parseInt(outPayment.getAmtPaid());
                            Log.d("paymentCheck", "in payment is : " + cost);
                            totalOutAmt += cost;
                            totalOut.setText(new StringBuilder().append("₹").append(valueOf(totalOutAmt)).toString());
                            outList.add(outPayment);

                            balanceAmount = totalInAmt - totalOutAmt;
                            balAmt.setText((new StringBuilder().append("₹").append(valueOf(balanceAmount)).toString()));
                        } else {
                            Log.d("paymentCheck", "Out payment is null");
                        }


                        Log.d("paymentCheck", "balanceOUT : " + balanceAmount);

                    }

                    updateOutAmt(String.valueOf(totalOutAmt));
                    paymentOutAdapter.notifyDataSetChanged();

                    txtPayOutText.setVisibility(View.VISIBLE);
                    txtPayOutTitle.setVisibility(View.VISIBLE);
                    totalOut.setVisibility(View.VISIBLE);
                } else {
                    txtPayOutText.setVisibility(View.GONE);
                    txtPayOutTitle.setVisibility(View.GONE);
                    totalOut.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        balanceAmount = totalInAmt - totalOutAmt;
        balAmt.setText((new StringBuilder().append("₹").append(valueOf(balanceAmount)).toString()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadRecyclerViewData();
    }

}