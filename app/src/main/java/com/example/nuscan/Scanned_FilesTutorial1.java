package com.example.nuscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Scanned_FilesTutorial1 extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Rec_View_Sub_Adatper mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Card_sub_item> mElist = new ArrayList<>();
    private TextView next;
    private ImageView next_arrow;
    private TextView exit;
    private String day;
    private int aret = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    private String date = new SimpleDateFormat("hh:mm").format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_files_tutorial1);
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
        getSupportActionBar().setTitle("NuScan_"+day+"_"+date);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buildrecyclerview();

        next = findViewById(R.id.tutorial4_next);
        next_arrow = findViewById(R.id.tutorial4_next_arrow);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scanned_FilesTutorial1.this,PreviewTutorial1.class);
                startActivity(intent);
            }
        });
        next_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scanned_FilesTutorial1.this,PreviewTutorial1.class);
                startActivity(intent);
            }
        });
        exit = findViewById(R.id.exit_tut4);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scanned_FilesTutorial1.this,App_About.class);
                startActivity(intent);
            }
        });

        mElist.add(new Card_sub_item("NuScan_"+day+"_"+date+"_0",null,null,null));

    }

    private void buildrecyclerview()
    {
        mRecyclerView = findViewById(R.id.sub_file_recview_tutorial);
        mLayoutManager = new GridLayoutManager(this,2);
        mAdapter = new Rec_View_Sub_Adatper(mElist,Scanned_FilesTutorial1.this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new Rec_View_Sub_Adatper.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position) {

            }

            @Override
            public void OnTitleClicked(int position) {

            }
        });
    }

}