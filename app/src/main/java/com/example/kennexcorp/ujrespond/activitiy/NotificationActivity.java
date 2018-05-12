package com.example.kennexcorp.ujrespond.activitiy;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kennexcorp.ujrespond.R;

public class NotificationActivity extends ListActivity {

    private static final String[] notifications = {
            "Notification 1", "Notification 2", "Notification 3", "Notification 4", "Notification 5", "Notification 6","Notification 7","Notification 8", "Notification 9", "Notification 10"
    };
    //ListActivity listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //setSupportActionBar(toolbar);
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notifications));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
