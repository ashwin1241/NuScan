package com.example.nuscan;

public class Card_item {

    private String title;
    private String date;
    private boolean isSelected;
    private long id;
    private String image;
    private String pdfname;

    public Card_item(String mtitle, String mdate, boolean misSelected)
    {
        this.title = mtitle;
        this.date = mdate;
        this.isSelected = misSelected;
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
