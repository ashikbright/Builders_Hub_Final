package dev.twgroups.builders_hub.ProjectModule.ManagePhotos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dev.twgroups.builders_hub.Common.Common;
import dev.twgroups.builders_hub.Models.UploadImage;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.ViewHolder.ImageAdapter;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

public class ProjectPhotosDisplayActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ArrayList<UploadImage> list;

    private ImageAdapter adapter;
    private FirebaseStorage mStorage;
    private DatabaseReference imageRef;
    private ValueEventListener mDBListener;
    private ImageButton imageButton;
    private int statusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_photos_display);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        imageRef = FirebaseDatabase.getInstance().getReference("Image");
        Intent mIntent = getIntent();
        statusCode = mIntent.getIntExtra("statusCode", 0);


        adapter = new ImageAdapter(this, list, statusCode);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(ProjectPhotosDisplayActivity.this);
        imageButton = findViewById(R.id.btn_back_list_order);
        imageButton.setOnClickListener(v -> finish());

        mStorage = FirebaseStorage.getInstance();
        mDBListener = imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UploadImage uploadImage = dataSnapshot.getValue(UploadImage.class);
                    if (uploadImage != null) {
                        uploadImage.setKey(dataSnapshot.getKey());
                        list.add(uploadImage);
                        sortOrders();
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ProjectPhotosDisplayActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProjectPhotosDisplayActivity.this, "Failed To Load!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onDeleteClick(int position) {
        UploadImage selectedItem = list.get(position);
        final String selectedKey = selectedItem.getKey();
        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Alert!!!");
        dialog.setMessage("Are you sure you want to delete? \nOnce deleted cannot be reverted.");
        dialog.setIcon(R.drawable.ic_dialog_alert);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        ProjectPhotosDisplayActivity.this.imageRef.child(selectedKey).removeValue();
                        Toast.makeText(ProjectPhotosDisplayActivity.this, "Image Deleted Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("selectedItem", "operation cancelled.");
            }
        });

        dialog.create().show();

    }

    private void sortOrders() {
        Collections.sort(list, new Comparator<UploadImage>() {
            @Override
            public int compare(UploadImage u1, UploadImage u2) {
                return u1.getKey().compareToIgnoreCase(u2.getKey());
            }
        });
        Collections.reverse(list);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageRef.removeEventListener(mDBListener);
    }
}