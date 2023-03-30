package ykt.ne_bezdari.pr_11_localauthorisation;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class LoginActivity extends AppCompatActivity {

    TextView LoginText;
    ConstraintLayout Back;
    TextView UserText;
    TextView PasswordText;
    TextView RemainingAttempts;
    EditText LoginInput;
    EditText PasswordInput;
    Button LoginButton;
    int RemainingA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Back = (ConstraintLayout) findViewById(R.id.Back);
        LoginText = (TextView) findViewById(R.id.Login_Text);
        RemainingAttempts = (TextView) findViewById(R.id.RemainingAttempts);
        UserText = (TextView) findViewById(R.id.UserText);
        PasswordText = (TextView) findViewById(R.id.PasswordText);
        LoginInput = (EditText) findViewById(R.id.LoginInput);
        PasswordInput = (EditText) findViewById(R.id.PasswordInput);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        RemainingA = 3;
        RemainingAttempts.setText("Осталось попыток: " + String.valueOf(RemainingA));

    }

    public void Login(View v) {
        if (LoginInput.getText().toString().equals("admin") && PasswordInput.getText().toString().equals("admin")) {
            Toast.makeText(this, "Успешный вход!", Toast.LENGTH_SHORT).show();
            Intent login_succsessful = new Intent(this, SecondActivity.class);
            startActivity(login_succsessful);
            overridePendingTransition(0, 0);
        } else {
            Toast.makeText(this, "Неправильные данные", Toast.LENGTH_SHORT).show();
            RemainingA--;
            RemainingAttempts.setText("Осталось попыток: " + String.valueOf(RemainingA));
            RemainingAttempts.setVisibility(View.VISIBLE);
        }
        if (RemainingA == 0) {
            LoginButton.setEnabled(false);
            Back.setBackgroundColor(Color.RED);
            LoginText.setText("Вход заблокирован!");
            LoginInput.setEnabled(false);
            PasswordInput.setEnabled(false);
            Toast.makeText(this, "Знаешь, если ты забыл логин и пароль, позвони мне (+ 7 924 860 53 16)", Toast.LENGTH_LONG).show();

            }
        }
    }