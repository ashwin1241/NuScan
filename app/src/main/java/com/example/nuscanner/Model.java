package com.example.nuscanner;

public class Model {

    private String imageUrl;

    public Model(String abc)
    {
        this.imageUrl = abc;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
