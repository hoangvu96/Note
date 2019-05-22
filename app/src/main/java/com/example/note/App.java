package com.example.note;

import android.app.Application;
import android.support.annotation.NonNull;

import com.example.note.injection.AppComponent;
import com.example.note.injection.AppModule;
import com.example.note.injection.DaggerAppComponent;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public final class App extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("MyNote")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @NonNull
    public AppComponent getAppComponent() {
        return mAppComponent;
    }
}