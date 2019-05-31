package com.fmenjivar.sicor.models;

import java.sql.Timestamp;

public class DangerPost {

    private String user_id,image_url,description,Danger,image_thumb;
    private Timestamp timestamp;

    public DangerPost(){}

    public DangerPost(String user_id, String image_url, String description, String danger, String image_thumb, Timestamp timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.description = description;
        Danger = danger;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
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
        return Danger;
    }

    public void setDanger(String danger) {
        Danger = danger;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
