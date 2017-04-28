package com.capstone.sejong.homenect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Thing> things;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = (RecyclerView)findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);

        things = new ArrayList<>();
        things.add(new Thing("Capstone", false, false, false, false));
        things.add(new Thing("Things", false, false, false, false));

        final RVAdapter adapter = new RVAdapter(things);
        rv.setAdapter(adapter);

//        initializeData();

        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(rv, new SwipeableRecyclerViewTouchListener.SwipeListener() {
            @Override // swipe event listener
            public boolean canSwipeLeft(int position) {
                return true;
            }

            @Override
            public boolean canSwipeRight(int position) {
                return true;
            }

            @Override
            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    things.remove(position);
                    adapter.notifyItemRemoved(position);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                    things.remove(position);
                    adapter.notifyItemRemoved(position);
                }
                adapter.notifyDataSetChanged();
            }
        });

        rv.addOnItemTouchListener(swipeTouchListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //STT 결과 받아오기 위한 함수
    }

    private void initializeData(){
        things = new ArrayList<>();
        things.add(new Thing("Capstone", false, false, false, false));
        things.add(new Thing("Things", false, false, false, false));
    }

    private void initializeAdapter(){

    }

    private void initFab(){
        findViewById(R.id.fab_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                things.add(new Thing("HomeNect", false, false, false, false));
                initializeAdapter();
            }
        });
    }
}
