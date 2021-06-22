package com.example.nuscan;

public class Card_sub_item {

private String title;
private String image = null;
private String pdf;
private String imgname;
private String pdfname;

public Card_sub_item(String title1, String image1, String pdf1, String imgname1)
{
    this.title = title1;
    this.image = image1;
    this.pdf = pdf1;
    this.imgname = imgname1;
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

    public String getName() {
        return imgname;
    }

    public void setName(String imgname) {
        this.imgname = imgname;
    }

    public String getPdfname() {
        return pdfname;
    }

    public void setPdfname(String pdfname) {
        this.pdfname = pdfname;
    }
}
