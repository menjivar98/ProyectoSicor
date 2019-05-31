package com.fmenjivar.sicor.models;

import java.sql.Timestamp;

public class DangerPost {

    private String user_id,image_url,description,danger,image_thumb;
    private Timestamp timestamp;

    public DangerPost(){}

    public DangerPost(String user_id, String image_url, String description, String danger, String image_thumb, Timestamp timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.description = description;
        this.danger = danger;
        this.image_thumb = image_thumb;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDanger() {
        return danger;
    }

    public void setDanger(String danger) {
        danger = danger;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }


}