
package com.japho.ueab.root.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.japho.ueab.root.Listing;
import com.japho.ueab.root.R;

import java.util.List;

import static com.japho.ueab.root.Helper.setImage;

public class ListItemAdapter extends RecyclerView.Adapter<ListItemAdapter.ViewHolder> {
    private List<Listing> itemList;

    private DatabaseReference rootRef;
    private DatabaseReference currentUserRef;
    private DatabaseReference favRef;

    private FirebaseUser user;

    // View holder is what holds the views
    public class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView name_tv;
        public TextView price_tv,status,desc;
        public ImageView image_iv;
        public ToggleButton fav;
        public Button btDelete;
        public String id;

        public ViewHolder(View v) {
            super(v);
            name_tv = (TextView) v.findViewById(R.id.list_item_name);
            price_tv = (TextView) v.findViewById(R.id.list_item_price);
             status = (TextView) v.findViewById(R.id.status);
           desc = (TextView) v.findViewById(R.id.desc);
            image_iv = (ImageView) v.findViewById(R.id.list_item_photo);
            fav = (ToggleButton) v.findViewById(R.id.list_item_fav);
            btDelete=(Button) v.findViewById(R.id.bDelete);
            view = v;
        }
    }

    public ListItemAdapter(List<Listing> itemList) {
        this.itemList = itemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        rootRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        Listing listItem = itemList.get(position);
        String se = listItem.getsellerId();
        if (se.equalsIgnoreCase(user.getUid())) {
            // holder.btDelete.setText("MINE");

        } else {
            holder.btDelete.setVisibility(View.INVISIBLE);

        }
        holder.id = listItem.getID();
        holder.name_tv.setText(listItem.getName());

        // NumberFormat formatter = NumberFormat.getCurrencyInstance();
        // String price = formatter.format(listItem.getPrice());
        holder.price_tv.setText("Kshs." + listItem.getPrice());
       // holder.desc.setText(listItem.getDesc());
        holder.status.setText(listItem.getStatus());
        holder.view.setTag(listItem.getID());

        String imgUrl = listItem.getImageURL();
        setImage(holder.view, imgUrl, holder.image_iv);

        if (user != null) {
            currentUserRef = rootRef.child("Users").child(user.getUid());
            favRef = currentUserRef.child("Favorites");

            final ViewHolder currentHolder = holder;
            favRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(currentHolder.id))
                        setFavToggle(currentHolder, true);
                    else
                        setFavToggle(currentHolder, false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            holder.fav.setVisibility(View.GONE);
        }
        holder.btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

                Listing listItem = itemList.get(position);
                String id = listItem.getID();
                DatabaseReference itemsRef = rootRef.child("Items").child(id);
                itemsRef.removeValue();
                itemList.remove(position);
                 StorageReference mStorage;
                mStorage = FirebaseStorage.getInstance().getReference();
                StorageReference filePath = mStorage.child("ItemPictures").child(id);
                filePath.delete();
                ListItemAdapter.super.notifyDataSetChanged();
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public Listing getListItem(int pos) {
        return itemList.get(pos);
    }

    public void setFavToggle(final ViewHolder holder, final boolean isFavorite) {
        rootRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            currentUserRef = rootRef.child("Users").child(user.getUid());

            if (isFavorite) {
                holder.fav.setChecked(true);
                holder.fav.setBackgroundResource(R.drawable.ic_star_yellow_24dp);
            } else {
                holder.fav.setChecked(false);
                holder.fav.setBackgroundResource(R.drawable.ic_star_border_yellow_24dp);
            }

            if (user != null) {
                holder.fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            //usersRef.child(user.getUid()).child("Favorites").setValue(favString + ";" + holder.id.toString());
                            currentUserRef.child("Favorites").child(holder.id).setValue(true);
                            holder.fav.setBackgroundResource(R.drawable.ic_star_yellow_24dp);
                        } else {
                        /*newFavList.remove(newFavList.indexOf(holder.id.toString()));
                        usersRef.child(user.getUid()).child("Favorites").setValue(android.text.TextUtils.join(";", newFavList));*/
                            currentUserRef.child("Favorites").child(holder.id).removeValue();

                            holder.fav.setBackgroundResource(R.drawable.ic_star_border_yellow_24dp);
                        }
                    }
                });
            }
        }
    }
}
