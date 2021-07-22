package com.example.nuscan;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Preview extends AppCompatActivity {

    private ImageView previmg;
    private ExtendedFloatingActionButton return_;
    private ExtendedFloatingActionButton edit;
    private Uri imguri;
    private String name;
    private String title;
    private String pdfname12;
    private int position;
    private long card_id;
    private ArrayList<Card_sub_item> mElist;
    private int edit_status=0;
    private String page_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        getSupportActionBar().setTitle("Preview");

        card_id = getIntent().getLongExtra("card_id",0);
        loadData();

        page_title = getIntent().getStringExtra("page title");
        imguri = getIntent().getParcelableExtra("previmg");
        name = getIntent().getStringExtra("name");
        title = getIntent().getStringExtra("title");
        pdfname12 = getIntent().getStringExtra("pdfname");
        position = getIntent().getIntExtra("position",0);
        card_id = getIntent().getLongExtra("card_id",0);
        previmg = findViewById(R.id.previmg);
        File mainfile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + name);
        if(mainfile.exists())
        {
            previmg.setImageURI(imguri);
            edit_status=1;
        }
        else
        {
            previmg.setImageResource(R.drawable.ic_outline_image_not_supported_24);
            edit_status=0;
        }
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
                if(edit_status==1)
                {
                    CropImage.activity(imguri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setGuidelinesColor(Color.parseColor("#54E2DB"))
                    .setMultiTouchEnabled(true)
                    .setBorderCornerColor(Color.parseColor("#54E2DB"))
                    .setBorderLineColor(Color.parseColor("#54E2DB"))
                    .setAutoZoomEnabled(true)
                    .setActivityTitle("Crop image")
                    .setAllowFlipping(true)
                    .setAllowRotation(true)
                    .setInitialCropWindowRectangle(new Rect())
                    .start(Preview.this);
                }
                if(edit_status==0)
                {
                    Toast.makeText(Preview.this, "Image not found", Toast.LENGTH_SHORT).show();
                }
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
                break;
            case R.id.preview_share: shareItem(imguri);
                break;
            case R.id.preview_delete: deleteItem();
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
            Toast.makeText(this, name+" edited and saved", Toast.LENGTH_SHORT).show();
            Toast.makeText(Preview.this, "Refresh the page to see changes", Toast.LENGTH_SHORT).show();
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

    private void shareItem(Uri uri)
    {
        String[] sub_objects = {"PDF","JPG"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(sub_objects, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(which)
                {
                    case 0 : sharepdf(uri);
                        break;
                    case 1 : sharejpg(uri);
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void sharepdf(Uri uri)
    {
        try
        {
            String destination = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
            java.io.File file = new java.io.File(destination);
            if (!file.exists()) {
                file.mkdir();
                Toast.makeText(this, "Folder created successfully", Toast.LENGTH_SHORT).show();
            }
            String pname = pdfname12;
            String pdfname = destination + "/" + pname;
            java.io.File pdfFile = new java.io.File(pdfname);
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
            Document document = new Document(new Rectangle(PageSize.A4),0,0,0,0);
            PdfWriter.getInstance(document,outputStream);
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
            float x = (PageSize.A4.getWidth() - image.getScaledWidth()) / 2;
            float y = (PageSize.A4.getHeight() - image.getScaledHeight()) / 2;
            image.setAbsolutePosition(x, y);
            document.add(image);
            stream.close();
            document.close();
            outputStream.flush();
            outputStream.close();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            Uri pdfuri = FileProvider.getUriForFile(this, "com.example.nuscan.fileprovider", pdfFile);
            intent.putExtra(Intent.EXTRA_STREAM, pdfuri);
            intent.putExtra(Intent.EXTRA_SUBJECT, title);
            intent.putExtra(Intent.EXTRA_TEXT, title);
            startActivity(Intent.createChooser(intent, "Share with.."));
        }
        catch (Exception e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sharejpg(Uri uri)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,title);
        startActivity(Intent.createChooser(intent,"Share with.."));
    }

    private void deleteItem()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(Preview.this);
        builder.setTitle("Delete")
                .setMessage("Are you sure you want to delete this file?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mElist.remove(position);
                        saveData(mElist);
                        Toast.makeText(Preview.this, "File deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Preview.this, Scanned_Files.class);
                        intent.putExtra("page_title",page_title);
                        intent.putExtra("card_id",card_id);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            imguri = CropImage.getActivityResult(data).getUri();
            previmg.setImageURI(imguri);
            Intent intent = new Intent(Preview.this, DsPhotoEditorActivity.class);
            intent.setData(imguri);
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY,"NuScan");
            intent.putExtra(DsPhotoEditorConstants.DS_MAIN_BACKGROUND_COLOR,Color.parseColor("#DEDEDE"));
            intent.putExtra(DsPhotoEditorConstants.DS_TOOL_BAR_BACKGROUND_COLOR,Color.parseColor("#54E2DB"));
            intent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE,new int[]{DsPhotoEditorActivity.TOOL_CROP,DsPhotoEditorActivity.TOOL_VIGNETTE,DsPhotoEditorActivity.TOOL_ROUND,DsPhotoEditorActivity.TOOL_FRAME,DsPhotoEditorActivity.TOOL_DRAW,DsPhotoEditorActivity.TOOL_ORIENTATION,DsPhotoEditorActivity.TOOL_PIXELATE,DsPhotoEditorActivity.TOOL_STICKER});
            startActivityForResult(intent,45);
        }
        if(requestCode == 45 && resultCode == RESULT_OK)
        {
            imguri = data.getData();
            previmg.setImageURI(imguri);
        }
    }

}