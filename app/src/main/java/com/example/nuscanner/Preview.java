package com.example.nuscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Preview extends AppCompatActivity {

    private ImageView previmg;
    private Button return_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setTitle("Preview");

        previmg = findViewById(R.id.previmg);
        previmg.setImageURI(getIntent().getParcelableExtra("previmg"));
        return_ = findViewById(R.id.return_);
        return_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}