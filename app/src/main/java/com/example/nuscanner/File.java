package com.example.nuscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class File extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Rec_View_Sub_Adatper mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Card_sub_item> mElist;
    private String page_title;
    private ImageButton sub_item_add;
    private long card_id;

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
        card_id = getIntent().getLongExtra("card_id",0);

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
        SharedPreferences sharedPreferences = getSharedPreferences("id:"+card_id, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(eList2);
        editor.putString("sub_doc_list"+card_id,json);
        editor.apply();
    }

    private void loadData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("id:"+card_id, MODE_PRIVATE);
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
            public void OnItemClicked(int position) {

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

}