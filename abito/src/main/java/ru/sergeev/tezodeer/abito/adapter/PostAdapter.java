package ru.sergeev.tezodeer.abito.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.List;

import ru.sergeev.tezodeer.abito.NewPost;
import ru.sergeev.tezodeer.abito.R;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolderData> {
    private List<NewPost> arrayPost;
    private Context context;
    private OnItemClickCustom onItemClickCustom;

    public PostAdapter(List<NewPost> arrayPost, Context context, OnItemClickCustom onItemClickCustom) {
        this.arrayPost = arrayPost;
        this.context = context;
        this.onItemClickCustom = this.onItemClickCustom;
    }



    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ads, parent, false);
        return new ViewHolderData(view, onItemClickCustom);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.SetData(arrayPost.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayPost.size();
    }

    public class ViewHolderData extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView tvPriceTel, tvDisc, tvTitle;
        private ImageView imAds;
        private OnItemClickCustom onItemClickCustom;


        public ViewHolderData(@NonNull View itemView, OnItemClickCustom onItemClickCustom) {
            super(itemView);
            tvPriceTel = itemView.findViewById(R.id.tvPriceTel);
            tvDisc = itemView.findViewById(R.id.tvDisk);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imAds = itemView.findViewById(R.id.imAds);
            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;
        }
        public void SetData(@NonNull NewPost newPost)
        {
            Picasso.get().load(newPost.getImageId()).into(imAds);
            tvTitle.setText(newPost.getTitle());
            String price_tel = "Цена: " + newPost.getPrice() + " Тел: " + newPost.getTel();
            tvPriceTel.setText(price_tel);
            tvDisc.setText(newPost.getDisk());
        }

        @Override
        public void onClick(View v) {
            onItemClickCustom.onItemSelected(getAdapterPosition());
        }
    }
    public interface OnItemClickCustom
    {
        public void onItemSelected(int position);
    }
    public void updateAdapter(List<NewPost> listData)
    {
        arrayPost.clear();
        arrayPost.addAll(listData);
        notifyDataSetChanged();
    }
}
