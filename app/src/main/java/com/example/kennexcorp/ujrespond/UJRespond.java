package com.example.kennexcorp.ujrespond;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by kennexcorp on 2/22/18.
 */

public class UJRespond extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
