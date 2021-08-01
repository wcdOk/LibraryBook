package com.wcdok.librarybook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("wcd","onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("wcd","onResume");
    }
}