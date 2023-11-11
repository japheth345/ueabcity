package Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.japho.ueab.root.Helper;
import com.japho.ueab.root.ItemDescription;
import com.japho.ueab.root.MainActivity;
import com.japho.ueab.root.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import com.google.android.gms.ads.InterstitialAd;
import static android.app.Activity.RESULT_OK;
import static com.japho.ueab.root.Helper.setImage;

public class AddItemFragment extends Fragment {
    View view;

    private DatabaseReference rootRef;
    private DatabaseReference databaseItems;
    private DatabaseReference categoriesRef;
    private DatabaseReference subCategoryRef;
    private StorageReference mStorage;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private EditText itemNameET;
    private EditText itemPriceET;
    private EditText itemDescriptionET;
    private TextView tv;
    private LinearLayout selectImageCameraLayout;
    private LinearLayout selectImageGalleryLayout;

    private Spinner categorySpinner;
    private String[] categoryOptions;
    private int selectedCategory;
    private boolean justCreatedFlagC;
    ImageView imageview;
    private ProgressBar mProgressBar;
    private Spinner subCategorySpinner;
    private String[] subCategoryOptions;
    private int selectedSubCategory;
    private boolean justCreatedFlagSC;
    private Button postItemButton;
    private boolean isItemPostable;
    private String uploadURL;
    private final String uniqueItemID = UUID.randomUUID().toString();

    private ProgressDialog mProgressDialog;

    private static final int CAMERA_REQUEST_CODE = 111;
    private static final int GALLERY_INTENT = 2;
    private InterstitialAd interstitialAd;

    public AddItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static AddItemFragment newInstance() {
        return new AddItemFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_listing_item, container, false);

        rootRef = FirebaseDatabase.getInstance().getReference();
        databaseItems = rootRef.child("Items");
        categoriesRef = rootRef.child("Categories");

        mStorage = FirebaseStorage.getInstance().getReference();
        tv = view.findViewById(R.id.tv);
        itemNameET = (EditText) view.findViewById(R.id.item_name);
        itemPriceET = (EditText) view.findViewById(R.id.item_price);
        itemDescriptionET = (EditText) view.findViewById(R.id.item_description);

        // selectImageCameraLayout = (LinearLayout) view.findViewById(R.id.select_image_photo);
        selectImageGalleryLayout = (LinearLayout) view.findViewById(R.id.select_image_gallery);

        postItemButton = (Button) view.findViewById(R.id.post_item_button);
        mProgressDialog = new ProgressDialog(getContext());

