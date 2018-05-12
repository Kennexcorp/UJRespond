package com.example.kennexcorp.ujrespond.activitiy;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kennexcorp.ujrespond.R;
import com.example.kennexcorp.ujrespond.model.Report;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportActivity extends Activity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {

    private static final String HEALTH_EMERGENCY_ID = "111";
    private static final String FIRE_EMERGENCY_ID = "222";
    private static final String POLICE_EMERGENCY_ID = "333";
    private static final String OTHER_EMERGENCY_ID = "000";

    private static final String[] cases = {
            "Select an emergency","Ambulance", "Fire Truck", "Police"
    };

    private Spinner spinner;
    public static final String LATITUDE_EXTRA = "";
    public static final String LONGITUDE_EXTRA = "";

    private TextView longtitude;
    private TextView latitude;
    private EditText description;
    private CardView addLocation;
    private CardView reportCase;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private String emergencyType;
    private CheckBox ambulanceBox;
    private CheckBox fireTruckBox;
    private CheckBox policeBox;
    private boolean isLocationSet = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("Report a Situation");
        toolbar.setNavigationIcon(R.drawable.ic_navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //toolbar.setNavigationIcon(getDrawable(R.drawable.ic_navigation_back));
        //setSupportActionBar(toolbar);

        ambulanceBox = findViewById(R.id.ambulanceBox);
        ambulanceBox.setOnCheckedChangeListener(this);
        fireTruckBox = findViewById(R.id.fireBox);
        fireTruckBox.setOnCheckedChangeListener(this);
        policeBox = findViewById(R.id.policeBox);
        policeBox.setOnCheckedChangeListener(this);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Reports");
        mAuth = FirebaseAuth.getInstance();

        longtitude = findViewById(R.id.anonlong);
        latitude = findViewById(R.id.anonlat);
        description = findViewById(R.id.description);
        addLocation = findViewById(R.id.locButton);
        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLocationSet = true;
                Bundle bundle = getIntent().getExtras();
                String[] location = bundle != null ? bundle.getStringArray("location") : new String[0];
                longtitude.setText(location != null ? location[0] : null);
                latitude.setText(location != null ? location[1] : null);
            }
        });
        reportCase = findViewById(R.id.sendReport);
        reportCase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reportSituation();
            }
        });

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cases);

        arrayAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Selected_spinner", adapterView.getItemAtPosition(i).toString());
                switch (i) {
                    case 1:
                        emergencyType = HEALTH_EMERGENCY_ID;
                        break;
                    case 2:
                        emergencyType = FIRE_EMERGENCY_ID;
                        break;
                    case 3:
                        emergencyType = POLICE_EMERGENCY_ID;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void reportSituation() {
        Report report = new Report();
        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference reportReference = mDatabaseReference.push();
        String lon = longtitude.getText().toString();
        String lat = latitude.getText().toString();
        Long timeStamp = System.currentTimeMillis() / 1000;
        //String ts = timeStamp.toString();
        Log.e("TimeStamp: ", timeStamp.toString());

        if (TextUtils.isEmpty(lon) && TextUtils.isEmpty(lat)) {
            Toast.makeText(this, "Your location cannot be determined", Toast.LENGTH_LONG).show();
            return;
        } else {
            report.setReportId(reportReference.getKey());
            report.setUserId(user_id);
            report.setTimeStamp(timeStamp);
            report.setId(emergencyType);
            report.setUserId(user_id);
            report.setDescription(description.getText().toString());
            //report.setTimeStamp();
            reportReference.setValue(report);
            Toast.makeText(ReportActivity.this, "Report Sent", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId() == ambulanceBox.getId() && b) {
            emergencyType = HEALTH_EMERGENCY_ID;
            Log.e("Checked", "true");
        }
        if (compoundButton.getId() == fireTruckBox.getId() && b) {
            emergencyType = FIRE_EMERGENCY_ID;
        }
        if (compoundButton.getId() == policeBox.getId() && b) {
            emergencyType = POLICE_EMERGENCY_ID;
        }

    }
}
