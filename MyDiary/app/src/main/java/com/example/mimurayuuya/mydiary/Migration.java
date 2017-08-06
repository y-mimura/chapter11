package com.example.mimurayuuya.mydiary;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by mimurayuuya on 2017/08/06.
 */

public class Migration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();

        /***********************************
          Version 0
          Class Diary
                Public long id;
                public String title;
                public String bodyText;
                public String date;
                public byte[] image;

          Version 1
          Class Diary
                Public long id;
                public String title;
                public String bodyText;
                public String date;
                public String location;    ←add
                public byte[] image;
         ***********************************/

        if (oldVersion == 0){
            RealmObjectSchema diarySchema = schema.get("Diary");

            // location列追加
            diarySchema.addField("location",String.class);
            oldVersion++;
        }

    }
}
