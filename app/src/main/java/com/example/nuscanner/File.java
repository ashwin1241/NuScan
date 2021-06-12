package com.example.nuscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class File extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Rec_View_Sub_Adatper mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Card_sub_item> mElist;
    private String page_title;
    private ImageButton sub_item_add;
    private long card_id;
    private int temp_position;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("Images");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imguri = null;

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

        sub_item_add = findViewById(R.id.sub_item_add);
        sub_item_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertitem(mElist.size());
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

    private void insertitem(int position)
    {
        mElist.add(mElist.size(),new Card_sub_item(page_title+"_"+position,null));
        mAdapter.notifyItemInserted(position);
        saveData(mElist);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,154);
        temp_position = position;
    }

    private void shareitem(int position)
    {

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
            public void OnItemClicked(int position) {}

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
        View view = LayoutInflater.from(File.this).inflate(R.layout.edit_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(File.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(File.this);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(File.this);
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
            if(imguri != null)
            {
                StorageReference fileRef = reference.child(System.currentTimeMillis()+"."+getfileextension(imguri));
                fileRef.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Model model = new Model(uri.toString());
                                String mid = root.push().getKey();
                                root.child(mid).setValue(model);
                                mElist.get(temp_position).setImage(uri.toString());
                                Toast.makeText(File.this, "Upload successful", Toast.LENGTH_SHORT).show();
                                saveData(mElist);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(File.this, "Upload failed 1101", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else
            {

            }
        }
    }

    private String getfileextension(Uri uri)
    {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

}