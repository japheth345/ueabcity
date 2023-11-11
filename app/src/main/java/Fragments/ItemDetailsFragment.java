package Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.japho.ueab.root.Helper;
import com.japho.ueab.root.ItemDescription;
import com.japho.ueab.root.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.japho.ueab.root.Helper.setImage;

public class ItemDetailsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener{
    public static String itemIDToDisplay;
    private String sellerId;
    public ItemDescription itemToDisplay;
    private final String NO_DESC = "No additional information available";
    public Button btDelete;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference itemsRef = rootRef.child("Items");
    private DatabaseReference currentUserRef;
    private DatabaseReference favRef;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private ValueEventListener mDBListener;
    private TextView name_tv, price_tv, description_tv, seller_name_tv, seller_email_tv;
    private ImageView item_iv, seller_iv;
    private ToggleButton favorite_tb;

    String sellerName, sellerEmail, sellerPhotoURL,status;
    private InterstitialAd interstitialAd;

    public ItemDetailsFragment() {
        // Required empty public constructor
    }

    public static ItemDetailsFragment newInstance() {
        return new ItemDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);

        //Get the views in case we ever want to do anything with them
        name_tv = (TextView) view.findViewById(R.id.item_name);
        price_tv = (TextView) view.findViewById(R.id.item_price);
        description_tv = (TextView) view.findViewById(R.id.item_description);
        item_iv = (ImageView) view.findViewById(R.id.item_photo);
        favorite_tb = (ToggleButton) view.findViewById(R.id.favorite);
        seller_name_tv = (TextView) view.findViewById(R.id.seller_name);
        seller_email_tv = (TextView) view.findViewById(R.id.seller_email);
        seller_iv = (ImageView) view.findViewById(R.id.seller_photo);
        btDelete=(Button) view.findViewById(R.id.bDelete);
        if (user != null) {
            setFavoriteButtonListener();

        } else {
            favorite_tb.setVisibility(View.GONE);
        }
        MobileAds.initialize(getContext(), getString(R.string.appid));
        interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(getString(R.string.item));
        AdRequest request = new AdRequest.Builder().build();
        interstitialAd.loadAd(request);
        interstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                if (interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //make the id string final to make it accessible in the onDataChange listener
        this.populateItem();
    }

    public void populateItem() {
        DatabaseReference item = itemsRef.child(itemIDToDisplay);
        itemToDisplay = new ItemDescription();

        item.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> itemsInDB = (HashMap<String, Object>) dataSnapshot.getValue();
                Map<String, Object> itemObj = (Map<String, Object>) itemsInDB;

                //get the data for the item to display
                sellerId = (String) itemObj.get("OwnerID");
                String name = (String) itemObj.get("Name");
                String description = (String) itemObj.get("Description");
                String url = (String) itemObj.get("ImageURL");
                Double price = ((Number) itemObj.get("Price")).doubleValue();
                int category = ((Number) itemObj.get("Category")).intValue();
                int subCategory = ((Number) itemObj.get("SubCategory")).intValue();
               String status= (String) itemObj.get("Status");
                String desc= (String) itemObj.get("Desciption");
                //set it
                itemToDisplay = new ItemDescription(itemIDToDisplay, sellerId, name, price, url, description, category,status);
                //Toast.makeText(getContext(),"SELLER ID:\t"+sellerId,Toast.LENGTH_LONG).show();
                btDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(getContext(), v);
                        popup.setOnMenuItemClickListener(ItemDetailsFragment.this);
                        popup.inflate(R.menu.popup);
                        popup.show();
                    } });
                        setDisplayViews();
                setSellerDetails();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setDisplayViews() {
        name_tv.setText(this.itemToDisplay.getName());

//        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String price ="Kshs.".concat(String.valueOf(this.itemToDisplay.getPrice()));
        price_tv.setText(price);

        if (Helper.isNullOrEmpty(this.itemToDisplay.getDescription())) {
            description_tv.setText(NO_DESC);
        } else {
            description_tv.setText(this.itemToDisplay.getDescription());
        }

        setImage(getActivity(), itemToDisplay.getImageURL(), item_iv);
    }

    public static void setItemIDToDisplay(String id) {
        ItemDetailsFragment.itemIDToDisplay = id;
    }

    private void setSellerDetails() {
        DatabaseReference sellerRef = rootRef.child("Users").child(sellerId);

        sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("imageURL").getValue(String.class)==null)
                {

                }
                else
                {
                  sellerPhotoURL= dataSnapshot.child("imageURL").getValue(String.class);
                    Picasso.get().load(sellerPhotoURL).into(seller_iv);

                }
                sellerName = dataSnapshot.child("firstName").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class);
                sellerEmail = dataSnapshot.child("email").getValue(String.class);
                seller_name_tv.setText(sellerName);
                seller_email_tv.setText(sellerEmail);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void setFavoriteButtonListener() {
        currentUserRef = rootRef.child("Users").child(user.getUid());
        favRef = currentUserRef.child("Favorites");

        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(itemIDToDisplay))
                    setFavToggle(true);
                else
                    setFavToggle(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setFavToggle(final boolean isFavorite) {
        rootRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            currentUserRef = rootRef.child("Users").child(user.getUid());

            if (isFavorite) {
                favorite_tb.setChecked(true);
                favorite_tb.setBackgroundResource(R.drawable.ic_star_yellow_24dp);
            } else {
                favorite_tb.setChecked(false);
                favorite_tb.setBackgroundResource(R.drawable.ic_star_border_yellow_24dp);
            }

            if (user != null) {
                favorite_tb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //usersRef.child(user.getUid()).child("Favorites").setValue(favString + ";" + holder.id.toString());
                            currentUserRef.child("Favorites").child(itemIDToDisplay).setValue(true);
                            favorite_tb.setBackgroundResource(R.drawable.ic_star_yellow_24dp);
                        } else {
                        /*newFavList.remove(newFavList.indexOf(holder.id.toString()));
                        usersRef.child(user.getUid()).child("Favorites").setValue(android.text.TextUtils.join(";", newFavList));*/
                            currentUserRef.child("Favorites").child(itemIDToDisplay).removeValue();

                            favorite_tb.setBackgroundResource(R.drawable.ic_star_border_yellow_24dp);
                        }
                    }
                });
            }
        }
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Toast.makeText(getContext(), "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.search_item:
                // do your code
                return true;
            case R.id.upload_item:
                // do your code
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("smsto:"));
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address" , new String (seller_email_tv.getText().toString()));
                smsIntent.putExtra("sms_body" , "Hey i am interested to buy your \n"+name_tv.getText().toString()+"\n you posted on UeabCity");
                startActivity(smsIntent);
                return true;
            case R.id.copy_item:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+seller_email_tv.getText().toString()));
                startActivity(intent);

                return true;
            case R.id.print_item:



                startActivity(
                        new Intent(Intent.ACTION_VIEW,
                                Uri.parse(
                                        String.format("https://api.whatsapp.com/send?phone=%s&text=%s",seller_email_tv.getText().toString() , "Hey i am interested to buy your \n"+name_tv.getText().toString()+"\n you posted on UeabCity")
                                )
                        )
                );
                return true;

            default:
                return false;
        }
    }
}
