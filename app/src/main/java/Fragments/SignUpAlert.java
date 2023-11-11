package Fragments;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.japho.ueab.root.R;

public class SignUpAlert  extends Dialog implements android.view.View.OnClickListener {

    public Context c;
    public Dialog d;
    public Button yes, no;



    public SignUpAlert(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.signupalert);
       // yes = (Button) findViewById(R.id.btn_yes);

       // yes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           //case R.id.btn_yes:
              //  getContext().startActivity(new Intent(getContext(), MainActivity.class));

                //c.finish();

              //  break;


            default:
                break;
        }
      //  dismiss();
    }
}