package dev.twgroups.builders_hub.UserModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.twgroups.builders_hub.Common.Common;
import dev.twgroups.builders_hub.ProjectModule.ManagePhotos.ProjectPhotosDisplayActivity;
import dev.twgroups.builders_hub.R;
import dev.twgroups.builders_hub.ViewHolder.ProfileListAdapter;
import dev.twgroups.builders_hub.auth.loginActivity;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

import static android.content.Context.MODE_PRIVATE;

//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String name, phone, email;
    private ListView listView;
    private CircleImageView userImage;
    private FirebaseStorage storage;
    private Uri imageURI;
    private StorageReference storageReference;
    private EditText editName, editPhone, editEmail;
    private Button create_project;
    private TextView closeButton;
    ProfileListAdapter listAdapter;
    private DatabaseReference userRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        progressBar = view.findViewById(R.id.user_progressBar);
        listView = view.findViewById(R.id.user_profile_list_view);
        userImage = view.findViewById(R.id.user_imageView);

        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        checkNetworkConnection connection = new checkNetworkConnection(getActivity());

        storageReference = storage.getReference().child("Users/" + FirebaseAuth.getInstance().getUid() + "/profile.jpg");
        progressBar.setVisibility(View.VISIBLE);

        loadProfileImageFromFirebase();


        try {
            SharedPreferences sp = requireActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
            name = sp.getString("name", name);
            phone = sp.getString("phone", phone);
            email = sp.getString("email", email);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        displayList(name, phone, email);
        if (name != null && phone != null && email != null) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.VISIBLE);


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 33);

            }
        });

        progressBar.setVisibility(View.GONE);

        Handler handler = new Handler();
        handler.postDelayed(() -> progressBar.setVisibility(View.GONE), 5000);

        return view;
    }

    public void displayList(String name, String phone, String email) {

        int[] imageIDs = {
                R.drawable.profile_icon, R.drawable.mobile_icon, R.drawable.email_icon,
                R.drawable.ic_baseline_info_24, R.drawable.our_works_icon, R.drawable.share_icon, R.drawable.logout_icon
        };

        String[] itemNames = {
                "Name", "Mobile", "Email", "About Us", "Our Works", "Refer a Friend", "LOG OUT"
        };

        String[] data = {
                name, phone, email, " ", " ", " ", " "
        };

        listAdapter = new ProfileListAdapter(getActivity(), imageIDs, itemNames, data);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int selectedItemPosition = position + 1;
            switch (selectedItemPosition) {
                case 1:
                    Log.d("profile ", "name : " + name);
                    showDialog(name, phone, email);
                    break;

                case 2:
                    Log.d("profile ", "phone : " + phone);
                    showDialog(name, phone, email);
                    break;

                case 3:
                    Log.d("profile ", "email : " + email);
                    showDialog(name, phone, email);
                    break;

                case 4:
                    Intent mIntent = new Intent(getActivity(), AboutUs.class);
                    startActivity(mIntent);
                    break;

                case 5:
                    Intent in = new Intent(getActivity(), ProjectPhotosDisplayActivity.class);
                    startActivity(in);
                    break;

                case 6:
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String body = "Download Builder Hub now!/n {appLink}";
                    String sub = "Share with your friends";
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                    myIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(myIntent, "Share Using"));
                    break;

                case 7:

                    new AlertDialog.Builder(getActivity())
                            .setIcon(R.drawable.warning_icon)
                            .setTitle("LOGOUT")
                            .setMessage("Do you really want to log out?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                progressBar.setVisibility(View.VISIBLE);

                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    String userID = user.getUid();
                                    Log.d("userID", "id is : " + userID);
                                    FirebaseMessaging.getInstance().unsubscribeFromTopic(userID);
                                } else {
                                    Log.d("userID", "id is : null");
                                }

                                mAuth.signOut();
                                progressBar.setVisibility(View.GONE);

                                Intent intent = new Intent(getActivity(), loginActivity.class);
                                startActivity(intent);

                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }


                                requireActivity().finish();
                            })
                            .setNegativeButton("No", null)
                            .show();
                    break;

            }

        });

    }


    private void showDialog(String name, String phone, String email) {

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.edit_profile, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        editName = dialogView.findViewById(R.id.p_name);
        editPhone = dialogView.findViewById(R.id.p_phone);
        editEmail = dialogView.findViewById(R.id.p_email);

        editName.setText(name);
        editPhone.setText(phone);
        editEmail.setText(email);

        editPhone.setEnabled(false);
        editEmail.setEnabled(false);

        editName.setSelection(editName.getText().length());

        create_project = dialogView.findViewById(R.id.btn_create);
        closeButton = dialogView.findViewById(R.id.btn_close);

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });


        create_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertToFirebase(alertDialog);
            }
        });


    }


    private void insertToFirebase(AlertDialog alertDialog) {

        String Name = editName.getText().toString();

        String UID = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);

        if (Name.isEmpty()) {
            editName.setError("cannot be blank");
            editName.requestFocus();
            return;
        }

        if (!Name.matches("^[a-zA-Z _,.]+")) {
            editName.setError("Enter a valid Name");
            editName.requestFocus();
            return;
        }


        Map<String, Object> updates = new HashMap<String, Object>();
        updates.put("name", Name);

        userRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getActivity(), "Name updated successfully.", Toast.LENGTH_SHORT).show();
                    updateName(Name);
                } else {
                    Toast.makeText(getActivity(), "Failed to  update!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.dismiss();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                imageURI = data.getData();
                Common.userProfileImage = imageURI;

                userImage.setImageURI(imageURI);
                uploadImageToFirebase(imageURI);
            }
        }

    }

    private void uploadImageToFirebase(Uri imageURI) {

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Uploading...");
        dialog.show();

        if (imageURI != null) {

            storageReference = storage.getReference().child("Users/" + FirebaseAuth.getInstance().getUid() + "/profile.jpg");


            storageReference.putFile(imageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("imageUpload", "success");
                    Toast.makeText(getActivity(), "Profile Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    loadProfileImageFromFirebase();
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("imageUpload", e.toString());
                    Toast.makeText(getActivity(), "error uploading photo", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

        }

    }

    private void loadProfileImageFromFirebase() {

        storageReference = storage.getReference().child("Users/" + FirebaseAuth.getInstance().getUid() + "/profile.jpg");

        ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Please Wait");
        dialog.setMessage("Loading...");
        dialog.show();

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Glide
                            .with(requireActivity())
                            .load(uri)
                            .placeholder(R.drawable.user_profile_progress_bar)
                            .into(userImage);
                } catch (Exception e) {
                    Log.d("GlideError", "something went wrong!" + e.toString());
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("storageReferenceError", "Failed to load image!");
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(dialog::dismiss, 1300);


    }


    private void updateName(String newName) {

        SharedPreferences.Editor editor = requireActivity().getSharedPreferences("userInfo", MODE_PRIVATE).edit();
        editor.putString("name", newName);
        editor.putString("phone", phone);
        editor.putString("email", email);
        editor.apply();

        SharedPreferences sp = requireActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        name = sp.getString("name", newName);

        Log.d("profileName", "name is " + name);
        Log.d("profileName", "phone is " + phone);
        Log.d("profileName", "email is " + email);

        listAdapter.notifyDataSetChanged();
        displayList(newName, phone, email);

    }


}