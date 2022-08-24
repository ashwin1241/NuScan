package com.example.nuscan;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

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
    private String image_name = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ItemTouchHelper touchHelper;
    private ArrayList<Card_item> mElist1;
    private String day;
    private String date = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    private ProgressDialog progressDialog;
    private DataBase dataBase;
    private Queries queries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_files);
        getSupportActionBar().setTitle("File");
        getSupportActionBar().setHomeButtonEnabled(true);

        page_title = null;
        page_title = getIntent().getStringExtra("page_title");
        if(page_title!=null)
        {
            getSupportActionBar().setTitle(page_title);
        }
        card_id = getIntent().getLongExtra("card_id",0);

        instantiateDataBase();

        loadData();
        loadDataMain();
        buildrecyclerview();
        checkIntent();

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

        swipeRefreshLayout = findViewById(R.id.file_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                buildrecyclerview();
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.START|ItemTouchHelper.DOWN|ItemTouchHelper.END,0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromposition = viewHolder.getAdapterPosition();
                int toposition = target.getAdapterPosition();
                int i=0;
                if(fromposition>=toposition)
                {
                    for (i = fromposition - 1; i >= toposition; i--) {
                        Collections.swap(mElist, i+1, i );
                    }
                }
                else
                {
                    for (i = fromposition + 1; i <= toposition; i++) {
                        Collections.swap(mElist, i-1, i );
                    }
                }
                //Collections.swap(mElist,fromposition,i);
                recyclerView.getAdapter().notifyItemMoved(fromposition,toposition);
//                saveData(mElist);
                queries.deleteAllSpecificSubItems(card_id);
                queries.insertAllSubItems(mElist);
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void loadDataMain()
    {
        mElist1 = (ArrayList<Card_item>) queries.getAllItems();
        if(mElist1==null)
            mElist1 = new ArrayList<>();
    }

    private void checkIntent()
    {
        page_title = getIntent().getStringExtra("page_title");
        if(page_title==null)
        onActivityResult(155,RESULT_OK,getIntent());
    }

    private void loadData()
    {
        instantiateDataBase();
        mElist = (ArrayList<Card_sub_item>) queries.getAllSubItems(card_id);
        if(mElist==null)
            mElist = new ArrayList<>();
    }

    private synchronized void instantiateDataBase()
    {
        progressDialog = new ProgressDialog(Scanned_Files.this);
        progressDialog.setMessage("Fetching your data");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        dataBase = DataBase.getInstance(Scanned_Files.this);
        progressDialog.setMessage("Activating queries");
        queries = dataBase.itemQueries();
        progressDialog.dismiss();
    }

    private void insert_gallery_item(int position)
    {
        temp_position = position;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
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
        image_name = page_title+System.currentTimeMillis()+".jpg";
        String imigname = destination+"/"+image_name;
        java.io.File imgFile = new java.io.File(imigname);
        camuri = FileProvider.getUriForFile(this,"com.example.nuscan.fileprovider",imgFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,camuri);
        startActivityForResult(intent,132);
    }

    private void buildrecyclerview()
    {
        mRecyclerView = findViewById(R.id.sub_file_recview);
        mLayoutManager = new GridLayoutManager(this,2);
        mAdapter = new Rec_View_Sub_Adatper(mElist,Scanned_Files.this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new Rec_View_Sub_Adatper.OnItemClickListener() {
            @Override
            public void OnItemClicked(int position) {
                Intent intent = new Intent(Scanned_Files.this,Preview.class);
                intent.putExtra("previmg",Uri.parse(mElist.get(position).getImage()));
                intent.putExtra("name",mElist.get(position).getImgname());
                intent.putExtra("title","NuScan scanned file "+mElist.get(position).getTitle());
                intent.putExtra("pdfname",mElist.get(position).getPdfname());
                intent.putExtra("position",position);
                intent.putExtra("card_id",card_id);
                intent.putExtra("page title",page_title);
                startActivity(intent);
            }

            @Override
            public void OnTitleClicked(int position) {
                openEditDialog(position);
            }

        });

    }

    private void openEditDialog(int position)
    {
        View view = LayoutInflater.from(Scanned_Files.this).inflate(R.layout.edit_dialog,null);
        EditText asdf = view.findViewById(R.id.edit_title);
        asdf.setText(mElist.get(position).getTitle());
        AlertDialog.Builder builder = new AlertDialog.Builder(Scanned_Files.this);
        builder.setTitle("Edit title")
        .setView(view)
        .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!(asdf.getText().toString().trim().equals("")||asdf.getText().toString().trim()==null))
                {
                    mElist.get(position).setTitle(asdf.getText().toString().trim());
                    mAdapter.notifyDataSetChanged();
//                    saveData(mElist);
                    queries.updateSubItem(mElist.get(position));
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 154 && resultCode == RESULT_OK && data != null)
        {
            ClipData clipData = data.getClipData();
            if(clipData!=null)
            {
                for(int i=0;i<clipData.getItemCount();i++)
                {
                    imguri = clipData.getItemAt(i).getUri();
                    Bitmap image = null;
                    try {
                        image = correctedBitmap(imguri);
                        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        if(!file.exists())
                        {
                            file.mkdir();
                            Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                        }
                        image_name = page_title+System.currentTimeMillis()+".jpg";
                        String imgname = file.getAbsolutePath()+"/"+image_name;
                        File imgfile = new File(imgname);
                        FileOutputStream outputStream = new FileOutputStream(imgfile);
                        image.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                        outputStream.flush();
                        outputStream.close();
                        imguri = FileProvider.getUriForFile(this,"com.example.nuscan.fileprovider",imgfile);
                    } catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if(imguri != null)
                    {
                        String pname = "NuScan_"+System.currentTimeMillis()+".pdf";
                        Card_sub_item item = new Card_sub_item(page_title+"_"+String.valueOf(temp_position+i),imguri.toString(), card_id, image_name,pname, System.currentTimeMillis());
                        mElist.add(temp_position,item);
                        mAdapter.notifyItemInserted(temp_position);
                        Toast.makeText(Scanned_Files.this, "File saved", Toast.LENGTH_SHORT).show();
//                        saveData1(mElist,card_id);
                        queries.insertSubItem(item);
                        mAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                imguri = data.getData();
                Bitmap image = null;
                try {
                    image = correctedBitmap(imguri);
                    File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    if(!file.exists())
                    {
                        file.mkdir();
                        Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                    }
                    image_name = page_title+System.currentTimeMillis()+".jpg";
                    String imgname = file.getAbsolutePath()+"/"+image_name;
                    File imgfile = new File(imgname);
                    FileOutputStream outputStream = new FileOutputStream(imgfile);
                    image.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    outputStream.flush();
                    outputStream.close();
                    imguri = FileProvider.getUriForFile(this,"com.example.nuscan.fileprovider",imgfile);
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if(imguri != null)
                {
                    String pname = "NuScan_"+System.currentTimeMillis()+".pdf";
                    Card_sub_item item = new Card_sub_item(page_title+"_"+temp_position,imguri.toString(), card_id, image_name,pname, System.currentTimeMillis());
                    mElist.add(temp_position,item);
                    mAdapter.notifyItemInserted(temp_position);
                    Toast.makeText(Scanned_Files.this, "File saved", Toast.LENGTH_SHORT).show();
//                    saveData1(mElist,card_id);
                    queries.insertSubItem(item);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode == 155 && resultCode == RESULT_OK && data != null)
        {
            int aret = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
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
            String pname = "NuScan_Batch_" + System.currentTimeMillis() + ".pdf";
            Card_item item1 = new Card_item("NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date()),date,false,System.currentTimeMillis(),null,pname);
            mElist1.add(0, item1);
            //saveDataMain(mElist1);
            queries.insertItem(item1);
            getSupportActionBar().setTitle("NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date()));
            getSupportActionBar().setHomeButtonEnabled(true);
            ClipData clipData = data.getClipData();
            if(clipData!=null)
            {
                for(int i=0;i<clipData.getItemCount();i++)
                {
                    imguri = clipData.getItemAt(i).getUri();
                    Bitmap image = null;
                    try {
                        image = correctedBitmap(imguri);
                        File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        if(!file.exists())
                        {
                            file.mkdir();
                            Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                        }
                        image_name = "NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date())+".jpg";
                        String imgname = file.getAbsolutePath()+"/"+image_name;
                        File imgfile = new File(imgname);
                        FileOutputStream outputStream = new FileOutputStream(imgfile);
                        image.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                        outputStream.flush();
                        outputStream.close();
                        imguri = FileProvider.getUriForFile(this,"com.example.nuscan.fileprovider",imgfile);
                    } catch (IOException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    if(imguri != null)
                    {
                        String pname12 = "NuScan_"+System.currentTimeMillis()+".pdf";
                        Card_sub_item item = new Card_sub_item("NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date())+"_"+i,imguri.toString(), card_id, image_name,pname12, System.currentTimeMillis());
                        mElist.add(0,item);
                        mAdapter.notifyItemInserted(temp_position);
                        Toast.makeText(Scanned_Files.this, "File saved", Toast.LENGTH_SHORT).show();
                        //saveData1(mElist,mElist1.get(0).getId());
                        queries.insertSubItem(item);
                        mAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else
            {
                imguri = data.getData();
                Bitmap image = null;
                try {
                    image = correctedBitmap(imguri);
                    File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    if(!file.exists())
                    {
                        file.mkdir();
                        Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
                    }
                    image_name = "NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date())+System.currentTimeMillis()+".jpg";
                    String imgname = file.getAbsolutePath()+"/"+image_name;
                    File imgfile = new File(imgname);
                    FileOutputStream outputStream = new FileOutputStream(imgfile);
                    image.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                    outputStream.flush();
                    outputStream.close();
                    imguri = FileProvider.getUriForFile(this,"com.example.nuscan.fileprovider",imgfile);
                } catch (IOException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if(imguri != null)
                {
                    String pname12 = "NuScan_"+System.currentTimeMillis()+".pdf";
                    Card_sub_item item = new Card_sub_item("NuScan_"+day+"_"+ new SimpleDateFormat("HH:mm").format(new Date())+"_0",imguri.toString(), card_id, image_name,pname12, System.currentTimeMillis());
                    mElist.add(0,item);
                    mAdapter.notifyItemInserted(temp_position);
                    Toast.makeText(Scanned_Files.this, "File saved", Toast.LENGTH_SHORT).show();
                    //saveData1(mElist,mElist1.get(0).getId());
                    queries.insertSubItem(item);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(requestCode == 132 && resultCode == RESULT_OK)
        {
            if(camuri!=null)
            {
                try {
                    cameracorrection(image_name, MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),camuri));
                }
                catch (Exception e)
                {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                String pname = "NuScan_"+System.currentTimeMillis()+".pdf";
                Card_sub_item item = new Card_sub_item(page_title+"_"+temp_position,camuri.toString(), card_id, image_name,pname, System.currentTimeMillis());
                mElist.add(temp_position,item);
                mAdapter.notifyItemInserted(temp_position);
                Toast.makeText(Scanned_Files.this, "File saved", Toast.LENGTH_SHORT).show();
                //saveData(mElist);
                queries.insertSubItem(item);
                mAdapter.notifyDataSetChanged();
            }
            else
            {
                Toast.makeText(this, "Image could not be saved", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cameracorrection(String name, Bitmap bitmap)
    {
        try
        {
            File file = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String path = file.getAbsolutePath() + "/" + name;
            Matrix matrix = new Matrix();
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90: matrix.postRotate(90);
                break;
                case ExifInterface.ORIENTATION_ROTATE_180: matrix.postRotate(180);
                break;
                case ExifInterface.ORIENTATION_ROTATE_270: matrix.postRotate(270);
                break;
            }
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            File file1 = new File(path);
            FileOutputStream outputStream = new FileOutputStream(file1);
            bitmap1.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
            outputStream.flush();
            outputStream.close();
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap correctedBitmap(Uri uri)
    {
        try
        {
            Matrix matrix = new Matrix();
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
            int orientation;
            Cursor cursor = getApplicationContext().getContentResolver().query(uri, new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);
            if (cursor.getCount() != 1) {
                orientation = -1;
            }
            cursor.moveToFirst();
            orientation = cursor.getInt(0);
            switch (orientation)
            {
                case 90: matrix.postRotate(90);
                    break;
                case 180: matrix.postRotate(180);
                    break;
                case 270: matrix.postRotate(270);
                    break;
            }
            Bitmap bitmap1 = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            return bitmap1;
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return  null;
    }

}