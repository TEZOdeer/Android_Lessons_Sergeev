package ru.sergeev.listview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
    public void Start (View v) {
        Intent Start = new Intent(this, MainActivity.class);
        startActivity(Start);
        overridePendingTransition(0, 0);
    }
}