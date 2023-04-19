package ru.sergeev.tezodeer.abito;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import ru.sergeev.tezodeer.abito.screens.ChooseImagesActivity;
import ru.sergeev.tezodeer.abito.utils.MyConstants;

public class EditActivity extends AppCompatActivity {
    private ImageView imItem;
    private StorageReference mStorageRef;
    private Uri uploadUri;
    private Spinner spinner;
    private DatabaseReference dReference;
    private FirebaseAuth firebaseAuth;
    private EditText edTitle, edPrice, edTel, edDisc;
    private Boolean edit_state = false;
    private String temp_cat = "";
    private String temp_uid = "";
    private String temp_time = "";
    private String temp_total_views = "";
    private String temp_key = "";
    private String temp_image_url = "";
    private Boolean is_image_update = false;
    private ProgressDialog pd;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        init();

    }
    private void init()
    {
        pd = new ProgressDialog(this);
        pd.setMessage("Идёт загрузка...");
        edTitle = findViewById(R.id.edTitile);
        edPrice = findViewById(R.id.edPrice);
        edTel = findViewById(R.id.edTel);
        edDisc = findViewById(R.id.edDescription);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_spinner, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mStorageRef = FirebaseStorage.getInstance().getReference("Images");
        imItem = (ImageView) findViewById(R.id.imItem);
        getMyIntent();
    }
    private void getMyIntent()
    {
            Intent i = getIntent();
            edit_state = i.getBooleanExtra(MyConstants.EDIT_STATE,false);
            if(edit_state)setDataAds(i);
        }
    private void setDataAds(Intent i)
    {
        edit_state = true;
        Picasso.get().load(i.getStringExtra(MyConstants.IMAGE_ID)).into(imItem);
        edTel.setText(i.getStringExtra(MyConstants.TEL));
        edTitle.setText(i.getStringExtra(MyConstants.TITLE));
        edPrice.setText(i.getStringExtra(MyConstants.PRICE));
        edDisc.setText(i.getStringExtra(MyConstants.DISK));
        temp_cat = i.getStringExtra(MyConstants.CAT);
        temp_uid = i.getStringExtra(MyConstants.UID);
        temp_time = i.getStringExtra(MyConstants.TIME);
        temp_key = i.getStringExtra(MyConstants.KEY);
        temp_image_url = i.getStringExtra(MyConstants.IMAGE_ID);
        temp_total_views = i.getStringExtra(MyConstants.TOTAL_VIEWS);
    }
    private void uploadImage()
    {
        Bitmap bitmap = ((BitmapDrawable)imItem.getDrawable()).getBitmap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25,out);
        byte[] byteArray = out.toByteArray();
        final StorageReference mRef = mStorageRef.child(System.currentTimeMillis() + "_image");
        UploadTask up = mRef.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
            uploadUri = task.getResult();
            assert uploadUri != null;
                savePost();
                Toast.makeText(EditActivity.this, "Upload done!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    private void updateImage()
    {
        Bitmap bitmap = ((BitmapDrawable)imItem.getDrawable()).getBitmap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25,out);
        byte[] byteArray = out.toByteArray();
        final StorageReference mRef = FirebaseStorage.getInstance().getReferenceFromUrl(temp_image_url);
        UploadTask up = mRef.putBytes(byteArray);
        Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return mRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                uploadUri = task.getResult();
                assert uploadUri != null;
                temp_image_url = uploadUri.toString();
                updatePost();
                Toast.makeText(EditActivity.this, "Upload done!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public void onClickSavePost(View v) {
        pd.show();
        if(!edit_state)
        {
            uploadImage();
        }
        else
        {
            if(is_image_update = true)
            {
                updateImage();
            }
            else
            {
                updatePost();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Mylog", "WubaWuba DabDab " + data);
        if(requestCode == 15 && data != null)
        {
            if (resultCode == RESULT_OK)
            {
                Log.d("MyLog", "uri Main " + data.getStringExtra("uriMain"));
                Log.d("MyLog", "uri 2 " + data.getStringExtra("uri2"));
                Log.d("MyLog", "uri 3 " + data.getStringExtra("uri3"));
            }
        }
    }

    public void onClickChooseImage (View v) {
        Intent e = new Intent(this, ChooseImagesActivity.class);
        startActivityForResult(e, 15);
    }

    private void updatePost()
    {
        dReference = FirebaseDatabase.getInstance().getReference(temp_cat);
            NewPost post = new NewPost();
            post.setImageId(uploadUri.toString());
            post.setTitle(edTitle.getText().toString());
            post.setTel(edTel.getText().toString());
            post.setPrice(edPrice.getText().toString());
            post.setCat(temp_cat);
            post.setKey(temp_key);
            post.setDisk(edDisc.getText().toString());
            post.setTime(temp_time);
            post.setUid(temp_uid);
            post.setTotal_views(temp_total_views);
            dReference.child(temp_key).child("anuncio").setValue(post);
            finish();
    }
    private void savePost()
    {
        dReference = FirebaseDatabase.getInstance().getReference(spinner.getSelectedItem().toString());
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getUid() != null)
        {
            String key = dReference.push().getKey();
            NewPost post = new NewPost();
            post.setImageId(uploadUri.toString());
            post.setTitle(edTitle.getText().toString());
            post.setTel(edTel.getText().toString());
            post.setPrice(edPrice.getText().toString());
            post.setCat(spinner.getSelectedItem().toString());
            post.setDisk(edDisc.getText().toString());
            post.setKey(key);
            post.setTime(String.valueOf(System.currentTimeMillis()));
            post.setUid(firebaseAuth.getUid());
            post.setTotal_views("0");

            if(key != null)dReference.child(key).child("anuncio").setValue(post);
            Intent i = new Intent();
            i.putExtra("cat", spinner.getSelectedItem().toString());
            setResult(RESULT_OK, i);

        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pd.dismiss();
    }
}
