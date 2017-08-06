package com.example.mimurayuuya.mydiary;

import android.location.Location;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mimurayuuya on 2017/07/31.
 */

public class Diary extends RealmObject {
    @PrimaryKey
    public long id;
    public String title;
    public String bodyText;
    public String date;
    public String location;
    public byte[] image;
}
