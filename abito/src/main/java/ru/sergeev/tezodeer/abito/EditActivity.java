package ru.sergeev.tezodeer.abito;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.sergeev.tezodeer.abito.adapter.ImageAdapter;
import ru.sergeev.tezodeer.abito.screens.ChooseImagesActivity;
import ru.sergeev.tezodeer.abito.utils.MyConstants;

public class EditActivity extends AppCompatActivity {
    private StorageReference mStorageRef;
    private String[] uploadUri = new String[3];
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
    private int load_image_counter = 0;
    private List<String> imagesUris;
    private ImageAdapter imAdapter;
    private TextView tvImagesCounter;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        init();

    }
    private void init()
     {
        tvImagesCounter = findViewById(R.id.tvImagesCounter);
        imagesUris = new ArrayList<>();
        ViewPager vp = findViewById(R.id.view_pager);
        imAdapter = new ImageAdapter(this);
        vp.setAdapter(imAdapter);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String dataText = position + 1 + "/" + imagesUris.size();
                tvImagesCounter.setText(dataText);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        uploadUri[0] = "empty";
        uploadUri[1] = "empty";
        uploadUri[2] = "empty";

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
      //Picasso.get().load(i.getStringExtra(MyConstants.IMAGE_ID)).into(imItem);
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

    private void uploadImage() throws IOException {

        if(Build.VERSION.SDK_INT <= 28) {
            if (load_image_counter < uploadUri.length) {

                if (!uploadUri[load_image_counter].equals("empty")) {

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uploadUri[load_image_counter]));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
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
                            if (task.getResult() == null) return;
                            uploadUri[load_image_counter] = task.getResult().toString();
                            assert uploadUri != null;
                            load_image_counter++;
                            if (load_image_counter < uploadUri.length) {
                                try {
                                    uploadImage();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                savePost();
                                Toast.makeText(EditActivity.this, "Upload done!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else {
                    load_image_counter++;
                    uploadImage();
                }
            } else {
                pd.show();
                savePost();
                finish();
            }
        }
        else
        {
            if (load_image_counter < uploadUri.length) {

                if (!uploadUri[load_image_counter].equals("empty")) {

                    Bitmap bitmap = null;

                    ImageDecoder.Source altUpload;
                    altUpload = ImageDecoder.createSource(getContentResolver(), Uri.parse(uploadUri[load_image_counter]));


                    Log.d("MyLog", "Wuba Wuba");
                    try {
                        bitmap = ImageDecoder.decodeBitmap(altUpload);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d("MyLog", "Wuba Wuba2");


                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    assert bitmap != null;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
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
                            if (task.getResult() == null) return;
                            uploadUri[load_image_counter] = task.getResult().toString();
                            assert uploadUri != null;
                            load_image_counter++;
                            if (load_image_counter < uploadUri.length) {
                                try {
                                    uploadImage();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                savePost();
                                Toast.makeText(EditActivity.this, "Upload done!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                } else {
                    load_image_counter++;
                    uploadImage();
                }
            } else {
                pd.show();
                savePost();
                finish();
            }
        }
    }
    private void updateImage() throws IOException {
        Bitmap bitmap = null;
        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uploadUri[load_image_counter]));
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
                uploadUri[load_image_counter] = task.getResult().toString();
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
    public void onClickSavePost(View v) throws IOException {
        pd.show();
        if(!edTitle.getText().toString().equals("")) {
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
        else {
            pd.dismiss();
            Toast.makeText(this, "Введите название объявления!", Toast.LENGTH_SHORT).show();
        }
        
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getContentResolver().getPersistedUriPermissions();

        Log.d("Mylog", "WubaWuba DabDab " + data);
        if(requestCode == 15 && data != null)
        {

            if (resultCode == RESULT_OK)
            {;
                uploadUri[0] = data.getStringExtra("uriMain");
                uploadUri[1] = data.getStringExtra("uri2");
                uploadUri[2] = data.getStringExtra("uri3");

                imagesUris.clear();
                for (String s : uploadUri)
                {
                    if(!s.equals("empty"))imagesUris.add(s);
                }
                String dataText;
                imAdapter.updateImages(imagesUris);
                if(imagesUris.size() > 0) {
                    dataText = 1 + "/" + imagesUris.size();
                    tvImagesCounter.setText(dataText);
                }
                else
                {
                    dataText = 0 + "/" + imagesUris.size();
                    tvImagesCounter.setText(dataText);
                }
            }
        }
    }


    public void onClickChooseImage (View v) {
        Intent e = new Intent(this, ChooseImagesActivity.class);
        e.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        e.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        e.addCategory(Intent.CATEGORY_OPENABLE);
        e.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        e.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(e, 15);
    }

    private void updatePost()
    {
        dReference = FirebaseDatabase.getInstance().getReference(temp_cat);
            NewPost post = new NewPost();
            post.setImageId(uploadUri[0]);
            post.setImageId(uploadUri[1]);
            post.setImageId(uploadUri[2]);
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
        Toast.makeText(this, "Объявление обновлено!", Toast.LENGTH_SHORT).show();
    }
    private void savePost()
    {
        dReference = FirebaseDatabase.getInstance().getReference(spinner.getSelectedItem().toString());
        firebaseAuth = FirebaseAuth.getInstance();
        if(firebaseAuth.getUid() != null)
        {
            String key = dReference.push().getKey();
            NewPost post = new NewPost();
            post.setImageId(uploadUri[0]);
            post.setImageId2(uploadUri[1]);
            post.setImageId3(uploadUri[2]);
            post.setTitle(edTitle.getText().toString());

            if(!edTel.getText().toString().equals("")) {
                post.setTel(edTel.getText().toString());
            }
            else {
                post.setTel("Не указан");
            }


            if(!edPrice.getText().toString().equals("")) {
                post.setPrice(edPrice.getText().toString());
            }
            else {
                post.setPrice("Договорная");
            }

            post.setCat(spinner.getSelectedItem().toString());

            if(!edDisc.getText().toString().equals("")) {
                post.setDisk(edDisc.getText().toString());
            }
            else {
                post.setDisk("Описание отсутствует");
            }

            post.setKey(key);
            post.setTime(String.valueOf(System.currentTimeMillis()));
            post.setUid(firebaseAuth.getUid());
            post.setTotal_views("0");

            if(key != null)dReference.child(key).child("anuncio").setValue(post);
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.putExtra("cat", spinner.getSelectedItem().toString());
            setResult(RESULT_OK, i);

        }
        finish();
        Toast.makeText(this, "Объявление опубликовано!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pd.dismiss();
    }
}
