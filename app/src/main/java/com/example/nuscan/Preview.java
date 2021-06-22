package com.example.nuscan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class Preview extends AppCompatActivity {

    private ImageView previmg;
    private Button return_;
    private Button edit;
    private Uri imguri;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setTitle("Preview");

        imguri = getIntent().getParcelableExtra("previmg");
        name = getIntent().getStringExtra("name");
        previmg = findViewById(R.id.previmg);
        previmg.setImageURI(imguri);
        return_ = findViewById(R.id.return_);
        return_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edit = findViewById(R.id.edit_);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,154);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_changes: savechanges(imguri,1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 154 && resultCode == RESULT_OK)
        {
            imguri = data.getData();
            savechanges(imguri,0);
        }
    }

    private void savechanges(Uri uri, int exit_status)
    {
        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
            File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String imgname = file.getAbsolutePath()+"/"+name;
            File imgfile = new File(imgname);
            FileOutputStream outputStream = new FileOutputStream(imgfile);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            previmg.setImageURI(Uri.fromFile(imgfile));
            Toast.makeText(this, "File : "+name+" edited and saved", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if(exit_status==1)
        {
            finish();
        }
    }
}