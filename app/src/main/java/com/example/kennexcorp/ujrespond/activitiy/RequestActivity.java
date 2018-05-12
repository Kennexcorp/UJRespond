package com.example.kennexcorp.ujrespond.activitiy;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kennexcorp.ujrespond.R;
import com.example.kennexcorp.ujrespond.model.RequestComplaint;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RequestActivity extends AppCompatActivity {

    private String user_id;
    private EditText name;
    private EditText email;
    private EditText message;
    private RequestComplaint requestComplaint;

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("RequestComplaints");

        user_id = mAuth.getCurrentUser().getUid();
        name = findViewById(R.id.feedbackName);
        email = findViewById(R.id.feedbackEmail);
        message = findViewById(R.id.feedbackMessage);
        getNameAndEmail();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rname = name.getText().toString().trim();
                String rmail = email.getText().toString().trim();
                String msg = message.getText().toString().trim();


                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(getApplicationContext(), "Message field cant be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (sendRequest()) {
                    Toast.makeText(getApplicationContext(), "Request sent successfully...", Toast.LENGTH_LONG).show();
                    clearFields();
                }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void clearFields() {
        name.setText("");
        message.setText("");
    }

    private void getNameAndEmail() {
        email.setText(mAuth.getCurrentUser().getEmail());
    }

    private boolean sendRequest() {
        Long timeStamp = System.currentTimeMillis() / 1000;
        //String ts = timeStamp.toString();
        Log.e("TimeStamp: ", timeStamp.toString());
        requestComplaint = new RequestComplaint();
        DatabaseReference reference = databaseReference.push();

        requestComplaint.setId(reference.getKey());
        requestComplaint.setTimeStamp(timeStamp);
        requestComplaint.setUser_id(user_id);
        requestComplaint.setName(name.getText().toString());
        requestComplaint.setEmail(email.getText().toString());
        requestComplaint.setMessage(message.getText().toString());

        reference.setValue(requestComplaint);

        return true;
    }

}
