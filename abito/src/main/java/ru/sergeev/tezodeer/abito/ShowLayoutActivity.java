package ru.sergeev.tezodeer.abito;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.sergeev.tezodeer.abito.utils.MyConstants;

public class ShowLayoutActivity extends AppCompatActivity {

    private TextView tvTitle, tvPrice, tvDisk, tvTel;
    private ImageView imMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout_activity);
        init();
    }
    private void init()
    {
        tvTitle = findViewById(R.id.tvTitle);
        tvTel = findViewById(R.id.tvTel);
        tvPrice = findViewById(R.id.tvPrice);
        tvDisk = findViewById(R.id.tvDisk);
        imMain = findViewById(R.id.imMain);
        if(getIntent() != null)
        {
            Intent i = getIntent();
            tvTitle.setText(i.getStringExtra(MyConstants.TITLE));
            tvTel.setText(i.getStringExtra(MyConstants.TEL));
            tvPrice.setText(i.getStringExtra(MyConstants.PRICE));
            tvDisk.setText(i.getStringExtra(MyConstants.DISK));
            Picasso.get().load(i.getStringExtra(MyConstants.IMAGE_ID)).into(imMain);
        }
    }
}