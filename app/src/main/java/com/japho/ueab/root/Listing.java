package com.japho.ueab.root;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class Listing {
	private String ID;
	private String sellerId;
	private String Name;
	private double Price;
	private String ImageURL;
	private int Category;
	private int SubCategory;
private String status;
	private String desc;
	public Listing(){
		this.ID = "";
		this.sellerId = "";
		this.Name = "";
		this.Price = 0;
		this.ImageURL = "";
		this.Category = 0;
		this.SubCategory = 0;
		this.status = "";
		this.desc= "";
	}

	public Listing(String id, String sellerId){
		this.ID = id;
		this.sellerId = sellerId;
	}

	public Listing(String id, String name, double price, String imageURL) {
		this.ID = id;
		this.Name = name;
		this.Price = price;
		this.ImageURL = imageURL;
	}

	public Listing(String id, String sellerId,String name, double price, String imageURL, int category, int subCategory,String status,String desc) {
		this.ID = id;
		this.sellerId=sellerId;
		this.Name = name;
		this.Price = price;
		this.ImageURL = imageURL;
		this.Category = category;
		this.SubCategory = subCategory;
		this.status=status;
		this.desc=desc;
	}

	@PropertyName("ID")
	public String getID() {
		return ID;
	}

	@PropertyName("OwnerID")
	public String getsellerId() {
		return sellerId;
	}

	@PropertyName("Name")
	public String getName() {
		return Name;
	}
	@PropertyName("Status")
	public String getStatus() {
		return status;
	}


	@PropertyName("Price")
	public double getPrice() {
		return Price;
	}

	@PropertyName("ImageURL")
	public String getImageURL(){
		return this.ImageURL;
	}

	@PropertyName("Category")
	public int getCategory() { return this.Category; }

	@PropertyName("SubCategory")
	public int getSubCategory() { return this.SubCategory; }

	@Exclude
	public void setName(String name) {
		this.Name = name;
	}

	@Exclude
	public void setPrice(double price) {
		this.Price = price;
	}

	@Exclude
	public void setImageURL(String url){
		this.ImageURL = url;
	}

	@Exclude
	public void setCategory(int category) { this.Category = category; }
	@Exclude
	public void setStatus(String status) { this.status = status; }

	@Exclude
	public void setSubCategory(int subCategory) { this.SubCategory = subCategory; }
}
