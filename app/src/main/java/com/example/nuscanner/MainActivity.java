package com.example.nuscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Rec_View_Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Card_item> mElist;
    private ArrayList<Integer> selected_items;
    private SimpleDateFormat simpleDateFormat;
    private String date;
    private ImageButton card_add;
    private ImageButton card_photo;
    private ImageButton card_gallery;
    private ImageButton card_delete;
    private ImageView page_sort;
    private ImageView page_search;
    private ImageView select_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("NuScanner");

        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        date = simpleDateFormat.format(new Date());
        selected_items = new ArrayList<>();

        buildrecyclerview();

        card_add = findViewById(R.id.card_add);
        card_photo = findViewById(R.id.card_photo);
        card_gallery = findViewById(R.id.card_gallery);
        card_delete = findViewById(R.id.card_delete);
        page_sort = findViewById(R.id.page_sort);
        page_search = findViewById(R.id.page_search);
        select_items = findViewById(R.id.select_items);

        card_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert_item(mElist.size());
            }
        });

        card_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDelDialog();
            }
        });

        select_items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setSelecttype(1);
                mAdapter.notifyDataSetChanged();
                card_add.setVisibility(View.INVISIBLE);
                card_photo.setVisibility(View.INVISIBLE);
                card_gallery.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void insert_item(int position)
    {
        mElist.add(mElist.size(),new Card_item("New Folder",date,null,false));
        mAdapter.notifyItemInserted(position);
    }

    private void remove_item(int position)
    {
        mElist.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    private void buildrecyclerview()
    {
        mElist = new ArrayList<>();

        mRecyclerView = findViewById(R.id.home_recview);
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new Rec_View_Adapter(mElist);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new Rec_View_Adapter.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                if(mAdapter.getSelecttype()==1)
                {
                    if(mElist.get(position).isSelected()==true)
                    {
                        mElist.get(position).setSelected(false);
                        mAdapter.notifyDataSetChanged();
                        for(int i=0;i<selected_items.size();i++)
                        {
                            if(i==position)
                            {
                                selected_items.remove(i);
                            }
                        }
                        if(selected_items.size()==0)
                        {
                            mAdapter.setSelecttype(0);
                            mAdapter.notifyDataSetChanged();
                            card_delete.setVisibility(View.INVISIBLE);
                            card_add.setVisibility(View.VISIBLE);
                            card_photo.setVisibility(View.VISIBLE);
                            card_gallery.setVisibility(View.VISIBLE);
                        }
                    }
                    else
                    {
                        mElist.get(position).setSelected(true);
                        mAdapter.notifyDataSetChanged();
                        selected_items.add(position);
                        if(selected_items.size()>0)
                        {
                            card_delete.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void OnItemLongClicked(int position) {
                mAdapter.setSelecttype(1);
                mElist.get(position).setSelected(true);
                selected_items.add(position);
                card_add.setVisibility(View.INVISIBLE);
                card_photo.setVisibility(View.INVISIBLE);
                card_gallery.setVisibility(View.INVISIBLE);
                card_delete.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnItemShared(int position) {
                Toast.makeText(MainActivity.this, "Share clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnTitleClicked(int position) {
                openEditDialog(position);
            }
        });
    }

    private void openDelDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Delete")
        .setMessage("Are you sure you want to delete selected items?")
        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Collections.sort(selected_items);
                for(int i=0;i<selected_items.size();i++)
                {
                    remove_item(selected_items.get(i)-i);
                }
                selected_items = new ArrayList<>();
                mAdapter.setSelecttype(0);
                mAdapter.notifyDataSetChanged();
                card_delete.setVisibility(View.INVISIBLE);
                card_add.setVisibility(View.VISIBLE);
                card_photo.setVisibility(View.VISIBLE);
                card_gallery.setVisibility(View.VISIBLE);
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void openEditDialog(int position)
    {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.edit_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit title")
        .setMessage("Enter new title")
        .setView(view)
        .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText asdf = view.findViewById(R.id.edit_title);
                mElist.get(position).setTitle(asdf.getText().toString().trim());
                mAdapter.notifyDataSetChanged();
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