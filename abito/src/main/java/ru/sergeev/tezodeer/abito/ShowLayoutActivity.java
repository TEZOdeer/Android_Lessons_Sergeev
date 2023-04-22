package ru.sergeev.tezodeer.abito;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.sergeev.tezodeer.abito.adapter.ImageAdapter;
import ru.sergeev.tezodeer.abito.utils.MyConstants;

public class ShowLayoutActivity extends AppCompatActivity {

    private TextView tvTitle, tvPrice, tvDisk, tvTel, tvImagesCounter2;
    private List<String> imagesUris;
    private ImageAdapter imAdapter;
    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_layout_activity);
        init();
    }
    private void init()
    {

        vp = findViewById(R.id.view_pager2);
        tvImagesCounter2 = findViewById(R.id.tvImagesCounter2);
        imagesUris = new ArrayList<>();
        imAdapter = new ImageAdapter(this);
        vp.setAdapter(imAdapter);

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String dataText = position + 1 + "/" + imagesUris.size();
                tvImagesCounter2.setText(dataText);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tvTitle = findViewById(R.id.tvTitle);
        tvTel = findViewById(R.id.tvTel);
        tvPrice = findViewById(R.id.tvPrice);
        tvDisk = findViewById(R.id.tvDisk);

        if(getIntent() != null)
        {
            Intent i = getIntent();
            tvTitle.setText(i.getStringExtra(MyConstants.TITLE));
            tvTel.setText(i.getStringExtra(MyConstants.TEL));
            tvPrice.setText(i.getStringExtra(MyConstants.PRICE));
            tvDisk.setText(i.getStringExtra(MyConstants.DISK));
            String[] images = new String[3];
            images[0] = i.getStringExtra(MyConstants.IMAGE_ID);
            images[1] = i.getStringExtra(MyConstants.IMAGE_ID_2);
            images[2] = i.getStringExtra(MyConstants.IMAGE_ID_3);

            for (String s : images) {
                if(!s.equals("empty"))imagesUris.add(s);
            }
            imAdapter.updateImages(imagesUris);
            String dataText;

            if(imagesUris.size() > 0)
            {
            dataText = 1 + "/" + imagesUris.size();
            }
            else
            {
                dataText = 0 + "/" + imagesUris.size();
            }

            tvImagesCounter2.setText(dataText);

            //Picasso.get().load(i.getStringExtra(MyConstants.IMAGE_ID)).into(imMain);
        }
    }
}