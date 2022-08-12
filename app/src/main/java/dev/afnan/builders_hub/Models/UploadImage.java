package dev.afnan.builders_hub.Models;

import com.google.firebase.database.Exclude;

public class UploadImage {
    private String imageUrl;
    private String desc;
    private String key;

    public UploadImage() {

    }

    public UploadImage(String imageUrl, String desc) {
        this.imageUrl = imageUrl;
        this.desc = desc;
    }

    public String getImageUrl() {
        return imageUrl;
    }


    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
