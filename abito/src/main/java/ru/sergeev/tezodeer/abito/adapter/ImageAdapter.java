package ru.sergeev.tezodeer.abito.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import ru.sergeev.tezodeer.abito.R;

public class ImageAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater inflater;
    int[] imageArray = {R.drawable.one, R.drawable.two, R.drawable.three};

    public ImageAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.pager_items, container, false);
        ImageView imItem = view.findViewById(R.id.ImageViewPager);
        imItem.setImageResource(imageArray[position]);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imageArray.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout)object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
