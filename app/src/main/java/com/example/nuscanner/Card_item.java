package com.example.nuscanner;

public class Card_item {

    private String title;
    private String date;
    private String Url;
    private boolean isSelected;

    public Card_item(String mtitle, String mdate, String mUrl, boolean misSelected)
    {
        this.title = mtitle;
        this.date = mdate;
        this.Url = mUrl;
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

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
