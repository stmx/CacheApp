package com.stmlab.android.cacheapp.models;

import com.google.gson.annotations.SerializedName;

public class TrefleModel {
    @SerializedName("id")
    private int mId;
    @SerializedName("common_name")
    private String mCommonName;
    @SerializedName("scientific_name")
    private String mScientificName;
    @SerializedName("bibliography")
    private String mBibliography;
    @SerializedName("family")
    private String mFamily;
    @SerializedName("image_url")
    private String mImageUrl;
    @SerializedName("year")
    private int mYear;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCommonName() {
        return mCommonName;
    }

    public void setCommonName(String commonName) {
        if ( commonName == null ) {
            commonName = "";
        }
        mCommonName = commonName;
    }

    public String getScientificName() {
        return mScientificName;
    }

    public void setScientificName(String scientificName) {
        if ( scientificName == null ) {
            scientificName = "";
        }
        mScientificName = scientificName;
    }

    public String getBibliography() {
        return mBibliography;
    }

    public void setBibliography(String bibliography) {
        if ( bibliography == null ) {
            bibliography = "";
        }
        mBibliography = bibliography;
    }

    public String getFamily() {
        return mFamily;
    }

    public void setFamily(String family) {
        if ( family == null ) {
            family = "";
        }
        mFamily = family;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        if ( imageUrl == null ) {
            imageUrl = "";
        }
        mImageUrl = imageUrl;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }
}
