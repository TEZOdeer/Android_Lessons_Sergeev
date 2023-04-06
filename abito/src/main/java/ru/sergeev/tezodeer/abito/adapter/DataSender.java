package ru.sergeev.tezodeer.abito.adapter;

import java.util.List;

import ru.sergeev.tezodeer.abito.NewPost;

public interface DataSender {
    public void onDataRecived(List<NewPost> listdata);
}
