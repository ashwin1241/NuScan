package com.example.nuscan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class App_Guide extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_guide);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Quick Guide");
    }
}