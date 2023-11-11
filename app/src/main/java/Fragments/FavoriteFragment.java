package Fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.japho.ueab.root.Adapters.ListItemAdapter;
import com.japho.ueab.root.Listing;
import com.japho.ueab.root.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavoriteFragment extends Fragment {

    private FirebaseAuth authRef = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference itemsRef = rootRef.child("Items");
    private DatabaseReference currentUserRef;
    private DatabaseReference favRef;

    private ArrayList<Listing> listingsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ListItemAdapter mAdapter;

    private TextView fav_message_tv;
    private AdView mAdView;
    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance() {
        return new FavoriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        mAdView = view.findViewById(R.id.adView);
        currentUserRef = rootRef.child("Users").child(user.getUid());
        favRef = currentUserRef.child("Favorites");

        final View currentView = view;
        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren())
                    populateFavoritesList(currentView, dataSnapshot);
                else
                    setMessage(currentView, R.string.no_favorites);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Toast.makeText(getContext(), " sucesfull ", Toast.LENGTH_SHORT).show();
            }
        });
        if(isNetworkAvailable()==false)
        {
            ExpiryAlert cdd=new ExpiryAlert("No internect Connection. \n Please connect to internet and try again",getActivity());
            cdd.show();
        }
        //mAdView.setAdUnitId(getString(R.string.btest));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return view;
    }

    public void populateFavoritesList(View view, final DataSnapshot favRef) {
        recyclerView = (RecyclerView) view.findViewById(R.id.fav_recycler_view);
        mAdapter = new ListItemAdapter(listingsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listingsList.clear();
                Map<String, Object> favMap = (HashMap<String, Object>) favRef.getValue();
                Map<String, Object> itemsMap = (HashMap<String, Object>) dataSnapshot.getValue();

                for (String key : favMap.keySet()) {
                    Object itemMap = itemsMap.get(key);
                    if (itemMap instanceof Map) {
                        Map<String, Object> itemObj = (Map<String, Object>) itemMap;

                        String name = (String) itemObj.get("Name");
                        Double price = ((Number) itemObj.get("Price")).doubleValue();
                        String url = (String) itemObj.get("ImageURL");

                        Listing item = new Listing(key, name, price, url);

                        listingsList.add(item);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void setMessage(View view, int messageID) {
        fav_message_tv = (TextView) view.findViewById(R.id.fav_message);
        fav_message_tv.setText(messageID);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
