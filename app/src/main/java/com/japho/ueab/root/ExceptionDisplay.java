package com.japho.ueab.root;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ExceptionDisplay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_display);
        //Toast.makeText(this, "I am ExceptionDisplay", Toast.LENGTH_LONG).show();
        TextView exception_text = findViewById(R.id.exception_text);
        Button btnBack = (Button) findViewById(R.id.btnBack);
        Bundle  bundle=getIntent().getExtras();
        if(bundle != null && bundle.containsKey("error"))
        {
            exception_text.setText("Sorry an unexpected error occured \n Go back and try again");
           // exception_text.setText(bundle.getString("error"));
           // Toast.makeText(getApplicationContext(), ""+bundle.getString("error"), Toast.LENGTH_LONG).show();
        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentData();
            }
        });
    }

    @Override
    public void onBackPressed() {
        intentData();
    }

    public void intentData() {

        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(ExceptionDisplay.this,SplashScreem.class);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }
    public boolean onKeyDown(int keyCode, KeyEvent e)
    {
        if((keyCode==KeyEvent.KEYCODE_BACK))
        {
            intentData();
        }



        return super.onKeyDown(keyCode,e);
    }

}





