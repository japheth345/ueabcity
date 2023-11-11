package Fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.japho.ueab.root.Helper;
import com.japho.ueab.root.R;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.japho.ueab.root.Helper.setImage;

public class ProfileFragment extends Fragment {
    View view;

    private TextView firstName_et;
    private TextView lastName_et;
    private TextView email_et;

    private TextView postalCode_et;
    private ImageView photo_iv,photo;

    private ImageButton updatePhoto_ib;

    private String firstName;
    private String lastName;
    private String email;
    private String imgurl;
    private String postalCode;
    String   phoneNumber;
    private Uri photoUrl;
    String uploadURL;
    private MenuItem editMenuItem;
    private MenuItem saveMenuItem;
    private MenuItem cancelMenuItem;
    private MenuItem settingsMenuItem;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //Database Update
    private DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference myUser = myRootRef.child("Users");
    private DatabaseReference myUID;

    //Firebase Authentication
    FirebaseAuth authRef = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener authListener;

    //Image upload variable
    private static final int IMG_RESULT = 1;
    private Intent intent;
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private StorageReference profilePictureRef = storageRef.child("ProfilePictures");
    private InterstitialAd interstitialAd;
    public ProfileFragment() {
        // Required empty default constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        photo=view.findViewById(R.id.profile_photo);
        setAuthStateListener(view);
        MobileAds.initialize(getContext(), getString(R.string.appid));
        interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(getString(R.string.profile));
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);
        interstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });
        if(isNetworkAvailable()==false)
        {
            ExpiryAlert cdd=new ExpiryAlert("No internect Connection. \n Please connect to internet and try again",getActivity());
            cdd.show();
        }
        setProfileFields();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        editMenuItem = menu.findItem(R.id.profile_edit_button);
        saveMenuItem = menu.findItem(R.id.profile_save_button);
        cancelMenuItem = menu.findItem(R.id.profile_cancel_button);
        settingsMenuItem = menu.findItem(R.id.profile_settings_button);

        editMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                toggleEditMode(true);
                return true;
            }
        });
        saveMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                updateName();
              //  updateEmail();
                updatePhoneNumber();
                updatePostalCode();
                updatePhoto();
                toggleEditMode(false);
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                return true;
            }
        });
        cancelMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                setProfileFields();
                toggleEditMode(false);
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                return true;
            }
        });
        settingsMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new SettingsFragment());
                transaction.commit();
                return true;
            }
        });
    }

    private void toggleEditMode(boolean editMode) {
        firstName_et.setFocusableInTouchMode(editMode);
        firstName_et.setEnabled(editMode);
        lastName_et.setFocusableInTouchMode(editMode);
        lastName_et.setEnabled(editMode);
       // email_et.setFocusableInTouchMode(editMode);
        //email_et.setEnabled(editMode);

        postalCode_et.setFocusableInTouchMode(editMode);
        postalCode_et.setEnabled(editMode);
        editMenuItem.setVisible(!editMode);
        saveMenuItem.setVisible(editMode);
        cancelMenuItem.setVisible(editMode);
        settingsMenuItem.setVisible(!editMode);

        if (!editMode) {
            firstName_et.clearFocus();
            lastName_et.clearFocus();
            email_et.clearFocus();

            postalCode_et.clearFocus();
        }
    }
    private void setProfileFields() {
        FirebaseUser user = authRef.getCurrentUser();
        String UID = user.getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference sellerRef = rootRef.child("Users").child(UID);

        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("imageURL").getValue(String.class)==null)
                {

                }
                else
                {
                    imgurl = dataSnapshot.child("imageURL").getValue(String.class);
                    Picasso.get().load(imgurl).into(photo);

                }
                firstName= dataSnapshot.child("firstName").getValue(String.class) ;
               lastName= dataSnapshot.child("lastName").getValue(String.class);
                email = dataSnapshot.child("email").getValue(String.class);
                postalCode=dataSnapshot.child("zipCode").getValue(String.class);

                firstName_et.setText(firstName);
                lastName_et.setText(lastName);
                email_et.setText(email);

                postalCode_et.setText(postalCode);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    // Not currently used
    private void updatePhoto() {
        if (uploadURL==null)
        {
        }
        else
        {

            myUID.child("imageURL").setValue(uploadURL);


        }
    }

    private void updateName() {
        if (Helper.isNullOrEmpty(firstName_et.getText().toString().trim()) || Helper.isNullOrEmpty(lastName_et.getText().toString().trim())) {
            Toast.makeText(view.getContext(), "Name field is invalid", Toast.LENGTH_SHORT).show();
        } else if (!firstName_et.getText().toString().trim().equals(firstName) || !lastName_et.getText().toString().trim().equals(lastName)) {
            firstName = firstName_et.getText().toString().trim();
            myUID.child("firstName").setValue(firstName);

            lastName = lastName_et.getText().toString().trim();
            myUID.child("lastName").setValue(lastName);
        }
    }

    // TODO: Only Gmail updates go through, possibly fix?
    private void updateEmail() {

            email = email_et.getText().toString().trim();
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                myUID.child("email").setValue(email);
                                Log.d("USER_EMAIL_UPDATE", "User email updated.");
                            } else {
                                Toast.makeText(view.getContext(), "Email update error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

    }

    // Do we want to allow people to remove their phone numbers?
    private void updatePhoneNumber() {


    }

    // Do we want to allow people to remove their postal codes?
    private void updatePostalCode() {
        if (!postalCode_et.getText().toString().trim().equals(postalCode)) {
            postalCode = postalCode_et.getText().toString().trim();
            myUID.child("zipCode").setValue(postalCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == IMG_RESULT && resultCode == RESULT_OK && data != null && user != null) {
                Uri URI = data.getData();
                Picasso.get().load(data.getData()).into(photo);
                String[] FILE = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContext().getContentResolver().query(URI,
                        FILE, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(FILE[0]);
                String imageDecode = cursor.getString(columnIndex);
                cursor.close();

               /* photo_iv.setImageBitmap(BitmapFactory
                        .decodeFile(ImageDecode));*/
                // Toast.makeText(getActivity(), ImageDecode, Toast.LENGTH_LONG).show();

                Uri file = Uri.fromFile(new File(imageDecode));
                StorageReference riversRef = profilePictureRef.child(user.getUid());
                riversRef.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Task<Uri> downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        uploadURL = uri.toString();
                                        if (user != null) {
                                            myUID = myUser.child(user.getUid());

                                            myUID.child("imageURL").setValue(uploadURL);
                                        }
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                              /*  if (user != null) {
                                    myUID = myUser.child(user.getUid());

                                    myUID.child("ImageURL").setValue(downloadUrl.toString());
                                }
*/
                                //Toast.makeText(getActivity(), "Successfully uploaded!", Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                Toast.makeText(getActivity(), "Failed to upload! Check app permissions! \n"+exception.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_LONG).show();
        }
    }

    public void setAuthStateListener(View view) {
        // Initialize controls
        firstName_et = (TextView) view.findViewById(R.id.profile_first_name);
        lastName_et = (TextView) view.findViewById(R.id.profile_last_name);
        email_et = (TextView) view.findViewById(R.id.profile_email);

        postalCode_et = (TextView) view.findViewById(R.id.profile_zip);
        photo_iv = (ImageView) view.findViewById(R.id.profile_photo);
        updatePhoto_ib = (ImageButton) view.findViewById(R.id.profile_update_photo);

        // Set onClick listener to Update Photo button
        updatePhoto_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMG_RESULT);
            }
        });

        // SET Auth State Listener
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (authRef.getCurrentUser() != null) {
                    setHasOptionsMenu(true);


                    //if (photoUrl != null) { new DownloadImageTask(photo_iv).execute(photoUrl.toString()); }
                    firstName_et.setVisibility(View.VISIBLE);
                    lastName_et.setVisibility(View.VISIBLE);
                    email_et.setVisibility(View.VISIBLE);

                    photo_iv.setVisibility(View.VISIBLE);
                    postalCode_et.setVisibility(View.VISIBLE);

                    updatePhoto_ib.setVisibility(View.VISIBLE);
                    /*updatePhoto_ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updatePhoto();
                        }
                    });*/

                    // SET Data Listener
                    myUID = myUser.child(authRef.getCurrentUser().getUid());
                    myUID.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String imgUrl = dataSnapshot.child("ImageURL").getValue(String.class);
                            if (!Helper.isNullOrEmpty(imgUrl)) {
                                setImage(getActivity(), imgUrl, photo_iv);
                            }
                            email = dataSnapshot.child("email").getValue(String.class);
                            firstName = dataSnapshot.child("firstName").getValue(String.class);
                            lastName = dataSnapshot.child("lastName").getValue(String.class);
                            phoneNumber = dataSnapshot.child("email").getValue(String.class);
                            postalCode = dataSnapshot.child("zipCode").getValue(String.class);

                            setProfileFields();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }
        };

        // ADD Auth State Listener
        authRef.addAuthStateListener(authListener);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
