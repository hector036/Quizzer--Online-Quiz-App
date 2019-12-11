package com.example.quizzer_onlinequizapp;

import android.app.Application;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        final FirebaseRemoteConfig remoteConfig  = FirebaseRemoteConfig.getInstance();

        Map<String,Object> defaultvalue = new HashMap<>();
        defaultvalue.put(UpdateHelper.KEY_UPDATE_ENABLE,false);
        defaultvalue.put(UpdateHelper.KEY_UPDATE_VERSION,"1.0");
        defaultvalue.put(UpdateHelper.KEY_UPDATE_URL,"link");

        remoteConfig.setDefaults(defaultvalue);
        remoteConfig.fetch(60).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    remoteConfig.activateFetched();
                }
            }
        });
    }
}
