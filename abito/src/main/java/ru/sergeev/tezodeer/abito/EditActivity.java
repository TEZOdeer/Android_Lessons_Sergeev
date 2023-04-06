package ru.sergeev.tezodeer.abito;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;

public class EditActivity extends AppCompatActivity {
    private ImageView imItem;
    private StorageReference mStorageRef;
    private Uri uploadUri;
    private Spinner spinner;
    private DatabaseReference dReference;
    private FirebaseAuth firebaseAuth;
    private EditText edTitle, edPrice, edTel, edDisc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_layout);
        init();
    }
    private void init()
    {
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
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10 && data != null && data.getData() != null)
        {
            if (resultCode == RESULT_OK)
            {
                imItem.setImageURI(data.getData());
                uploadImage();
            }
        }
    }
    private void uploadImage()
    {
        Bitmap bitmap = ((BitmapDrawable)imItem.getDrawable()).getBitmap();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75,out);
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
                Toast.makeText(EditActivity.this, "Upload done: " + uploadUri.toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    public void onClickSavePost(View v) {
        savePost();
        Toast.makeText(this, "Объявление опубликовано", Toast.LENGTH_SHORT).show();
        finish();
    }
    public void onClickImage (View v) {
        getImage();
    }
    private void getImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 10);
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
            post.setDisk(edDisc.getText().toString());
            post.setKey(key);
            post.setTime(String.valueOf(System.nanoTime()));
            post.setUid(firebaseAuth.getUid());

            if(key != null)dReference.child(key).child("anuncio").setValue(post);

        }
    }
}
