package com.capstone.sejong.homenect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_ACT = 1000;

    private List<Thing> things;
    private RecyclerView rv;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        fab = (FloatingActionButton)findViewById(R.id.fab_settings);

        initFab();
        initializeData();
        initializeAdapter();
    }

    private void initializeData(){
        things = new ArrayList<>();
        things.add(new Thing("Capstone", false, R.drawable.default_photo));
        things.add(new Thing("Things", false, R.drawable.default_photo));
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(things);
        rv.setAdapter(adapter);
    }

    private void initFab(){
        findViewById(R.id.fab_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                things.add(new Thing("HomeNect", false, R.drawable.default_photo));
                initializeAdapter();
            }
        });
    }
}
