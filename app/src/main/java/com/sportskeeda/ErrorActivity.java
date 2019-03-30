package com.sportskeeda;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // Creating full screen activity for splash screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_error);


        Intent i =getIntent();
        int flagActivityErrorType= i.getIntExtra("flag",0);
        TextView  errText= findViewById(R.id.error_message);
        ImageView errImage= findViewById(R.id.error_image);
        ImageView reload= findViewById(R.id.reload);
        reload.setOnClickListener(this);
        if(flagActivityErrorType==2){
            errText.setText(this.getResources().getString(R.string.error_internet));
            errImage.setImageResource(R.drawable.nointernet);
        }
        else if(flagActivityErrorType==3){
            errText.setText(this.getResources().getString(R.string.error_realm));
            errImage.setImageResource(R.drawable.database);
        }

    }
    @Override
    public void onBackPressed() {
          Intent tryAgain = new Intent(ErrorActivity.this,Splash.class);
          startActivity(tryAgain);


    }

    @Override
    public void onClick(View v) {
        Intent tryAgain = new Intent(ErrorActivity.this,Splash.class);
        startActivity(tryAgain);
    }
}
