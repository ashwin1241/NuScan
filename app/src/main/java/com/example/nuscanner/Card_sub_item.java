package com.example.nuscanner;

import android.net.Uri;

public class Card_sub_item {

private String title;
private Uri image;

public Card_sub_item(String title1, Uri image1)
{
    this.title = title1;
    this.image = image1;
}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
