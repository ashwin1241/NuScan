package com.example.nuscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivityTutorial2 extends AppCompatActivity {

    private TextView exit;
    private TextView next;
    private ImageView next_arrow;
    private RecyclerView mRecyclerView;
    private Rec_View_Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Card_item> mElist = new ArrayList<>();
    private String day;
    private int aret = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    private String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tutorial2);

        buildrecyclerview();

        next = findViewById(R.id.tutorial2_next);
        next_arrow = findViewById(R.id.tutorial2_next_arrow);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityTutorial2.this, MainActivityTutorial3.class);
                startActivity(intent);
            }
        });
        next_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityTutorial2.this, MainActivityTutorial3.class);
                startActivity(intent);
            }
        });
        exit = findViewById(R.id.exit_tut2);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityTutorial2.this,App_About.class);
                startActivity(intent);
            }
        });
        switch (aret)
        {
            case Calendar.MONDAY: day = "Mon";
                break;
            case Calendar.TUESDAY: day = "Tue";
                break;
            case Calendar.WEDNESDAY: day = "Wed";
                break;
            case Calendar.THURSDAY: day = "Thu";
                break;
            case Calendar.FRIDAY: day = "Fri";
                break;
            case Calendar.SATURDAY: day = "Sat";
                break;
            case Calendar.SUNDAY: day = "Sun";
                break;
        }
        mElist.add(0, new Card_item("NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date()),date,false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void buildrecyclerview()
    {
        mRecyclerView = findViewById(R.id.home_recview_tutorial2);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new Rec_View_Adapter(mElist);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new Rec_View_Adapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position) {

            }

            @Override
            public void OnItemLongClicked(int position) {

            }

            @Override
            public void OnItemShared(int position) {

            }

            @Override
            public void OnTitleClicked(int position) {

            }

            @Override
            public void NewListselect(int position, ArrayList<Card_item> list1) {

            }
        });
    }

}