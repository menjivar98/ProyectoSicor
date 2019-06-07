package com.fmenjivar.sicor.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class DangerPostId  {

    @Exclude
    public String DangerPostID;

    public <T extends DangerPostId> T withId(@NonNull final String id){
        return (T) this;
    }

}
