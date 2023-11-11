package com.japho.ueab.root;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import Fragments.FavoriteFragment;
import Fragments.HomeFragment;
import Fragments.ItemDetailsFragment;
import Fragments.LoggedOutFragment;
import Fragments.ProfileFragment;
import Fragments.SearchFragment;
import Fragments.AddItemFragment;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth authRef = FirebaseAuth.getInstance();
    private static final int SMS_PERMISSION_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      //  requestReadAndSendSmsPermission();
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(getApplicationContext()));
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

       if( authRef.getCurrentUser() == null)
       {
           Intent intent = new Intent(MainActivity.this, RegisterPhoneNumber.class);
         
           startActivity(intent);
           finish();
       }
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                if (HomeFragment.itemFilter == null) {
                                    HomeFragment.itemFilter = new FilterObject();
                                }
                                HomeFragment.applyAdvancedFilter = false;
                                HomeFragment.itemFilter.resetFilter();
                                selectedFragment = HomeFragment.newInstance();
                                break;
                            case R.id.action_search:
                                if (HomeFragment.itemFilter == null) {
                                    HomeFragment.itemFilter = new FilterObject();
                                }
                                selectedFragment = SearchFragment.newInstance();
                                break;
                            case R.id.action_addition:
                                if (authRef.getCurrentUser() != null)
                                    selectedFragment = AddItemFragment.newInstance();
                                else
                                    selectedFragment = LoggedOutFragment.newInstance();
                                break;
                            case R.id.action_profile:
                                if (authRef.getCurrentUser() != null)
                                    selectedFragment = ProfileFragment.newInstance();
                                else
                                    selectedFragment = LoggedOutFragment.newInstance();
                                break;
                            case R.id.action_favorite:
                                if (authRef.getCurrentUser() != null)
                                    selectedFragment = FavoriteFragment.newInstance();
                                else
                                    selectedFragment = LoggedOutFragment.newInstance();
                                break;

                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.commit();
    }

    public void logInMethod(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    public void signUpMethod(View view) {
        startActivity(new Intent(MainActivity.this, signUpActivity.class));
    }

    //Placeholder methods for settings menu
    public void notificationsMethod(View view) {
        Toast.makeText(MainActivity.this, "Notifications settings here", Toast.LENGTH_SHORT).show();
    }

    public void showItemDescription(View view) {
        String id = (String) view.getTag();
        ItemDetailsFragment.setItemIDToDisplay(id);
        Fragment selectedFragment = ItemDetailsFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.CALL_PHONE)  )  {

            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE,Manifest.permission.BLUETOOTH}, SMS_PERMISSION_CODE);

    }
}
//com.japho.ueab.root

