package com.example.nuscan;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Card_sub_item implements Serializable {

private String title;
private String image;
private String editedImage;
private long parent_id;
private String imgname;
private String pdfname;
@PrimaryKey(autoGenerate = false)
private long id;
@ColumnInfo(name = "inx")
private int index;

    public Card_sub_item(String title, String image, String editedImage, long parent_id, String imgname, @NonNull String pdfname, long id, int index) {
        this.title = title;
        this.image = image;
        this.editedImage = editedImage;
        this.parent_id = parent_id;
        this.imgname = imgname;
        this.pdfname = pdfname;
        this.id = id;
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public String getPdfname() {
        return pdfname;
    }

    public void setPdfname(@NonNull String pdfname) {
        this.pdfname = pdfname;
    }

    public long getParent_id() {
        return parent_id;
    }

    public void setParent_id(long parent_id) {
        this.parent_id = parent_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getEditedImage() {
        return editedImage;
    }

    public void setEditedImage(String editedImage) {
        this.editedImage = editedImage;
    }
}
