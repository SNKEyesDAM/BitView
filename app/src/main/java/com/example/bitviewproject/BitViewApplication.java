package com.example.bitviewproject;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class BitViewApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .name("BitViewDataBase.realm")
                .schemaVersion(1)
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

}
