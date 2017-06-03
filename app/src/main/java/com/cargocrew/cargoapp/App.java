package com.cargocrew.cargoapp;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;
import io.realm.RealmConfiguration;



public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }
}