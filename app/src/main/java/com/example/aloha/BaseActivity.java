package com.example.aloha;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aloha.utilities.Constants;
import com.example.aloha.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if(preferenceManager.getString(Constants.KEY_USER_ID) != null){
            documentReference = database.collection(Constants.KEY_COLLECTION_USERS)
                    .document(preferenceManager.getString(Constants.KEY_USER_ID));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG_onPause", "onPause: ");
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getString(Constants.KEY_USER_ID) != null) {
            documentReference.update(Constants.KEY_AVAILABILITY, 0);
            preferenceManager.putString(Constants.KEY_AVAILABILITY, "0");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG_onResume", "onResume: ");
        PreferenceManager preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getString(Constants.KEY_USER_ID) != null){
            documentReference.update(Constants.KEY_AVAILABILITY, 1);
            preferenceManager.putString(Constants.KEY_AVAILABILITY, "1");
        }
    }
}
