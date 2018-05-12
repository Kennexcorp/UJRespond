package com.example.kennexcorp.ujrespond.activitiy;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kennexcorp.ujrespond.R;
import com.example.kennexcorp.ujrespond.model.Tips;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends AppCompatActivity {

    private static final String[] tips = {
            "Tips 1", "Tips 2", "Tips 3", "Tips 4", "Tips 5", "Tips 6","Tips 7","Tips 8", "Tips 9", "Tips 10"
    };

    private RecyclerView recyclerView;
    private RecyclerView.Adapter reAdapter;
    private TipsAdapter adapter;
    private List<Tips> tipsList;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tips);
        Toolbar toolbar = findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);
        toolbar.setTitle("Safety Tips");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        // toolbar.setTitleTextColor(getResources().getColor);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        tipsList = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("EmergencyTips");
        mDatabaseReference.keepSynced(true);

        GetTips();
        adapter = new TipsAdapter(tipsList);
        //recyclerView.setAdapter(adapter);

    }

    void GetTips() {
        //DatabaseReference mRef = mDatabaseReference.child()
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Tips emergencyTips = dataSnapshot.getValue(Tips.class);

                tipsList.add(emergencyTips);

                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipsViewHolder> {

        List<Tips> tipsListArray;

        TipsAdapter(List<Tips> tipsList) {
            this.tipsListArray = tipsList;
        }

        @Override
        public void onBindViewHolder(TipsViewHolder holder, int position) {
            //Tips tips = tipsListArray.get(position);
            Log.e("Tips", tipsListArray.get(position).getTitle());
            holder.titleTextView.setText(tipsListArray.get(position).getTitle());
            holder.descriptionTextView.setText(tipsListArray.get(position).getDescription());
        }

        @Override
        public TipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview, parent, false);

            return new TipsViewHolder(view);
        }


        class TipsViewHolder extends RecyclerView.ViewHolder{
            TextView titleTextView;
            TextView descriptionTextView;

            TipsViewHolder(View itemView) {
                super(itemView);

                titleTextView = itemView.findViewById(R.id.tipsTitle);
                descriptionTextView = itemView.findViewById(R.id.tipsDescription);
            }
        }

        @Override
        public int getItemCount() {
            return tipsListArray.size();
        }
    }

}
