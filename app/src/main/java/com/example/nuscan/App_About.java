package com.example.nuscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class App_About extends AppCompatActivity {

    private ExtendedFloatingActionButton tutorial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_about);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("About this app");

        tutorial = findViewById(R.id.app_tutorial);
        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(App_About.this,MainActivityTutorial1.class);
                startActivity(intent);
            }
        });

    }
}