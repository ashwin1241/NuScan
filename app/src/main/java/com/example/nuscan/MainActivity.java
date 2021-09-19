package com.example.nuscan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private Rec_View_Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Card_item> mElist;
    private ArrayList<Card_sub_item> subshare_list;
    private ArrayList<Integer> selected_items;
    private SimpleDateFormat simpleDateFormat;
    private String date;
    private ImageButton card_add;
    private ImageButton card_delete;
    private ImageButton card_select_all;
    private ImageButton card_multiple_share;
    private ImageView page_sort;
    private ImageView page_search;
    private ImageView select_items;
    private ImageView selection_cancel;
    private EditText searchfield;
    private ImageView search_cancel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("NuScan");

        if(permission())
        {

        }
        else
        {
            AskPermission();
        }

        {
            simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            date = simpleDateFormat.format(new Date());
            selected_items = new ArrayList<>();

            loadData();
            buildrecyclerview();
            loadImages();

            card_add = findViewById(R.id.card_add);
            card_delete = findViewById(R.id.card_delete);
            page_sort = findViewById(R.id.page_sort);
            page_search = findViewById(R.id.page_search);
            select_items = findViewById(R.id.select_items);
            selection_cancel = findViewById(R.id.selection_cancel);
            card_select_all = findViewById(R.id.card_select_all);
            card_multiple_share = findViewById(R.id.card_share_multiple);
            searchfield = findViewById(R.id.searchfield);
            search_cancel = findViewById(R.id.search_cancel);
            swipeRefreshLayout = findViewById(R.id.main_list_refresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                    buildrecyclerview();
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            card_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    insert_item(0);
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
                    page_search.setVisibility(View.INVISIBLE);
                    page_sort.setVisibility(View.INVISIBLE);
                    selection_cancel.setVisibility(View.VISIBLE);
                }
            });
            selection_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAdapter.setSelecttype(0);
                    for (int i = 0; i < mElist.size(); i++) {
                        mElist.get(i).setSelected(false);
                    }
                    selected_items = new ArrayList<>();
                    mAdapter.notifyDataSetChanged();
                    card_select_all.setVisibility(View.INVISIBLE);
                    card_add.setVisibility(View.VISIBLE);
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
                    for (int i = 0; i < mElist.size(); i++) {
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
                    mAdapter.setSelecttype(25);
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
                    card_add.setVisibility(View.VISIBLE);
                    mAdapter.setSelecttype(0);
                    buildrecyclerview();
                }
            });
            card_multiple_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    share_bulk();
                }
            });
        }

    }

    private boolean permission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
            return Environment.isExternalStorageManager();
        else
        {
            int a = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE);
            int b = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return a==PackageManager.PERMISSION_GRANTED && b == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void AskPermission()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            try
            {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s",new Object[]{getApplicationContext().getPackageName()})));
                startActivityForResult(intent,2000);
            }
            catch (Exception e)
            {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent,2000);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults [1] == PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {
                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.MANAGE_EXTERNAL_STORAGE))
                {
                    AskPermission();
                }
                else
                {
                    finish();
                    System.exit(0);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2000)
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
            {
                if(Environment.isExternalStorageManager())
                {

                }
                else
                {

                }
            }
        }
    }

    private void saveData(ArrayList<Card_item> eList1)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedpreferences_sp",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(eList1);
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

    private void loadImages()
    {
        ArrayList<Card_sub_item> carrier ;
        long card_id = 0;
        for(Card_item item : mElist)
        {
            card_id = item.getId();
            SharedPreferences sp_sub = getSharedPreferences("id_"+card_id, MODE_PRIVATE);
            Gson gs_sub = new Gson();
            String js_sub = sp_sub.getString("sub_doc_list"+card_id,null);
            Type type_sub = new TypeToken<ArrayList<Card_sub_item>>(){}.getType();
            carrier = gs_sub.fromJson(js_sub,type_sub);
            if(carrier!=null&&carrier.size()>0)
            {
                item.setImage(carrier.get(0).getImage());
            }
            else
            {
                continue;
            }
        }
        mAdapter.notifyDataSetChanged();
        saveData(mElist);
    }

    String day;
    int aret = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

    private void insert_item(int position)
    {
        switch (aret)
        {
            case Calendar.MONDAY: day = "Mon";
                break;
            case Calendar.TUESDAY: day = "Tue";
                break;
            case Calendar.WEDNESDAY: day = "Wed";
                break;
            case Calendar.THURSDAY: day = "Thu";
                break;
            case Calendar.FRIDAY: day = "Fri";
                break;
            case Calendar.SATURDAY: day = "Sat";
                break;
            case Calendar.SUNDAY: day = "Sun";
                break;
        }
        mElist.add(position, new Card_item("NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date()),date,false));
        mElist.get(position).setId(System.currentTimeMillis());
        String pname = "NuScan_Batch_" + System.currentTimeMillis() + ".pdf";
        mElist.get(position).setPdfname(pname);
        mAdapter.notifyItemInserted(position);
        saveData(mElist);
    }

    private void remove_item(int position)
    {
        mElist.remove(position);
        mAdapter.notifyItemRemoved(position);
        saveData(mElist);
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
        saveData(mElist);
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
        saveData(mElist);
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
        saveData(mElist);
    }

    private void sortdatedesc()
    {
        Collections.sort(mElist, new Comparator<Card_item>() {
            @Override
            public int compare(Card_item o1, Card_item o2) {
                return o2.getDate().trim().compareTo(o1.getDate().trim());
            }
        });
        mAdapter.notifyDataSetChanged();
        saveData(mElist);
    }

    private void search()
    {
        searchfield.setVisibility(View.VISIBLE);
        search_cancel.setVisibility(View.VISIBLE);
        page_search.setVisibility(View.INVISIBLE);
        page_sort.setVisibility(View.INVISIBLE);
        select_items.setVisibility(View.INVISIBLE);
        card_add.setVisibility(View.INVISIBLE);
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
        mAdapter = new Rec_View_Adapter(mElist,MainActivity.this);
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
                else
                {
                    Intent intent = new Intent(MainActivity.this, Scanned_Files.class);
                    intent.putExtra("page_title",mElist.get(position).getTitle());
                    intent.putExtra("card_id",mElist.get(position).getId());
                    startActivity(intent);
                }
            }

            @Override
            public void OnItemLongClicked(int position) {
                mAdapter.setSelecttype(1);
                mElist.get(position).setSelected(true);
                selected_items.add(position);
                card_select_all.setVisibility(View.VISIBLE);
                card_add.setVisibility(View.INVISIBLE);
                card_delete.setVisibility(View.VISIBLE);
                card_multiple_share.setVisibility(View.VISIBLE);
                page_search.setVisibility(View.INVISIBLE);
                page_sort.setVisibility(View.INVISIBLE);
                selection_cancel.setVisibility(View.VISIBLE);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void OnItemShared(int position) {
                shareItem(position,mElist);
            }

            @Override
            public void OnTitleClicked(int position) {
                openEditDialog(position);
            }

            @Override
            public void NewListselect(int position, ArrayList<Card_item> list1) {
                Intent intent = new Intent(MainActivity.this, Scanned_Files.class);
                intent.putExtra("page_title",list1.get(position).getTitle());
                intent.putExtra("card_id",list1.get(position).getId());
                startActivity(intent);
            }

            @Override
            public void NewListshare(int position, ArrayList<Card_item> list1) {
                shareItem(position,list1);
            }
        });
    }

    private void shareItem(int position, ArrayList<Card_item> list1)
    {
        String[] options = {"PDF","JPG"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share")
        .setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0 : sharePDF(position,list1);
                    break;
                    case 1 : shareJPG(position,list1);
                    break;
                }
            }
        });
        builder.create().show();
    }

    private void sharePDF(int position,ArrayList<Card_item> list1)
    {
        try
        {
            long card_id = list1.get(position).getId();
            SharedPreferences sp = getSharedPreferences("id_"+card_id, MODE_PRIVATE);
            Gson gs = new Gson();
            String js = sp.getString("sub_doc_list"+card_id,null);
            Type type = new TypeToken<ArrayList<Card_sub_item>>(){}.getType();
            subshare_list = gs.fromJson(js,type);
            if(subshare_list==null)
            {
                subshare_list = new ArrayList<Card_sub_item>();
            }
            String destination = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
            java.io.File file = new java.io.File(destination);
            if (!file.exists()) {
                file.mkdir();
                Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
            }
            String pname = list1.get(position).getPdfname();
            String pdfname = destination + "/"+pname;
            java.io.File pdfFile = new java.io.File(pdfname);
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            Document document = new Document(new Rectangle(PageSize.A4),0,0,0,0);
            PdfWriter.getInstance(document,outputStream);
            document.open();
            for(int i=0;i<subshare_list.size();i++)
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(subshare_list.get(i).getImage()));
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.scaleToFit(new Rectangle(PageSize.A4));
                image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                float x = (PageSize.A4.getWidth() - image.getScaledWidth()) / 2;
                float y = (PageSize.A4.getHeight() - image.getScaledHeight()) / 2;
                image.setAbsolutePosition(x, y);
                document.add(image);
                document.newPage();
                stream.close();
            }
            document.close();
            outputStream.flush();
            outputStream.close();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            Uri pdfuri = FileProvider.getUriForFile(this, "com.example.nuscan.fileprovider", pdfFile);
            intent.putExtra(Intent.EXTRA_STREAM, pdfuri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "NuScan batch scanned file " + list1.get(position).getTitle());
            intent.putExtra(Intent.EXTRA_TEXT,"NuScan scanned file "+list1.get(position).getTitle());
            startActivity(Intent.createChooser(intent, "Share with.."));
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void shareJPG(int position,ArrayList<Card_item> list1)
    {
        try
        {
            long card_id = list1.get(position).getId();
            SharedPreferences sp = getSharedPreferences("id_" + card_id, MODE_PRIVATE);
            Gson gs = new Gson();
            String js = sp.getString("sub_doc_list" + card_id, null);
            Type type = new TypeToken<ArrayList<Card_sub_item>>() {
            }.getType();
            subshare_list = gs.fromJson(js, type);
            if (subshare_list == null) {
                subshare_list = new ArrayList<Card_sub_item>();
            }
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("*/*");
            ArrayList<Uri> mult_imgs = new ArrayList<Uri>();
            for (Card_sub_item sub_item : subshare_list) {
                mult_imgs.add(Uri.parse(sub_item.getImage()));
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mult_imgs);
            intent.putExtra(Intent.EXTRA_SUBJECT,"NuScan batch scanned files " + list1.get(position).getTitle());
            intent.putExtra(Intent.EXTRA_TEXT,"NuScan batch scanned file "+list1.get(position).getTitle());
            startActivity(Intent.createChooser(intent, "Share with.."));
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void share_bulk()
    {
        String[] options = {"PDF","JPG"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which)
                        {
                            case 0 : share_bulk_PDF();
                                break;
                            case 1 : share_bulk_JPG();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    private void share_bulk_PDF()
    {
        try
        {
            int j=0;
            String destination = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
            java.io.File file = new java.io.File(destination);
            if (!file.exists()) {
                file.mkdir();
                Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
            }
            String pname = "NuScan_Bulk_"+System.currentTimeMillis()+".pdf";
            String pdfname = destination+"/"+pname;
            java.io.File pdfFile = new java.io.File(pdfname);
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            Document document = new Document(new Rectangle(PageSize.A4),0,0,0,0);
            PdfWriter.getInstance(document,outputStream);
            document.open();
            for(Integer integer : selected_items)
            {
                long card_id = mElist.get((int) integer).getId();
                SharedPreferences sp = getSharedPreferences("id_"+card_id, MODE_PRIVATE);
                Gson gs = new Gson();
                String js = sp.getString("sub_doc_list"+card_id,null);
                Type type = new TypeToken<ArrayList<Card_sub_item>>(){}.getType();
                subshare_list = gs.fromJson(js,type);
                if(subshare_list==null)
                {
                    subshare_list = new ArrayList<Card_sub_item>();
                }
                for(int i=0;i<subshare_list.size();i++)
                {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(subshare_list.get(i).getImage()));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                    Image image = Image.getInstance(stream.toByteArray());
                    image.scaleToFit(new Rectangle(PageSize.A4));
                    image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
                    float x = (PageSize.A4.getWidth() - image.getScaledWidth()) / 2;
                    float y = (PageSize.A4.getHeight() - image.getScaledHeight()) / 2;
                    image.setAbsolutePosition(x, y);
                    document.add(image);
                    document.newPage();
                    stream.close();
                }
            }
            document.close();
            outputStream.flush();
            outputStream.close();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            Uri pdfuri = FileProvider.getUriForFile(this, "com.example.nuscan.fileprovider", pdfFile);
            intent.putExtra(Intent.EXTRA_STREAM, pdfuri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "NuScan Bulk scanned file " + date);
            intent.putExtra(Intent.EXTRA_TEXT,"NuScan Bulk scanned file "+ date);
            startActivity(Intent.createChooser(intent, "Share with.."));
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        mAdapter.setSelecttype(0);
        for(int i=0;i<mElist.size();i++)
        {
            mElist.get(i).setSelected(false);
        }
        selected_items = new ArrayList<>();
        mAdapter.notifyDataSetChanged();
        card_select_all.setVisibility(View.INVISIBLE);
        card_add.setVisibility(View.VISIBLE);
        page_search.setVisibility(View.VISIBLE);
        page_sort.setVisibility(View.VISIBLE);
        selection_cancel.setVisibility(View.INVISIBLE);
        card_delete.setVisibility(View.INVISIBLE);
        card_multiple_share.setVisibility(View.INVISIBLE);
        saveData(mElist);
    }

    private void share_bulk_JPG()
    {
        try
        {
            ArrayList<Uri> imagelist12 = new ArrayList<Uri>();
            for(Integer integer : selected_items)
            {
                long card_id = mElist.get((int) integer).getId();
                SharedPreferences sp = getSharedPreferences("id_" + card_id, MODE_PRIVATE);
                Gson gs = new Gson();
                String js = sp.getString("sub_doc_list" + card_id, null);
                Type type = new TypeToken<ArrayList<Card_sub_item>>() {
                }.getType();
                subshare_list = gs.fromJson(js, type);
                if (subshare_list == null) {
                    subshare_list = new ArrayList<Card_sub_item>();
                }
                for(Card_sub_item sub_item : subshare_list)
                {
                    imagelist12.add(Uri.parse(sub_item.getImage()));
                }
            }
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setType("*/*");
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imagelist12);
            intent.putExtra(Intent.EXTRA_SUBJECT,"NuScan Bulk scanned files " + date);
            intent.putExtra(Intent.EXTRA_TEXT,"NuScan Bulk scanned file "+ date);
            startActivity(Intent.createChooser(intent, "Share with.."));
            mAdapter.setSelecttype(0);
            for(int i=0;i<mElist.size();i++)
            {
                mElist.get(i).setSelected(false);
            }
            selected_items = new ArrayList<>();
            mAdapter.notifyDataSetChanged();
            card_select_all.setVisibility(View.INVISIBLE);
            card_add.setVisibility(View.VISIBLE);
            page_search.setVisibility(View.VISIBLE);
            page_sort.setVisibility(View.VISIBLE);
            selection_cancel.setVisibility(View.INVISIBLE);
            card_delete.setVisibility(View.INVISIBLE);
            card_multiple_share.setVisibility(View.INVISIBLE);
            saveData(mElist);
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
                page_search.setVisibility(View.VISIBLE);
                page_sort.setVisibility(View.VISIBLE);
                selection_cancel.setVisibility(View.INVISIBLE);
                card_delete.setVisibility(View.INVISIBLE);
                card_multiple_share.setVisibility(View.INVISIBLE);
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

    private void openEditDialog(int position)
    {
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.edit_dialog,null);
        EditText asdf = view.findViewById(R.id.edit_title);
        asdf.setText(mElist.get(position).getTitle());
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit title")
        .setView(view)
        .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!(asdf.getText().toString().trim().equals("")||asdf.getText().toString().trim()==null))
                {
                    mElist.get(position).setTitle(asdf.getText().toString().trim());
                    mAdapter.notifyDataSetChanged();
                    saveData(mElist);
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_info: Intent intent = new Intent(MainActivity.this,App_About.class);
            startActivity(intent);
            break;
            case R.id.app_feedback: Intent intent1 = new Intent(MainActivity.this,App_Feedback.class);
            startActivity(intent1);
            break;
            case R.id.app_backup:   backupRoutine();
            break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void backupRoutine()
    {
        String[] options = {"Create a local Backup","Restore from a local Backup"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Backup")
        .setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which)
                {
                    case 0: createLocalBackup();
                        break;
                    case 1: loadFromLocalBackup();
                        break;
                }
            }
        })
        .create().show();
    }

    private void createLocalBackup()
    {
        String folderPath;
        String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getParentFile().getAbsolutePath()+"/NuScan Backup";
        File file = new File(path1);
        if(!file.exists())
        {
            file.mkdirs();
        }
        folderPath = file.getAbsolutePath();
        if(file.exists())
        {
            Toast.makeText(MainActivity.this, "Backup folder created successfully", Toast.LENGTH_SHORT).show();
            generateBackup(folderPath);
        }
        else
        {
            Toast.makeText(MainActivity.this, "Could not create Backup Folder", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFromLocalBackup()
    {
        String folderPath;
        String path1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getParentFile().getAbsolutePath()+"/NuScan Backup";
        File file = new File(path1);
        if(!file.exists())
        {
            Toast.makeText(MainActivity.this, "No Backups were generated", Toast.LENGTH_SHORT).show();
        }
        else
        {
            folderPath = file.getAbsolutePath();

        }
    }

    private void generateBackup(String path)
    {
        try
        {
            File file = new File(path + "/Backup " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + " " + new SimpleDateFormat("HH:mm").format(new Date()) + ".txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            File imgbackup = new File(path + "/Image Backup");
            if (!imgbackup.exists())
                imgbackup.mkdirs();
            ArrayList<Card_sub_item> tempList12;
            for (Card_item item : mElist) {
                printWriter.println("ItemStart:");
                printWriter.flush();
                printWriter.println(item.getTitle());
                printWriter.flush();
                printWriter.println(item.getDate());
                printWriter.flush();
                printWriter.println(item.isSelected());
                printWriter.flush();
                printWriter.println(item.getId());
                printWriter.flush();
                printWriter.println(item.getImage());
                printWriter.flush();
                printWriter.println(item.getPdfname());
                printWriter.flush();
                printWriter.println("SubItemsStart:");
                long id = item.getId();
                SharedPreferences sp_sub = getSharedPreferences("id_" + id, MODE_PRIVATE);
                Gson gs_sub = new Gson();
                String js_sub = sp_sub.getString("sub_doc_list" + id, null);
                Type type_sub = new TypeToken<ArrayList<Card_sub_item>>() {
                }.getType();
                tempList12 = gs_sub.fromJson(js_sub, type_sub);
                if (tempList12 != null && tempList12.size() > 0) {
                    for(Card_sub_item sub_item : tempList12)
                    {
                        FileChannel source = new FileInputStream(sub_item.getImage()).getChannel();
                        FileChannel destination = new FileOutputStream(new File(imgbackup.getAbsolutePath()+"/"+sub_item.getImageName())).getChannel();
                        destination.transferFrom(source,0,source.size());
                        if(source!=null)
                            source.close();
                        if(destination!=null)
                            destination.close();
                        printWriter.println("SubItemStart:");
                        printWriter.flush();
                        printWriter.println(sub_item.getTitle());
                        printWriter.flush();
                        printWriter.println(sub_item.getImage());
                        printWriter.flush();
                        printWriter.println(sub_item.getPdf());
                        printWriter.flush();
                        printWriter.println(sub_item.getImageName());
                        printWriter.flush();
                        printWriter.println(sub_item.getPdfname());
                        printWriter.flush();
                    }
                }
                printWriter.println("ItemEnd");
                printWriter.flush();
            }
            printWriter.flush();
            printWriter.close();
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        catch (Exception e)
        {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}