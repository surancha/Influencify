package com.example.influencify;

public class Influencer {
    private String name;
    private String category;
    private float rating;
    private int rpp;
    private int followers;
    private int cpp;
    private int price;
    private int heartCount; // Added to track the heart/like count

    public Influencer(String name, String category, float rating, int rpp, int followers, int cpp, int price, int heartCount) {
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.rpp = rpp;
        this.followers = followers;
        this.cpp = cpp;
        this.price = price;
        this.heartCount = heartCount;
    }

    // Getters
    public String getName() { return name; }
    public String getCategory() { return category; }
    public float getRating() { return rating; }
    public int getRpp() { return rpp; }
    public int getFollowers() { return followers; }
    public int getCpp() { return cpp; }
    public int getPrice() { return price; }
    public int getHeartCount() { return heartCount; }

    // Setters
    public void setRating(float rating) { this.rating = rating; }
    public void setRpp(int rpp) { this.rpp = rpp; }
    public void setFollowers(int followers) { this.followers = followers; }
    public void setCpp(int cpp) { this.cpp = cpp; }
    public void setPrice(int price) { this.price = price; }
    public void setHeartCount(int heartCount) { this.heartCount = heartCount; }
}