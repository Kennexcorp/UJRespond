package com.example.kennexcorp.ujrespond.activitiy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kennexcorp.ujrespond.R;
import com.example.kennexcorp.ujrespond.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {

    private User userProfile;
    private String uid;
    private ImageView profileAvatar;
    private TextView profileName;
    private TextInputEditText matriculationNumber;
    private TextInputEditText firstSOSNumber;
    private TextInputEditText secondSOSNumber;
    private TextInputEditText department;
    private TextInputEditText faculty;
    private TextInputEditText address;
    private TextInputEditText otherDetail;
    //private ProgressDialog progressDialog;
    private static final int GALLERY_REQUEST = 1;
    private Uri profileUri;
    private SharedPreferences prefs;
    private UploadTask uploadTask;
    private Uri imageUri;
    private String profileImg;

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    DatabaseReference profileReference;
    StorageReference mStorageReference;

    private static final int SELECT_PHOTO = 123;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

     /*   prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (isFirstRun()) {
            ActivityCompat.requestPermissions(this, PERMS, GALLERY_REQUEST);
        }*/
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("User_Profile");

        uid = mAuth.getCurrentUser().getUid();
        Log.e("user id", uid);
        profileReference = databaseReference.child(uid);
        profileReference.keepSynced(true);
        userProfile = new User();

        getProfileDetails();
        //fields
        profileAvatar = findViewById(R.id.profilepic);
        profileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, SELECT_PHOTO);
            }
        });

        profileName = findViewById(R.id.profile_name);
        matriculationNumber = findViewById(R.id.matricNo);
        firstSOSNumber = findViewById(R.id.emergencyNo1);
        secondSOSNumber = findViewById(R.id.emergencyNo2);
        department = findViewById(R.id.department);
        faculty = findViewById(R.id.faculty);
        address = findViewById(R.id.address);
        otherDetail = findViewById(R.id.otherDetails);

        CardView updateProfileButton = findViewById(R.id.updateProfile);
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Updating Profile");
                progressDialog.show();
                updateProfile();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*private boolean isFirstRun() {
        boolean result = prefs.getBoolean(PREF_IS_FIRST_RUN, true);
        if (result) {
            prefs.edit().putBoolean(PREF_IS_FIRST_RUN, false).apply();
        }

        return result;
    }*/

    private void getProfileDetails() {
        profileReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user_profile = dataSnapshot.getValue(User.class);
                if (user_profile != null) {
                    Log.v("E_VALUE:", "Name: " + user_profile.getProfileName());
                    //profileAvatar.setImageURI(user_profile.getProfileAvatar());
                    profileName.setText(user_profile.getProfileName());
                    faculty.setText(user_profile.getFaculty());
                    department.setText(user_profile.getDepartment());
                    matriculationNumber.setText(user_profile.getMatriculationNumber());
                    address.setText(user_profile.getAddress());
                    firstSOSNumber.setText(user_profile.getFirstSOSNumber());
                    secondSOSNumber.setText(user_profile.getSecondSOSNumber());
                    otherDetail.setText(user_profile.getOtherDetail());

                    //Log.e("profile", user_profile.getProfileAvatar());
                    profileImg = user_profile.getProfileAvatar();
                    Picasso.with(ProfileActivity.this).load(user_profile.getProfileAvatar())
                            .fit()
                            .into(profileAvatar);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateProfile() {
        //initialize fields
        StorageReference filePath = mStorageReference.child("Photos").child(uid);
        if (imageUri!=null) {
            //imageUri = Uri.fromFile(new File("drawable/upload.jpg"));
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Uri imUri = taskSnapshot.getDownloadUrl();
                            userProfile.setId(uid);
                            //userProfile.setProfileAvatar(profileUri.toString());
                            userProfile.setProfileName(profileName.getText().toString());
                            userProfile.setMatriculationNumber(matriculationNumber.getText().toString());
                            userProfile.setFirstSOSNumber(firstSOSNumber.getText().toString());
                            userProfile.setSecondSOSNumber(secondSOSNumber.getText().toString());
                            userProfile.setDepartment(department.getText().toString());
                            userProfile.setFaculty(faculty.getText().toString());
                            userProfile.setAddress(address.getText().toString());
                            userProfile.setOtherDetail(otherDetail.getText().toString());
                            userProfile.setProfileAvatar(imUri.toString());

                            profileReference.setValue(userProfile);
                            Toast.makeText(ProfileActivity.this, "Details successfully updated", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        } else {
            /*Toast.makeText(ProfileActivity.this, "No image Selected", Toast.LENGTH_LONG).show();*/
            Log.e("Photo", "No Photo Selected");
            userProfile.setId(uid);
            //userProfile.setProfileAvatar(profileUri.toString());
            userProfile.setProfileName(profileName.getText().toString());
            userProfile.setMatriculationNumber(matriculationNumber.getText().toString());
            userProfile.setFirstSOSNumber(firstSOSNumber.getText().toString());
            userProfile.setSecondSOSNumber(secondSOSNumber.getText().toString());
            userProfile.setDepartment(department.getText().toString());
            userProfile.setFaculty(faculty.getText().toString());
            userProfile.setAddress(address.getText().toString());
            userProfile.setOtherDetail(otherDetail.getText().toString());
            userProfile.setProfileAvatar(profileImg);
            profileReference.setValue(userProfile);
            Toast.makeText(ProfileActivity.this, "Details successfully updated", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        getProfileDetails();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {

            imageUri = data.getData();
            profileAvatar.setImageURI(imageUri);
            /*if (imageUri != null) {

                progressDialog.setMessage("Uploading photo...");
                progressDialog.show();

                StorageReference filePath = mStorageReference.child("Photos").child(uid);

                uploadTask = filePath.putFile(imageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        profileUri = taskSnapshot.getDownloadUrl();
                        Picasso.with(ProfileActivity.this)
                                .load(profileUri)
                                .into(profileAvatar);
                        Toast.makeText(ProfileActivity.this,"Upload complete", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Picture upload failed", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Log.e("V_CAMERA", "Uri null");
            }*/
            //profileAvatar.setImageURI(imageUri);
        } else {
            Log.e("V_CAMERA", "Not ok");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProfileDetails();
    }
}
