package com.japho.ueab.root;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.japho.ueab.root.R;

public class internetAlert extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public TextView tv;
    String message;
    public internetAlert(String message,@NonNull Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.internetalert);
        yes = (Button) findViewById(R.id.btn_yes);
        tv=findViewById(R.id.txt_dia);
        tv.setText(message);
        yes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
               System.exit(0);

                //c.finish();

                break;


            default:
                break;
        }
        dismiss();
    }
}