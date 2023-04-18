package ru.sergeev.tezodeer.abito.screens;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.sergeev.tezodeer.abito.R;

public class ChooseImagesActivity extends AppCompatActivity {
    private String uriMain, uri2, uri3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_images);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            switch (requestCode)
            {
                case 1:
                    uriMain = data.getData().toString();
                    break;
                case 2:
                    uri2 = data.getData().toString();
                    break;
                case 3:
                    uri3 = data.getData().toString();
                    break;
            }
        }
    }

    public void OnClickMainImage (View v) {
        getImage(1);
    }
    public void OnClickImage2 (View v) {
        getImage(2);
    }
    public void OnClickImage3 (View v) {
        getImage(3);
    }

    private void getImage(int index)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
}