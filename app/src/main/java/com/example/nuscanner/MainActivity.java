package com.example.nuscanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    private ImageButton card_select_all;
    private ImageButton card_multiple_share;
    private ImageView page_sort;
    private ImageView page_search;
    private ImageView select_items;
    private ImageView selection_cancel;
    private EditText searchfield;
    private ImageView search_cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("NuScanner");

        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        date = simpleDateFormat.format(new Date());
        selected_items = new ArrayList<>();

        loadData();
        buildrecyclerview();

        card_add = findViewById(R.id.card_add);
        card_photo = findViewById(R.id.card_photo);
        card_gallery = findViewById(R.id.card_gallery);
        card_delete = findViewById(R.id.card_delete);
        page_sort = findViewById(R.id.page_sort);
        page_search = findViewById(R.id.page_search);
        select_items = findViewById(R.id.select_items);
        selection_cancel = findViewById(R.id.selection_cancel);
        card_select_all = findViewById(R.id.card_select_all);
        card_multiple_share = findViewById(R.id.card_share_multiple);
        searchfield = findViewById(R.id.searchfield);
        search_cancel = findViewById(R.id.search_cancel);

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
                card_select_all.setVisibility(View.VISIBLE);
                card_add.setVisibility(View.INVISIBLE);
                card_photo.setVisibility(View.INVISIBLE);
                card_gallery.setVisibility(View.INVISIBLE);
                page_search.setVisibility(View.INVISIBLE);
                page_sort.setVisibility(View.INVISIBLE);
                selection_cancel.setVisibility(View.VISIBLE);
            }
        });
        selection_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setSelecttype(0);
                for(int i=0;i<mElist.size();i++)
                {
                    mElist.get(i).setSelected(false);
                }
                selected_items = new ArrayList<>();
                mAdapter.notifyDataSetChanged();
                card_select_all.setVisibility(View.INVISIBLE);
                card_add.setVisibility(View.VISIBLE);
                card_photo.setVisibility(View.VISIBLE);
                card_gallery.setVisibility(View.VISIBLE);
                page_search.setVisibility(View.VISIBLE);
                page_sort.setVisibility(View.VISIBLE);
                selection_cancel.setVisibility(View.INVISIBLE);
                card_delete.setVisibility(View.INVISIBLE);
                card_multiple_share.setVisibility(View.INVISIBLE);
            }
        });
        card_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.setSelecttype(1);
                selected_items = new ArrayList<>();
                for(int i=0;i<mElist.size();i++)
                {
                    mElist.get(i).setSelected(true);
                    selected_items.add(i);
                }
                mAdapter.notifyDataSetChanged();
                card_delete.setVisibility(View.VISIBLE);
                card_multiple_share.setVisibility(View.VISIBLE);
            }
        });
        page_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortItems();
            }
        });
        page_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        search_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchfield.setVisibility(View.INVISIBLE);
                search_cancel.setVisibility(View.INVISIBLE);
                page_search.setVisibility(View.VISIBLE);
                page_sort.setVisibility(View.VISIBLE);
                select_items.setVisibility(View.VISIBLE);
                card_photo.setVisibility(View.VISIBLE);
                card_add.setVisibility(View.VISIBLE);
                card_gallery.setVisibility(View.VISIBLE);
                buildrecyclerview();
            }
        });

    }

    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedpreferences_sp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mElist);
        editor.putString("doc_list",json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedpreferences_sp",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("doc_list",null);
        Type type = new TypeToken<ArrayList<Card_item>>(){}.getType();
        mElist = gson.fromJson(json,type);
        if(mElist==null)
        {
            mElist = new ArrayList<Card_item>();
        }
    }

    private void insert_item(int position)
    {
        mElist.add(mElist.size(),new Card_item("NuScanner_"+System.currentTimeMillis(),date,null,false));
        mAdapter.notifyItemInserted(position);
        saveData();
    }

    private void remove_item(int position)
    {
        mElist.remove(position);
        mAdapter.notifyItemRemoved(position);
        saveData();
    }

    private void sortItems()
    {
        String[] objects = {"Creation date (ascending)","Creation date (descending)","Title (A-Z)","Title (Z-A)"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Sort by:")
        .setItems(objects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0: sortdateasc();
                        break;
                    case 1: sortdatedesc();
                        break;
                    case 2: sortAZ();
                        break;
                    case 3: sortZA();
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void sortAZ()
    {
        Collections.sort(mElist, new Comparator<Card_item>() {
            @Override
            public int compare(Card_item o1, Card_item o2) {
                return o1.getTitle().trim().compareTo(o2.getTitle().trim());
            }
        });
        mAdapter.notifyDataSetChanged();
        saveData();
    }

    private void sortZA()
    {
        Collections.sort(mElist, new Comparator<Card_item>() {
            @Override
            public int compare(Card_item o1, Card_item o2) {
                return o2.getTitle().trim().compareTo(o1.getTitle().trim());
            }
        });
        mAdapter.notifyDataSetChanged();
        saveData();
    }

    private void sortdateasc()
    {
        Collections.sort(mElist, new Comparator<Card_item>() {
            @Override
            public int compare(Card_item o1, Card_item o2) {
                return o1.getDate().trim().compareTo(o2.getDate().trim());
            }
        });
        mAdapter.notifyDataSetChanged();
        saveData();
    }

    private void sortdatedesc()
    {
        Collections.sort(mElist, new Comparator<Card_item>() {
            @Override
            public int compare(Card_item o1, Card_item o2) {
                return o2.getDate().trim().compareTo(o2.getDate().trim());
            }
        });
        mAdapter.notifyDataSetChanged();
        saveData();
    }

    private void search()
    {
        searchfield.setVisibility(View.VISIBLE);
        search_cancel.setVisibility(View.VISIBLE);
        page_search.setVisibility(View.INVISIBLE);
        page_sort.setVisibility(View.INVISIBLE);
        select_items.setVisibility(View.INVISIBLE);
        card_photo.setVisibility(View.INVISIBLE);
        card_add.setVisibility(View.INVISIBLE);
        card_gallery.setVisibility(View.INVISIBLE);
        searchfield.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString().trim());
            }
        });
    }

    public void filter(String s)
    {
        ArrayList<Card_item> filter_list = new ArrayList<>();
        for(Card_item item : mElist)
        {
            if(item.getTitle().trim().toLowerCase().contains(s.toLowerCase()))
            {
                filter_list.add(item);
            }
        }
        mAdapter.filterList(filter_list);
    }

    private void buildrecyclerview()
    {
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
                        for(int i=0;i<selected_items.size();i++)
                        {
                            if(selected_items.get(i)==position)
                            {
                                selected_items.remove(i);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        if(selected_items.size()==0)
                        {
                            mAdapter.setSelecttype(0);
                            mAdapter.notifyDataSetChanged();
                            card_select_all.setVisibility(View.INVISIBLE);
                            card_add.setVisibility(View.VISIBLE);
                            card_photo.setVisibility(View.VISIBLE);
                            card_gallery.setVisibility(View.VISIBLE);
                            page_search.setVisibility(View.VISIBLE);
                            page_sort.setVisibility(View.VISIBLE);
                            selection_cancel.setVisibility(View.INVISIBLE);
                            card_delete.setVisibility(View.INVISIBLE);
                            card_multiple_share.setVisibility(View.INVISIBLE);
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
                            card_multiple_share.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }

            @Override
            public void OnItemLongClicked(int position) {
                mAdapter.setSelecttype(1);
                mElist.get(position).setSelected(true);
                selected_items.add(position);
                card_select_all.setVisibility(View.VISIBLE);
                card_add.setVisibility(View.INVISIBLE);
                card_photo.setVisibility(View.INVISIBLE);
                card_gallery.setVisibility(View.INVISIBLE);
                card_delete.setVisibility(View.VISIBLE);
                card_multiple_share.setVisibility(View.VISIBLE);
                page_search.setVisibility(View.INVISIBLE);
                page_sort.setVisibility(View.INVISIBLE);
                selection_cancel.setVisibility(View.VISIBLE);
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
                card_select_all.setVisibility(View.INVISIBLE);
                card_add.setVisibility(View.VISIBLE);
                card_photo.setVisibility(View.VISIBLE);
                card_gallery.setVisibility(View.VISIBLE);
                page_search.setVisibility(View.VISIBLE);
                page_sort.setVisibility(View.VISIBLE);
                selection_cancel.setVisibility(View.INVISIBLE);
                card_delete.setVisibility(View.INVISIBLE);
                card_multiple_share.setVisibility(View.INVISIBLE);
                saveData();
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
                saveData();
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