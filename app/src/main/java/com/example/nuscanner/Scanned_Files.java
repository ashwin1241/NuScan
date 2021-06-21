package com.example.nuscanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Scanned_Files extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Rec_View_Sub_Adatper mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Card_sub_item> mElist;
    private String page_title;
    private ImageButton sub_item_camera;
    private ImageButton sub_item_gallery;
    private long card_id;
    private int temp_position;
    private Uri imguri = null;
    private Uri camuri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        getSupportActionBar().setTitle("File");
        getSupportActionBar().setHomeButtonEnabled(true);

        page_title = null;
        page_title = getIntent().getStringExtra("page_title");
        if(page_title!=null)
        {
            getSupportActionBar().setTitle(page_title);
        }

        loadData();
        buildrecyclerview();

        sub_item_gallery = findViewById(R.id.sub_item_gallery);
        sub_item_camera = findViewById(R.id.sub_item_camera);
        sub_item_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert_gallery_item(mElist.size());
            }
        });
        sub_item_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert_camera_item(mElist.size());
            }
        });

    }

    private void saveData(ArrayList<Card_sub_item> eList2)
    {
        card_id = getIntent().getLongExtra("card_id",0);
        SharedPreferences sharedPreferences = getSharedPreferences("id_"+card_id, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(eList2);
        editor.putString("sub_doc_list"+card_id,json);
        editor.apply();
    }

    private void loadData()
    {
        String str;
        card_id = getIntent().getLongExtra("card_id",0);
        SharedPreferences sharedPreferences = getSharedPreferences("id_"+card_id, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("sub_doc_list"+card_id,null);
        Type type = new TypeToken<ArrayList<Card_sub_item>>(){}.getType();
        mElist = gson.fromJson(json,type);
        if(mElist==null)
        {
            mElist = new ArrayList<Card_sub_item>();
        }
    }

    private void insert_gallery_item(int position)
    {
        temp_position = position;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,154);
    }

    private void insert_camera_item(int position)
    {
        temp_position = position;
        String destination = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        java.io.File file = new java.io.File(destination);
        if(!file.exists())
        {
            file.mkdir();
            Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
        }
        String imigname = destination+"/"+page_title+System.currentTimeMillis()+".jpg";
        java.io.File imgFile = new java.io.File(imigname);
        camuri = FileProvider.getUriForFile(this,"com.example.nuscanner.fileprovider",imgFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,camuri);
        startActivityForResult(intent,132);
    }

    private void shareitem(int position)
    {
        String[] sub_objects = {"PDF","JPG"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Scanned_Files.this);
        builder.setItems(sub_objects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0 : sharepdf(position);
                        break;
                    case 1 : sharejpg(position);
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void sharepdf(int position)
    {
        if(mElist.get(position).getPdf()==null)
        {
            try {
                String destination = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
                java.io.File file = new java.io.File(destination);
                if (!file.exists()) {
                    file.mkdir();
                    Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                }
                String pdfname = destination + "/NuScanner_" + System.currentTimeMillis() + ".pdf";
                java.io.File pdfFile = new java.io.File(pdfname);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(mElist.get(position).getImage()));
                PdfDocument pdfDocument = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                page.getCanvas().drawBitmap(bitmap, 0, 0, null);
                FileOutputStream outputStream = new FileOutputStream(pdfFile);
                pdfDocument.finishPage(page);
                pdfDocument.writeTo(outputStream);
                pdfDocument.close();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                Uri pdfuri = FileProvider.getUriForFile(this, "com.example.nuscanner.fileprovider", pdfFile);
                intent.putExtra(Intent.EXTRA_STREAM, pdfuri);
                intent.putExtra(Intent.EXTRA_SUBJECT, "NuScanner scanned file " + mElist.get(position).getTitle());
                startActivity(Intent.createChooser(intent, "Share with.."));
                mElist.get(position).setPdf(pdfuri.toString());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(mElist.get(position).getPdf()));
            intent.putExtra(Intent.EXTRA_SUBJECT, "NuScanner scanned file " + mElist.get(position).getTitle());
            startActivity(Intent.createChooser(intent, "Share with.."));
        }
    }

    private void sharejpg(int position) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM,Uri.parse(mElist.get(position).getImage()));
        intent.putExtra(Intent.EXTRA_SUBJECT,"NuScanner scanned file "+mElist.get(position).getTitle());
        startActivity(Intent.createChooser(intent,"Share with.."));
    }

    private void buildrecyclerview()
    {
        mRecyclerView = findViewById(R.id.sub_file_recview);
        mLayoutManager = new GridLayoutManager(this,2);
        mAdapter = new Rec_View_Sub_Adatper(mElist);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new Rec_View_Sub_Adatper.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Intent intent = new Intent(Scanned_Files.this,Preview.class);
                intent.putExtra("previmg",Uri.parse(mElist.get(position).getImage()));
                startActivity(intent);
            }

            @Override
            public void OnTitleClicked(int position) {
                openEditDialog(position);
            }

            @Override
            public void OnItemLongClicked(int position) {
                openLongClickDialog(position);
            }

        });

    }

    private void openEditDialog(int position)
    {
        View view = LayoutInflater.from(Scanned_Files.this).inflate(R.layout.edit_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(Scanned_Files.this);
        builder.setTitle("Edit title")
                .setMessage("Enter new title")
                .setView(view)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText asdf = view.findViewById(R.id.edit_title);
                        mElist.get(position).setTitle(asdf.getText().toString().trim());
                        mAdapter.notifyDataSetChanged();
                        saveData(mElist);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }

    private void openLongClickDialog(int position)
    {
        String[] sub_objects = {"Delete","Share"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Scanned_Files.this);
        builder.setItems(sub_objects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0 : openDelDialog(position);
                        break;
                    case 1 : shareitem(position);
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void openDelDialog(int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Scanned_Files.this);
        builder.setTitle("Delete")
        .setMessage("Are you sure you want to delete this document?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mElist.remove(position);
                mAdapter.notifyItemRemoved(position);
                saveData(mElist);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 154 && resultCode == RESULT_OK && data != null)
        {
            imguri = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imguri);
                File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                if(!file.exists())
                {
                    file.mkdir();
                    Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                }
                String imgname = file.getAbsolutePath()+"/"+page_title+System.currentTimeMillis()+".jpg";
                File imgfile = new File(imgname);
                FileOutputStream outputStream = new FileOutputStream(imgfile);
                image.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                outputStream.flush();
                outputStream.close();
                imguri = FileProvider.getUriForFile(this,"com.example.nuscanner.fileprovider",imgfile);
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            if(imguri != null)
            {
                mElist.add(temp_position,new Card_sub_item(page_title+"_"+temp_position,null,null));
                mAdapter.notifyItemInserted(temp_position);
                mElist.get(temp_position).setImage(imguri.toString());
                Toast.makeText(Scanned_Files.this, "File saved", Toast.LENGTH_SHORT).show();
                saveData(mElist);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == 132 && resultCode == RESULT_OK)
        {
            if(camuri!=null)
            {
                mElist.add(temp_position,new Card_sub_item(page_title+"_"+temp_position,null,null));
                mAdapter.notifyItemInserted(temp_position);
                mElist.get(temp_position).setImage(camuri.toString());
                Toast.makeText(Scanned_Files.this, "File saved", Toast.LENGTH_SHORT).show();
                saveData(mElist);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                Toast.makeText(this, "Image could not be saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

}