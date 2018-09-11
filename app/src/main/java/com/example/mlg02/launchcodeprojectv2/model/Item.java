package com.example.mlg02.launchcodeprojectv2.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Item {


    public String uid;
    public String author;
    public String title;
    public String body;




    public Item() {

    }

    public Item(String uid, String author,String title, String body) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
    }


    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("title",title);
        result.put("body",body);

        return result;
    }



}
