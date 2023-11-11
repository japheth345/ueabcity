package Fragments;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.japho.ueab.root.R;

public class DurationAlert  extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
public TextView tv;
    String data;
    public DurationAlert(Activity a,String data) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.data=data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sualert);
        yes = (Button) findViewById(R.id.btn_yes);
          tv=findViewById(R.id.txt_dia);
          tv.setText(data);
        yes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
               // Intent mainIntent = new Intent(getContext(), MainActivity.class);
               // getContext().startActivity(mainIntent);
               // c.finish();

                break;


            default:
                break;
        }
       dismiss();
    }
}