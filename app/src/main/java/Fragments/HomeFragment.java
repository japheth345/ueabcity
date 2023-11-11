package Fragments;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.japho.ueab.root.Adapters.ListItemAdapter;
import com.japho.ueab.root.FilterObject;
import com.japho.ueab.root.Helper;
import com.japho.ueab.root.Listing;
import com.japho.ueab.root.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class HomeFragment extends Fragment {
    //I chose to use an unfilteredlist to base filters off of. This way, the database is only called when something is changed in the database.
    //Otherwise, every time the filter is changed, we would have to get the items from the database again.
    private ArrayList<Listing> unfilteredList = new ArrayList<>();
    private ArrayList<Listing> listingsList = new ArrayList<>();
    private ListItemAdapter mAdapter;
    private boolean isViewFiltered;
    public static FilterObject itemFilter;
    public static boolean applyAdvancedFilter;
    private AdView mAdView;
    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference itemsRef = rootRef.child("Items");
    private ProgressBar mProgressBar;
    TextView tv;
    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mAdView = view.findViewById(R.id.adView);
        mAdapter = new ListItemAdapter(listingsList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mProgressBar = view.findViewById(R.id.myDataLoaderProgressBar);
        tv=view.findViewById(R.id.textView);
        mProgressBar.setVisibility(View.VISIBLE);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            //OnDataChange gets the full database every time something is changed inside of it.
            public void onDataChange(DataSnapshot dataSnapshot) {
                //clear the listingslist so we can add the items again (with changes)
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    unfilteredList.clear();
                    listingsList.clear();
                    //itemsMap is a map of every item in the 'Items' database
                    Map<String, Object> itemsMap = (HashMap<String, Object>) dataSnapshot.getValue();

                    for (String key : itemsMap.keySet()) {
                        if (!Helper.isNullOrEmpty(key)) {
                            Object itemMap = itemsMap.get(key);
                            //itemMap is a single item, but still in json format.
                            //From this object, extract wanted data to item, and add it to our list of items.
                            if (itemMap instanceof Map) {
                                Map<String, Object> itemObj = (Map<String, Object>) itemMap;
                                String seller = (String) itemObj.get("OwnerID");
                                String name = (String) itemObj.get("Name");
                                Double price = ((Number) itemObj.get("Price")).doubleValue();
                                String url = (String) itemObj.get("ImageURL");
                                int category = ((Number) itemObj.get("Category")).intValue();
                                int subCategory = ((Number) itemObj.get("SubCategory")).intValue();
                                String status = (String) itemObj.get("Status");
                                String desc = (String) itemObj.get("Description");
                                Listing item = new Listing(key, seller, name, price, url, category, subCategory, status,desc);

                                DatabaseReference sellerRef = rootRef.child("Users").child(seller);

                                sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        // result[0] = dataSnapshot.child("firstName").getValue(String.class) + " " + dataSnapshot.child("lastName").getValue(String.class);
                                        String phone = dataSnapshot.child("email").getValue(String.class);

                                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                                        DatabaseReference exp = rootRef.child("Expiry");
                                        // String androidID = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                                        exp.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot snapshot) {
                                                if (snapshot.getValue() == null) {
                                                    // The child doesn't exist
                                                    //long millliseconds=System.currentTimeMillis()+(3600000 * 72);
                                                    // exp.setValue(phone+":"+millliseconds);
                                                    //result[2] ="null";
                                                    if (doNotFilterOutItem(item)) {
                                                        listingsList.add(item);
                                                    }
                                                    unfilteredList.add(item);
                                                    mAdapter.notifyDataSetChanged();
                                                } else {
                                                    String x = snapshot.getValue(String.class);


                                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy  hh:mm:ss");

                                                    long milliSeconds = Long.parseLong(x);
                                                    //System.out.println(milliSeconds);


                                                    long result = milliSeconds - System.currentTimeMillis();
                                                    if (result <= 0) {


                                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                        if (seller.equalsIgnoreCase(user.getUid())) {

                                                            Listing  item2 = new Listing(key, seller, name, price, url, category, subCategory, "Not Displayed,Your subscription expired \n Whattsap japho \t +254741660161 \n For assistance",desc);
                                                            if (doNotFilterOutItem(item2)) {
                                                                listingsList.add(item2);

                                                            }
                                                            unfilteredList.add(item2);
                                                            mAdapter.notifyDataSetChanged();
                                                        } else {

                                                        }
                                                    } else {
                                                        Listing item = new Listing(key, seller, name, price, url, category, subCategory, status,desc);

                                                        if (doNotFilterOutItem(item)) {
                                                            listingsList.add(item);
                                                        }
                                                        unfilteredList.add(item);
                                                        mAdapter.notifyDataSetChanged();
                                                    }

                                                    //filter the item out of the display6 list if necessary
                               /* if (doNotFilterOutItem(item)) {
                                    listingsList.add(item);
                                }
                                unfilteredList.add(item);
                                */

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            }
                        }
                    }
                }
                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.GONE);
                tv.setText("Available items ");
            }
                                @Override
            public void onCancelled(DatabaseError databaseError) {
                                    mProgressBar.setVisibility(View.INVISIBLE);
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

    public boolean doNotFilterOutItem(Listing itemToFilter){
        if(!isViewFiltered && !HomeFragment.applyAdvancedFilter)
            return true;
        else{
            boolean containsString =  HomeFragment.itemFilter.isContainedIn(itemToFilter.getName());
            boolean inPriceRange = HomeFragment.itemFilter.isInPriceRange(itemToFilter.getPrice());
            boolean isRightCategory = HomeFragment.itemFilter.isCategory(itemToFilter.getCategory());
            boolean isRightSubCategory = HomeFragment.itemFilter.isSubCategory(itemToFilter.getSubCategory());

            return containsString && inPriceRange && isRightCategory && isRightSubCategory;
        }
    }

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		MenuItem mSearchMenuItem = menu.findItem(R.id.action_search_query);
		SearchView searchView = (SearchView) mSearchMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Filters the listingsListdata set

					if (query != null && !query.isEmpty()) {
						isViewFiltered = true;
						HomeFragment.itemFilter.setStringFilter(query.toLowerCase());
					}
					listingsList.clear();
					//To filter, go through the unfiltered list and only add the wanted items to the list to listingslist, which is the displayed list
					for (Listing item : unfilteredList) {
						if (doNotFilterOutItem(item)) {
							listingsList.add(item);
						}
					}
					mAdapter.notifyDataSetChanged();
					return false;
				}

            @Override
            // Responsible for displaying all possible string from the list based on each additionnal character input made by user
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase(); //eliminate possibility of uppercases

                listingsList.clear();
                for (Listing list : unfilteredList) {
                    final String text = list.getName().toLowerCase();
                    if (text.contains(newText)) { //adding all items that match the query string to the filtered arraylist
                        listingsList.add(list);
                    }
                }

                mAdapter.notifyDataSetChanged(); //notify the adapter that the dataset was changed
                return true;
            }
        });

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
