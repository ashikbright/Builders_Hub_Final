package dev.twgroups.builders_hub.AdminModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import dev.twgroups.builders_hub.R;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.twgroups.builders_hub.Common.Common;
import dev.twgroups.builders_hub.ViewHolder.ProfileListAdapter;
import dev.twgroups.builders_hub.WorkerModule.WorkerDetailsHome;
import dev.twgroups.builders_hub.auth.loginActivity;
import dev.twgroups.builders_hub.utility.checkNetworkConnection;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String name, phone, email;
    private ListView listView;
    private CircleImageView userImage;
    private FirebaseStorage storage;
    private Uri imageURI;
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment//
        View view = inflater.inflate(R.layout.fragment_profile_admin, container, false);
        progressBar = view.findViewById(R.id.user_progressBar);
        listView = view.findViewById(R.id.user_profile_list_view);
        mAuth = FirebaseAuth.getInstance();
        userImage = view.findViewById(R.id.user_imageView);
        storage = FirebaseStorage.getInstance();

        checkNetworkConnection connection = new checkNetworkConnection(getActivity());

        storageReference = storage.getReference().child("Admin/" + FirebaseAuth.getInstance().getUid() + "/profile.jpg");
        progressBar.setVisibility(View.VISIBLE);
        loadProfileImageFromFirebase();
        try {
            SharedPreferences sp = requireActivity().getSharedPreferences("adminInfo", MODE_PRIVATE);
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


        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 33);

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                progressBar.setVisibility(View.GONE);
            }
        }, 5000);

        return view;
    }


    public void displayList(String name, String phone, String email) {

        int[] imageIDs = {
                R.drawable.profile_icon, R.drawable.mobile_icon, R.drawable.email_icon, R.drawable.worker_icon,
                R.drawable.share_icon, R.drawable.start_icon, R.drawable.logout_icon
        };

        String[] itemNames = {
                "Name", "Mobile", "Email", "Worker", "Refer a Friend", "Rate Builders Hub", "LOG OUT"
        };

        String[] data = {
                name, phone, email, " ", " ", " ", " "
        };

        ProfileListAdapter profileListAdapter = new ProfileListAdapter(getActivity(), imageIDs, itemNames, data);
        listView.setAdapter(profileListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int selectedItemPosition = position + 1;

                switch (selectedItemPosition) {
                    case 1:
                        Log.d("listClick", "Name");
                        break;

                    case 2:
                        Log.d("listClick", "Phone");
                        break;

                    case 3:
                        Log.d("listClick", "Email");
                        break;

                    case 4:
                        Intent intent = new Intent(getActivity(), WorkerDetailsHome.class);
                        startActivity(intent);
                        break;

                    case 5:
                        Intent myIntent = new Intent(Intent.ACTION_SEND);
                        myIntent.setType("text/plain");
                        String body = "Install the app now!";
                        String sub = "Share with your friends";
                        myIntent.putExtra(Intent.EXTRA_SUBJECT, sub);
                        myIntent.putExtra(Intent.EXTRA_TEXT, body);
                        startActivity(Intent.createChooser(myIntent, "Share Using"));
                        break;

                    case 6:
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.ic_dialog_alert)
                                .setTitle("Rate this app")
                                .setMessage(R.string.rate_dialog_message)
                                .setIcon(R.drawable.ic_dialog_alert)
                                .setPositiveButton("Rate It Now", (dialog, which) -> {

                                })
                                .setNeutralButton("Remind Me Later", (dialog, which) -> {

                                })
                                .setNegativeButton("No, Thanks", (dialog, which) -> {

                                })
                                .show();
                        break;

                    case 7:

                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.ic_dialog_alert)
                                .setTitle("LOGOUT")
                                .setMessage("Do you really want to log out?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressBar.setVisibility(View.VISIBLE);
                                        FirebaseMessaging.getInstance().unsubscribeFromTopic("admin");      //removing subscription for admin.
                                        mAuth.signOut();
                                        progressBar.setVisibility(View.GONE);

                                        Intent intent = new Intent(getActivity(), loginActivity.class);
                                        startActivity(intent);

                                        FragmentManager fm = getActivity().getSupportFragmentManager();         //removing everything from the backstack
                                        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                            fm.popBackStack();
                                        }


                                        requireActivity().finish();
                                    }
                                })
                                .setNegativeButton("No", null)
                                .show();
                        break;

                }
            }
        });


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

            storageReference = storage.getReference().child("Admin/" + FirebaseAuth.getInstance().getUid() + "/profile.jpg");


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

        storageReference = storage.getReference().child("Admin/" + FirebaseAuth.getInstance().getUid() + "/profile.jpg");

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
}