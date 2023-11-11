package com.japho.ueab.root;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import Fragments.SignUpAlert;

public class signUpActivity extends AppCompatActivity {

    TextView editTextSUFirstName;
    TextView editTextSULastName;
   // TextView editTextSUEmail;
    TextView editTextSUPhone;
    TextView editTextSUZIP;
  //  TextView editTextSUPassword;
  //  TextView editTextSUConfPassword;
    Button buttonSUSignUp;

    FirebaseAuth authRef = FirebaseAuth.getInstance();
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference usersRef = rootRef.child("Users");
    DatabaseReference exp = rootRef.child("Expiry");
    String phone;
    public signUpActivity()
    {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(getApplicationContext()));
        Intent intent = getIntent();
        phone = (String) intent.getSerializableExtra("phone");
        editTextSUFirstName = (TextView) findViewById(R.id.editTextSUFirstName);
        editTextSULastName = (TextView) findViewById(R.id.editTextSULastName);
     //   editTextSUEmail = (TextView) findViewById(R.id.editTextSUEmail);
        editTextSUPhone = (TextView) findViewById(R.id.editTextSUPhone);
        editTextSUPhone.setText(phone);
        editTextSUPhone.setEnabled(false);
        editTextSUZIP = (TextView) findViewById(R.id.editTextSUZIP);
      //  editTextSUPassword = (TextView) findViewById(R.id.editTextSUPassword);
       // editTextSUConfPassword = (TextView) findViewById(R.id.editTextSUConfPassword);

        buttonSUSignUp = (Button) findViewById(R.id.buttonSUSignUp);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Login
        buttonSUSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }

            ;
        });
    }

    private void signUp() {
      //  final String email = editTextSUEmail.getText().toString().trim();
        final String firstName = editTextSUFirstName.getText().toString().trim();
        final String lastName = editTextSULastName.getText().toString().trim();
        final String phoneNumber = editTextSUPhone.getText().toString().trim();
        final String ZIPCode = editTextSUZIP.getText().toString().trim();

       // final String password = editTextSUPassword.getText().toString().trim();
       // final String confPassword = editTextSUConfPassword.getText().toString().trim();

        // Validate all fields
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty() || ZIPCode.isEmpty()) {
            Toast.makeText(signUpActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
       // } else if (!password.equals(confPassword)) {
           // Toast.makeText(signUpActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                FirebaseAuth authRef = FirebaseAuth.getInstance();
                FirebaseUser user = authRef.getCurrentUser();
                String UID = user.getUid();
                User newUser = new User(firstName, lastName, phone, phoneNumber, ZIPCode);
                usersRef.child(UID).setValue(newUser);
               // String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                exp.child(phone);
               exp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                   public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            // The child doesn't exist
                            long millliseconds=System.currentTimeMillis()+(3600000 * 72);
                            //long millliseconds=System.currentTimeMillis()+(60000 * 3);
                            exp.child(phone).setValue(String.valueOf(millliseconds));
                            Toast.makeText(signUpActivity.this, "Sign up successful!", Toast.LENGTH_LONG).show();
                        }
                    }

                   @Override
                   public void onCancelled(@NonNull @NotNull DatabaseError error) {

                   }
               });
                Toast.makeText(signUpActivity.this, "Sign up successful!", Toast.LENGTH_LONG).show();

                 SignUpAlert cdd=new SignUpAlert(signUpActivity.this);
                cdd.show();
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        /* Create an Intent that will start the Menu-Activity. */
                        Intent mainIntent = new Intent(signUpActivity.this,MainActivity.class);
                        signUpActivity.this.startActivity(mainIntent);
                      // signUpActivity.this.finish();
                    }
                }, 3000);
                //finish();


              //  startActivity(new Intent(signUpActivity.this, MainActivity.class));
            }
            catch(Exception ex)
            {
                Toast.makeText(signUpActivity.this, "Error \t!"+ex.getMessage(), Toast.LENGTH_LONG).show();
            }

            // Create user
          /*  authRef.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(signUpActivity.this, "Sign up successful!", Toast.LENGTH_LONG).show();
                        FirebaseUser user = task.getResult().getUser();
                        String UID = user.getUid();
                        User newUser = new User(firstName, lastName, phone, phoneNumber, ZIPCode);
                        usersRef.child(UID).setValue(newUser);
                        finish();
                        startActivity(new Intent(signUpActivity.this, MainActivity.class));
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(signUpActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

           */
        }
    }
    public String getPhoneNumber()
    {
        final String[] result2 = {null};
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference usersRef = rootRef.child("Users");
        DatabaseReference exp = rootRef.child("Expiry");
        String androidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        exp.child(androidID);
        exp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    // The child doesn't exist
                    //long millliseconds=System.currentTimeMillis()+(3600000 * 72);
                   // exp.setValue(phone+":"+millliseconds);
                    result2[0] ="null";
                }
                else
                {
                  result2[0]=snapshot.getValue(String.class);

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
     return result2[0] ;
    }
}
