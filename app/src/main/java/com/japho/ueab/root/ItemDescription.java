package com.japho.ueab.root;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class ItemDescription extends Listing {
    private String Description;

    public ItemDescription() {
        super();
        this.Description = "";
    }

    public ItemDescription(Listing listing, String d) {
        super(listing.getID(), listing.getName(), listing.getPrice(), listing.getImageURL());
        this.Description = d;
    }

    public ItemDescription(String id, String sellerId, String name, double price, String url, String desc) {
        super(id, sellerId);
        this.setName(name);
        this.setPrice(price);
        this.setImageURL(url);
        this.setDescription(desc);
    }

    public ItemDescription(String id, String sellerId, String name, double price, String url, String desc, int category,String status) {
        super(id, sellerId);
        this.setName(name);
        this.setPrice(price);
        this.setImageURL(url);
        this.setDescription(desc);
        this.setCategory(category);
        this.setStatus(status);
    }

    @PropertyName("Description")
    public String getDescription() {
        return this.Description;
    }

    @Exclude
    public void setDescription(String description) {
        this.Description = description;
    }

    public String getSellerID() {
        return super.getsellerId();
    }

    public String getItemID() {
        return super.getID();
    }
}