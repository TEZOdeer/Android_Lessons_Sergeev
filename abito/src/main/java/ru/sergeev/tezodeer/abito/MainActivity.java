package ru.sergeev.tezodeer.abito;

import static android.content.ContentValues.TAG;
import static android.os.Build.VERSION_CODES.M;

import static java.security.AccessController.getContext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonUiContext;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.ResponseInfo;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yandex.mobile.ads.banner.AdSize;
import com.yandex.mobile.ads.banner.BannerAdEventListener;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.interstitial.InterstitialAd;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;

import ru.sergeev.tezodeer.abito.adapter.DataSender;
import ru.sergeev.tezodeer.abito.adapter.PostAdapter;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private NavigationView nav_view;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private TextView userEmail;
    private AlertDialog dialog;
    private Toolbar toolbar;
    private FloatingActionButton fb;
    private PostAdapter.OnItemClickCustom onItemClickCustom;
    private RecyclerView rcView;
    private PostAdapter postAdapter;
    private DataSender dataSender;
    private DbManager dbManager;
    private Context contextDB;
    public static String MAUTH = "";
    private String current_cat = "Машины";
    private NewPost newPost;
    private final int EDIT_RES = 12;
    private BannerAdView bannerAdView;
    private static final String YANDEX_AD_UNIT_ID = "demo-banner-yandex";
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddAds();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
        if(adView != null)
        {
            adView.resume();
        }
         */
        Log.d("MyLog", "On Resume");
        if(current_cat.equals("my_ads"))
        {
            dbManager.getMyAdsDataFromDb(mAuth.getUid());
        }
        else
        {
            dbManager.getDataFromDb(current_cat);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
        if(adView != null)
        {
            adView.pause();
        }
         */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*
        if(adView != null)
        {
            adView.destroy();
        }
         */
    }

    private void init()
    {
        fab = findViewById(R.id.floatingActionButton2);
        setOnItemClickCustom();
        rcView = findViewById(R.id.recyclerView);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        fb = findViewById(R.id.floatingActionButton2);
        List<NewPost> arrayPost = new ArrayList<>();

        postAdapter = new PostAdapter(arrayPost, this,onItemClickCustom, null);
        rcView.setAdapter(postAdapter);
        nav_view = findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar, R.string.toggle_open, R.string.toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        userEmail = (TextView) nav_view.getHeaderView(0).findViewById(R.id.tvEmail);
        mAuth = FirebaseAuth.getInstance();
        getDataDB();
        dbManager = new DbManager(dataSender, this);

        postAdapter.setDbManager(dbManager);

        getUserData();

    }

    private void getDataDB()
    {
        dataSender = new DataSender() {
            @Override
            public void onDataRecived(List<NewPost> listdata) {
                Collections.reverse(listdata);
                postAdapter.updateAdapter(listdata);

            }
        };
    }


    private void setOnItemClickCustom()
    {
        onItemClickCustom = new PostAdapter.OnItemClickCustom() {
            @Override
            public void onItemSelected(int position) {

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_RES && resultCode == RESULT_OK && data != null)
        {
            current_cat = data.getStringExtra("cat");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void onClickEdit(View v)
    {
            Intent i = new Intent(MainActivity.this, EditActivity.class);
            i.setAction(Intent.ACTION_OPEN_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(i, EDIT_RES);
    }
    private void getUserData()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
            fab.setEnabled(true);
            userEmail.setText(currentUser.getEmail());
            MAUTH = mAuth.getUid();
        }
        else
        {
            fab.setEnabled(false);
            userEmail.setText(R.string.sign_in_or_sign_up);
            MAUTH = "";
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.id_my_ads:
                current_cat = "my_ads";
                dbManager.getMyAdsDataFromDb(mAuth.getUid());
                break;
            case R.id.id_cars_ads:
                current_cat = "Машины";
                dbManager.getDataFromDb("Машины");
                break;
            case R.id.id_cloth_ads:
                current_cat = "Одежда";
                dbManager.getDataFromDb("Одежда");
                break;
            case R.id.id_pc_ads:
                current_cat = "Компьютеры";
                dbManager.getDataFromDb("Компьютеры");
                break;
            case R.id.id_smartphone_ads:
                current_cat = "Смартфоны";
                dbManager.getDataFromDb("Смартфоны");
                break;
            case R.id.id_dm_ads:
                current_cat = "Бытовая техника";
                dbManager.getDataFromDb("Бытовая техника");
                break;
            case R.id.id_services_ads:
                current_cat = "Услуги";
                dbManager.getDataFromDb("Услуги");
                break;
            case R.id.id_sign_up:
                signUpDialog(R.string.sign_up,R.string.sign_up_button, 0);
                break;
            case R.id.id_sign_in:
                signUpDialog(R.string.sign_in,R.string.sign_in_button, 1);
                break;
            case R.id.id_sign_out:
                signOut();
                break;
        }
        return false;
    }
    private void signUpDialog(int title, int buttonTitle,int index)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign__up_layout, null);
        dialogBuilder.setView(dialogView);
        TextView titleTextView = dialogView.findViewById(R.id.tvAlertTitle);
        titleTextView.setText(title);
        EditText edEmail = dialogView.findViewById(R.id.edEmail);
        EditText edPassword = dialogView.findViewById(R.id.edPassword);
        Button b = dialogView.findViewById(R.id.buttonSignUp);
        b.setText(buttonTitle);
        b.setOnClickListener((v -> {
                if(index==0)
                {
                    signUp(edEmail.getText().toString(), edPassword.getText().toString());
                }
                else
                {
                    signIn(edEmail.getText().toString(), edPassword.getText().toString());
                }
                dialog.dismiss();
            }
        ));
        dialog = dialogBuilder.create();
        dialog.show();
    }
    private void signUp(String email, String password)
    {
        if(!email.equals("") && !password.equals(""))
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            getUserData();
                            Toast.makeText(MainActivity.this, "Вы успешно зарегистрировались!", Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Ошибка при авторизации",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
     else
        {
            Toast.makeText(this, "Email или Password пустой!", Toast.LENGTH_SHORT).show();
        }
    }
    private void signIn(String email, String password)
    {
        if(!email.equals("") && !password.equals(""))
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            getUserData();
                            Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Ошибка при авторизации",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        else
        {
            Toast.makeText(this, "Email или Password пустой!", Toast.LENGTH_SHORT).show();
        }
    }
    private void signOut()
    {
        mAuth.signOut();
        getUserData();
        Toast.makeText(this, "Вы вышли из аккаунта!", Toast.LENGTH_SHORT).show();
    }
    private void AddAds() {
        MobileAds.initialize(this, new InitializationListener() {
            @Override
            public void onInitializationCompleted() {
            }
        });
        bannerAdView = new BannerAdView(this);
        bannerAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.setAdUnitId(YANDEX_AD_UNIT_ID);
        bannerAdView.setAdSize(AdSize.BANNER_320x100);
        bannerAdView.loadAd(adRequest);
    }
}