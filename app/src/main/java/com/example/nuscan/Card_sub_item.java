package com.example.nuscan;

public class Card_sub_item {

private String title;
private String image = null;
private String pdf;

public Card_sub_item(String title1, String image1, String pdf1)
{
    this.title = title1;
    this.image = image1;
    this.pdf = pdf1;
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

    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
}
