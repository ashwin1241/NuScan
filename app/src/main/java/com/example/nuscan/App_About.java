package com.example.nuscan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class App_About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_about);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("About this app");
    }
}