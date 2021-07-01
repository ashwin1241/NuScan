package com.example.nuscan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class App_Feedback extends AppCompatActivity {

    private ExtendedFloatingActionButton send;
    private ExtendedFloatingActionButton clear;
    private EditText message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_feedback);
        getSupportActionBar().setTitle("Feedback");
        getSupportActionBar().setHomeButtonEnabled(true);

        send = findViewById(R.id.feedback_send);
        clear = findViewById(R.id.feedback_clear);
        message = findViewById(R.id.feedback_message);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message.setText("");
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] recepient = {"ashwinwadatkar@gmail.com"};
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,message.getText().toString().trim());
                intent.putExtra(Intent.EXTRA_SUBJECT,"NuScan app feedback");
                intent.putExtra(Intent.EXTRA_EMAIL,recepient);
                startActivity(intent);
            }
        });
    }
}