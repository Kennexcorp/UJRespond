package com.example.kennexcorp.ujrespond.activitiy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.kennexcorp.ujrespond.R;

public class HotlinesActivity extends AppCompatActivity {

    private static final String POLICE_SERVICE_NUMBER = "08169311714";
    private static final String FIRE_SERVICE_NUMBER = "08037052258";
    private static final String HEALTH_SERVICE_NUMBER = "08035381399";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotlines);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CardView fireServiceDial = findViewById(R.id.fireservice);
        CardView healthServiceDial = findViewById(R.id.healthservice);
        CardView policeServiceDial = findViewById(R.id.policeservice);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fireServiceDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialFireService();
            }
        });

        healthServiceDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialHealthService();
            }
        });

        policeServiceDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialPoliceService();
            }
        });
    }

    private void dialPoliceService() {
        String toDial="tel:"+POLICE_SERVICE_NUMBER;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial)));
    }

    private void dialHealthService() {
        String toDial="tel:"+HEALTH_SERVICE_NUMBER;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial)));
    }

    private void dialFireService() {
        String toDial="tel:"+FIRE_SERVICE_NUMBER;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial)));
    }

}