        categorySpinner = (Spinner) view.findViewById(R.id.add_category);
        // subCategorySpinner = (Spinner) view.findViewById(R.id.add_subCategory);
        imageview = view.findViewById(R.id.selectphoto);
        //DATABASE-DEPENDANT LISTENERS
        categoriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //categoryOptions = Helper.getCategoryArrayFromSnapshot(dataSnapshot, "Choose a Category.");
                categoryOptions = Helper.getCategoryArrayFromSnapshot("Choose a Category.");
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, categoryOptions); //this, android.R.layout.simple_spinner_item, categoryOptions);

                categorySpinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        justCreatedFlagC = true;
        selectedCategory = -1;
        categorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int category, long id) {
                        if (justCreatedFlagC) {
                            justCreatedFlagC = false;
                            return;
                        } else {
                            selectedCategory = category - 1;

                         /*   subCategoryRef = categoriesRef.child(Integer.toString(selectedCategory)).child("SubCategories");
                            subCategoryRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                  /*  if (selectedCategory >= 0) {
                                        subCategoryOptions = Helper.getCategoryArrayFromSnapshot("Choose a Sub-Category.");

                                        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, subCategoryOptions); //this, android.R.layout.simple_spinner_item, categoryOptions);

                                        subCategorySpinner.setAdapter(adapter);
                                    } else {
                                        ArrayAdapter adapter = ArrayAdapter.createFromResource(getContext(), R.array.search_chooseCategory, android.R.layout.simple_spinner_item);
                                        subCategorySpinner.setAdapter(adapter);

                                }
*/
                            /*
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                        }
                        */

                    } }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        return;
                    }
                });

        justCreatedFlagSC = true;
        selectedSubCategory = -1;
      /*  subCategorySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int subCategory, long l) {
                        if (justCreatedFlagSC) {
                            justCreatedFlagSC = false;
                            return;
                        } else {
                            selectedSubCategory = subCategory - 1;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        return;
                    }
                }
        );
*/
      /*  //CLICK LISTENERS
        selectImageCameraLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set camera click listener
            }
        });


       */
        selectImageGalleryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // selectImageCameraLayout.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
        MobileAds.initialize(getContext(), getString(R.string.appid));
        interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(getString(R.string.add));
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);
        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {

            }
        });
        postItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                isItemPostable = false;
                                dialog.cancel();
                            }
                        });

                if (Helper.isEmpty(itemNameET)) {
                    builder1.setMessage("Please enter a name for your item!");
                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                } else if (Helper.isEmpty(itemPriceET)) {
                    builder1.setMessage("Please enter a price for your item!");
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else if (Helper.isEmpty(itemDescriptionET)) {
                    builder1.setMessage("Please enter a description for your item!");
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
               else if (selectedCategory < 0) {
                    builder1.setMessage("Please choose a category!");
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }


               /* else if (selectedSubCategory < 0) {
                    builder1.setMessage("Please choose a sub-category!");
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

                */
                else if (Helper.isNullOrEmpty(uploadURL)) {
                    builder1.setMessage("Please upload an image!");
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                } else {
                    isItemPostable = true;
                }

                if (isItemPostable) {
                    String itemName = itemNameET.getText().toString();
                    double itemPrice = Double.parseDouble(itemPriceET.getText().toString());
                    itemPrice = Math.round(itemPrice * 100) / 100.0;
                    String itemDescription = itemDescriptionET.getText().toString();

                    ItemDescription listingItem = new ItemDescription(uniqueItemID, user.getUid(), itemName, itemPrice, uploadURL, itemDescription, selectedCategory,"");


                    databaseItems.child(uniqueItemID).setValue(listingItem);

                    Toast.makeText(getContext(), "Item Posted", Toast.LENGTH_LONG).show();
                    Intent setIntent = new Intent(getContext(), MainActivity.class);
                    setIntent.addCategory(Intent.CATEGORY_HOME);
                    setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(setIntent);
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            }


        });
        if(isNetworkAvailable()==false)
        {
            ExpiryAlert cdd=new ExpiryAlert("No internect Connection. \n Please connect to internet and try again",getActivity());
            cdd.show();
        }
       // String androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        getPhoneNumber();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //selectImageCameraLayout.setVisibility(View.VISIBLE);
            Picasso.get().load(data.getData()).into(imageview);
            uploadImageToFirebase(data);


        }
    }

    public void uploadImageToFirebase(Intent data) {
        //  mProgressDialog.setMessage("Uploading ...\t" + 0 + "%");
        tv.setText("Uploading ...\t" + 0 + "%");
        mProgressDialog.setTitle("Uploading ...\t" + 0 + "%");
        mProgressDialog.show();

        Uri uri = data.getData();
        StorageReference filePath = mStorage.child("ItemPictures").child(uniqueItemID);

        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Photo Upload Done", Toast.LENGTH_LONG).show();
                // mProgressDialog.setMessage("Uploading ...\t" +100 + "%");
                tv.setText("Uploading ...\t" + 100 + "%");
                tv.setText("what_would_you_like_to_sell?");
                mProgressDialog.setTitle("Uploading ...\t" + 100 + "%");
                mProgressDialog.dismiss();
                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        uploadURL = uri.toString();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                mProgressDialog.dismiss();
                            }
                        });

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress1 = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                       double progress = Math.round((progress1) * 10)/10;
                       // mProgressBar.setProgress((int) progress);
                        mProgressDialog.setTitle("Uploading ...\t" + progress + "%");
                        tv.setText("Uploading ...\t" + progress + "%");
                    }
                });
    }
    public void getPhoneNumber()
    {
        final String[] result = new String[3];
        String phone=null;
        String name=null;
        String duration=null;
        FirebaseAuth authRef = FirebaseAuth.getInstance();
        FirebaseUser user = authRef.getCurrentUser();
        String sellerId = user.getUid();
        DatabaseReference sellerRef = rootRef.child("Users").child(sellerId);

        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                result[0] = dataSnapshot.child("firstName").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class);
                result[1] = dataSnapshot.child("email").getValue(String.class);

                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference usersRef = rootRef.child("Users");
                DatabaseReference exp = rootRef.child("Expiry");
                // String androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                exp.child(result[1]).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            // The child doesn't exist
                            //long millliseconds=System.currentTimeMillis()+(3600000 * 72);
                            // exp.setValue(phone+":"+millliseconds);
                            result[2] ="null";
                        }
                        else
                        {
                            result[2]=snapshot.getValue(String.class);

                            String x = result[2];

                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy  hh:mm:ss");

                            long milliSeconds= Long.parseLong(x);
                            System.out.println(milliSeconds);


                          long result=milliSeconds-System.currentTimeMillis();
                          if(result <=0)
                          {
                              itemNameET.setVisibility(View.INVISIBLE);
                              itemPriceET .setVisibility(View.INVISIBLE);
                              itemDescriptionET.setVisibility(View.INVISIBLE);
                              selectImageGalleryLayout.setVisibility(View.INVISIBLE);
                              postItemButton .setVisibility(View.INVISIBLE);
                              categorySpinner.setVisibility(View.INVISIBLE);
                              ExpiryAlert cdd=new ExpiryAlert("Your Subscription has expired. \n Kindly send Japho whattsap message via Telephone number +254741660161 \n For assistance",getActivity());
                              cdd.show();
                          }
                          else
                          {
                              Calendar calendar = Calendar.getInstance();
                              calendar.setTimeInMillis(milliSeconds);
                              String date=formatter.format(calendar.getTime());
                              //String diff=findateDiff(String.valueOf(System.currentTimeMillis()),String.valueOf(milliSeconds));
                              String result2="Your subscription will expire on:\t"+date;



                             DurationAlert cdd=new DurationAlert(getActivity(),result2);
                              cdd.show();
                          }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }
    public String findateDiff(String start_date,String end_date)
    {
// SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        String result=null;
        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();
            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));
            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

             result = difference_In_Days + "\t days \t:" + difference_In_Hours + "\t hours \t:" + difference_In_Minutes + "\t minutes \t:" + difference_In_Seconds + "\t Seconds";
        }
        catch(Exception ex)
        {
            result="Soon ";
        }
return result;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
