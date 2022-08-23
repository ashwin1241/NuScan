package com.example.nuscan;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Card_item implements Serializable {

    private String title;
    private String date;
    private boolean isSelected;
    @PrimaryKey(autoGenerate = false)
    private long id;
    private String image;
    private String pdfname;

    public Card_item(String title, String date, boolean isSelected, long id, String image, String pdfname) {
        this.title = title;
        this.date = date;
        this.isSelected = isSelected;
        this.id = id;
        this.image = image;
        this.pdfname = pdfname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPdfname() {
        return pdfname;
    }

    public void setPdfname(String pdfname) {
        this.pdfname = pdfname;
    }
}
