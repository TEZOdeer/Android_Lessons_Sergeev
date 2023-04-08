package ru.sergeev.tezodeer.abito;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import ru.sergeev.tezodeer.abito.adapter.DataSender;

public class DbManager {
    private Query mQuery;
    private List<NewPost> newPostList;
    private DataSender dataSender;
    private FirebaseDatabase db;
    private int cat_ads_counter = 0;
    private String[] category_ads = {"Машины", "Компьютеры", "Смартфоны", "Бытовая техника"};

    public void deleteitem()
    {

    }

    public DbManager(DataSender dataSender) {
        this.dataSender = dataSender;
        newPostList = new ArrayList<>();
        db = FirebaseDatabase.getInstance();
    }

    public void getDataFromDb(String path)
    {
        //if(newPostList.size() > 0) newPostList.clear();
        DatabaseReference dbRef = db.getReference(path);
        mQuery = dbRef.orderByChild("anuncio/time");
        readDataUpdate();

    }
    public void getMyAdsDataFromDb(String uid)
    {
        if(newPostList.size() > 0) newPostList.clear();
        DatabaseReference dbRef = db.getReference(category_ads[0]);
        mQuery = dbRef.orderByChild("anuncio/uid").equalTo(uid);
        readMyAdsDataUpdate(uid);
        cat_ads_counter++;
    }
    public void readDataUpdate()
    {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(newPostList.size() > 0) newPostList.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    NewPost newPost = ds.child("anuncio").getValue(NewPost.class);
                    newPostList.add(newPost);
                }
                dataSender.onDataRecived(newPostList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readMyAdsDataUpdate(final String uid)
    {
        mQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    NewPost newPost = ds.child("anuncio").getValue(NewPost.class);
                    newPostList.add(newPost);
                }
                if(cat_ads_counter > 3)
                {
                    dataSender.onDataRecived(newPostList);
                    newPostList.clear();
                    cat_ads_counter = 0;
                }
                else
                {
                    DatabaseReference dbRef = db.getReference(category_ads[cat_ads_counter]);
                    mQuery = dbRef.orderByChild("anuncio/uid").equalTo(uid);
                    readMyAdsDataUpdate(uid);
                    cat_ads_counter++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
