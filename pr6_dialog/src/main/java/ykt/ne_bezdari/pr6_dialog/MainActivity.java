package ykt.ne_bezdari.pr6_dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bgButton;
    public ConstraintLayout ConstraintLayout;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bgButton = (Button)findViewById(R.id.button_fon);
        bgButton.setOnClickListener(this);
        ConstraintLayout = (ConstraintLayout) findViewById(R.id.cons);
        context = MainActivity.this;
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.message);
        final CharSequence[] item =
                {getText(R.string.red),
                        getText(R.string.yellow),
                        getText(R.string.green),
                        getText((R.string.white))};
        builder.setItems(item, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0 -> {
                        ConstraintLayout.setBackgroundResource(R.color.redColor);
                        Toast.makeText(context, R.string.red, Toast.LENGTH_LONG).show();
                    }

                    case 1 -> {
                        ConstraintLayout.setBackgroundResource(R.color.yellowColor);
                        Toast.makeText(context, R.string.yellow, Toast.LENGTH_LONG).show();
                    }

                    case 2 -> {
                        ConstraintLayout.setBackgroundResource(R.color.greenColor);
                        Toast.makeText(context, R.string.green, Toast.LENGTH_LONG).show();
                    }

                    case 3 -> {
                        ConstraintLayout.setBackgroundResource(R.color.whiteColor);
                        Toast.makeText(context, R.string.white, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}