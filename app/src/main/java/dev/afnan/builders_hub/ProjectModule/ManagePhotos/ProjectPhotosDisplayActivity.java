package dev.afnan.builders_hub.ProjectModule.ManagePhotos;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import dev.afnan.builders_hub.R;

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

import dev.afnan.builders_hub.Models.UploadImage;
import dev.afnan.builders_hub.ViewHolder.ImageAdapter;
import dev.afnan.builders_hub.utility.checkNetworkConnection;

public class ProjectPhotosDisplayActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ArrayList<UploadImage> list;

    private ImageAdapter adapter;
    private FirebaseStorage mStorage;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Image");
    private ValueEventListener mDBListener;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_photos_display);

        checkNetworkConnection connection = new checkNetworkConnection(this);

        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        adapter = new ImageAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListner(ProjectPhotosDisplayActivity.this);
        imageButton = findViewById(R.id.btn_back_list_order);
        imageButton.setOnClickListener(v -> finish());

        mStorage = FirebaseStorage.getInstance();
        mDBListener = root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UploadImage uploadImage = dataSnapshot.getValue(UploadImage.class);
                    uploadImage.setKey(dataSnapshot.getKey());
                    list.add(uploadImage);
                }
                sortOrders();
                adapter.notifyDataSetChanged();
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

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                root.child(selectedKey).removeValue();
                Toast.makeText(ProjectPhotosDisplayActivity.this, "Image Deleted Successfully", Toast.LENGTH_SHORT).show();
            }
        });

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
        root.removeEventListener(mDBListener);
    }
}