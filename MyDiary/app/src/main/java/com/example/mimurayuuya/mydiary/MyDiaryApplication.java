package com.example.mimurayuuya.mydiary;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mimurayuuya on 2017/07/31.
 */

public class MyDiaryApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);

        RealmConfiguration realmConfig =
                new RealmConfiguration.Builder()
                        .migration(new Migration()).build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
