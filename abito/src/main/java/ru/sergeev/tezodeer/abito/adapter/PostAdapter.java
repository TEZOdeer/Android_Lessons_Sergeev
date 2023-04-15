package ru.sergeev.tezodeer.abito.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import ru.sergeev.tezodeer.abito.DbManager;
import ru.sergeev.tezodeer.abito.EditActivity;
import ru.sergeev.tezodeer.abito.MainActivity;
import ru.sergeev.tezodeer.abito.NewPost;
import ru.sergeev.tezodeer.abito.R;
import ru.sergeev.tezodeer.abito.ShowLayoutActivity;
import ru.sergeev.tezodeer.abito.utils.MyConstants;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolderData> {
    private List<NewPost> arrayPost;
    private Context context;
    private OnItemClickCustom onItemClickCustom;
    private DbManager dbManager;
    private AlertDialog dialog;


    public PostAdapter(List<NewPost> arrayPost, Context context, OnItemClickCustom onItemClickCustom, NewPost newPost) {
        this.arrayPost = arrayPost;
        this.context = context;
        this.onItemClickCustom = onItemClickCustom;
        this.dbManager = dbManager;
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
        private TextView tvPriceTel, tvDisc, tvTitle, tvTotalViews;
        private ImageView imAds;
        private LinearLayout edit_layout;
        private OnItemClickCustom onItemClickCustom;
        private ImageButton deleteButton;
        private ImageButton editButton;
        private Spinner spinner;
        private ImageButton look;
        private ImageView image;
        private TextView title;
        private TextView price;
        private TextView tel;
        private TextView desk;


        public ViewHolderData(@NonNull View itemView, OnItemClickCustom onItemClickCustom) {
            super(itemView);
            tvTotalViews = itemView.findViewById(R.id.tvTotalViews);
            spinner = itemView.findViewById(R.id.spinner);
            tvPriceTel = itemView.findViewById(R.id.tvPriceTel);
            tvDisc = itemView.findViewById(R.id.tvDisk);
            edit_layout = itemView.findViewById(R.id.edit_layout);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            imAds = itemView.findViewById(R.id.ImageView);
            deleteButton = itemView.findViewById(R.id.imDeleteItem);
            editButton = itemView.findViewById(R.id.imEditItem);
            itemView.setOnClickListener(this);
            this.onItemClickCustom = onItemClickCustom;
        }
        public void SetData(@NonNull NewPost newPost)
        {
            if( newPost.getUid().equals(MainActivity.MAUTH))
            {
                edit_layout.setVisibility(itemView.VISIBLE);
            }
            else
            {
                edit_layout.setVisibility(itemView.GONE);
            }
            Picasso.get().load(newPost.getImageId()).into(imAds);
            tvTitle.setText(newPost.getTitle());
            String price_tel = "Цена: " + newPost.getPrice() + " Тел: " + newPost.getTel();
            tvPriceTel.setText(price_tel);
            String textDisk;
            if(newPost.getDisk().length() > 50)
            {
                textDisk = newPost.getDisk().substring(0, 50) + "...";
            }
            else
            {
                textDisk = newPost.getDisk();
            }
            tvDisc.setText(textDisk);
            tvTotalViews.setText(newPost.getTotal_views());

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog(newPost, getAdapterPosition());
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, EditActivity.class);
                    i.putExtra(MyConstants.IMAGE_ID, newPost.getImageId());
                    i.putExtra(MyConstants.TITLE, newPost.getTitle());
                    i.putExtra(MyConstants.PRICE, newPost.getPrice());
                    i.putExtra(MyConstants.TEL, newPost.getTel());
                    i.putExtra(MyConstants.DISK, newPost.getDisk());
                    i.putExtra(MyConstants.KEY, newPost.getKey());
                    i.putExtra(MyConstants.UID, newPost.getUid());
                    i.putExtra(MyConstants.TIME, newPost.getTime());
                    i.putExtra(MyConstants.CAT, newPost.getCat());
                    i.putExtra(MyConstants.EDIT_STATE, true);
                    i.putExtra(MyConstants.TOTAL_VIEWS,newPost.getTotal_views());
                    context.startActivity(i);
                }
            });
            //LOOK_init
            /*
            look.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    Look(newPost);
                }
            });
             */
        }

        @Override
        public void onClick(View v)
        {
            NewPost newPost = arrayPost.get(getAdapterPosition());
            dbManager.updateTotalViews(newPost);
            Intent i = new Intent(context, ShowLayoutActivity.class);
            i.putExtra(MyConstants.IMAGE_ID, newPost.getImageId());
            i.putExtra(MyConstants.TITLE, newPost.getTitle());
            i.putExtra(MyConstants.PRICE, newPost.getPrice());
            i.putExtra(MyConstants.TEL, newPost.getTel());
            i.putExtra(MyConstants.DISK, newPost.getDisk());
            context.startActivity(i);
            onItemClickCustom.onItemSelected(getAdapterPosition());
        }
    }
    private void deleteDialog(NewPost newPost, int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.delete_title);
        builder.setMessage(R.string.delete_message);
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbManager.deleteitem(newPost);
                arrayPost.remove(position);
                notifyItemRemoved(position);
            }
        });
        builder.show();
    }
    public interface OnItemClickCustom
    {
        void onItemSelected(int position);
    }
    //LOOK
    /*
    public void Look(NewPost newPost)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.view_ads, null);
        builder.setView(dialogView);
        TextView title = dialogView.findViewById(R.id.TitleView);
        TextView price = dialogView.findViewById(R.id.Price);
        TextView tel = dialogView.findViewById(R.id.Tel);
        TextView desk  = dialogView.findViewById(R.id.Desk);
        ImageView image = dialogView.findViewById(R.id.ImageView);

        Picasso.get().load(newPost.getImageId()).into(image);
        title.setText(newPost.getTitle());
        price.setText("Цена: " + newPost.getPrice());
        tel.setText("Телефон: " + newPost.getTel());
        desk.setText("Описание: " + newPost.getDisk());

        dialog = builder.create();
        dialog.show();
    }
    */
    public void updateAdapter(List<NewPost> listData)
    {
        arrayPost.clear();
        arrayPost.addAll(listData);
        notifyDataSetChanged();
    }
    public void setDbManager(DbManager dbManager)
        {
            this.dbManager = dbManager;
        }
}
