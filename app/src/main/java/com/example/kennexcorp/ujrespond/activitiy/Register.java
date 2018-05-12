package com.example.kennexcorp.ujrespond.activitiy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kennexcorp.ujrespond.R;
import com.example.kennexcorp.ujrespond.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends Activity {

    private TextInputEditText rEmail;
    private TextInputEditText rPass;
    private TextInputEditText rName;
    private TextInputEditText rMatNum;
    private TextInputEditText rFaculty;
    private TextInputEditText rIceNum;
    private CardView rRegister;
    private TextView rCancel;
    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private FirebaseDatabase mDatabase;
    private ProgressDialog progressDialog;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize firebase references
        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference().child("User_Profile");

        rEmail = findViewById(R.id.uemail);
        rPass = findViewById(R.id.upassword);
        rName = findViewById(R.id.rName);
        rFaculty = findViewById(R.id.rFaculty);
        rMatNum = findViewById(R.id.rMatricNumber);
        rIceNum = findViewById(R.id.uice);

        progressDialog = new ProgressDialog(this);

        rRegister = findViewById(R.id.uRegister);
        rRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = rEmail.getText().toString().trim();
                String password = rPass.getText().toString().trim();
                final String name = rName.getText().toString().trim();
                final String matNum = rMatNum.getText().toString().trim();
                final String faculty = rFaculty.getText().toString().trim();
                final String iceNum = rIceNum.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter your name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(matNum)) {
                    Toast.makeText(getApplicationContext(), "Enter your matriculation number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(iceNum)) {
                    Toast.makeText(getApplicationContext(), "Enter your Next of Kin contact", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(faculty)) {
                    Toast.makeText(getApplicationContext(), "Enter your Faculty", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.setMessage("Registering...");
                progressDialog.show();

                //create user
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                user_id = mAuth.getCurrentUser().getUid();
                                Log.e("User_id", user_id);
                                createUserDb(name, matNum, faculty, iceNum);
                                progressDialog.dismiss();
                                Toast.makeText(Register.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(Register.this, Login.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(loginIntent);
                                //mAuth.signOut();
                                finish();


                            }
                        }).addOnFailureListener(Register.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "Registration failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        rCancel = findViewById(R.id.rcancel);
        rCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }

    private void createUserDb(String name, String matNum, String faculty, String iceNum) {
        User userProfile = new User();
        DatabaseReference current_user_db = mReference.child(user_id);

        //initialize compulsory fields
        userProfile.setId(user_id);
        userProfile.setProfileName(name);
        userProfile.setMatriculationNumber(matNum);
        userProfile.setFaculty(faculty);
        userProfile.setFirstSOSNumber(iceNum);

        //initialize other fields to empty strings
        userProfile.setSecondSOSNumber("");
        userProfile.setOtherDetail("");
        userProfile.setAddress("");
        userProfile.setDepartment("");
        userProfile.setProfileAvatar(null);

        //create user database
        current_user_db.setValue(userProfile);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
