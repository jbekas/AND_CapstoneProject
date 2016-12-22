package com.redgeckotech.beerfinder.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class Brewery implements Parcelable {

    private long id;
    private String name;
    private String address;
    private String address2;
    private String city;
    private String state;
    private String postalCode;
    private double latitude;
    private double longitude;
    private String phone;
    private String taproomHours;
    private String tourHours;
    private List<Beer> beerList;
    private String logoUrl;
    private String photoUrl;
    private String website;
    private String twitter;

    public Brewery() {
    }

    public Brewery(Cursor cursor) {
        id = cursor.getLong(BreweryLoader.Query._ID);
        name = cursor.getString(BreweryLoader.Query.NAME);
        address = cursor.getString(BreweryLoader.Query.ADDRESS);
        address2 = cursor.getString(BreweryLoader.Query.ADDRESS2);
        city = cursor.getString(BreweryLoader.Query.CITY);
        state = cursor.getString(BreweryLoader.Query.STATE);
        postalCode = cursor.getString(BreweryLoader.Query.POSTAL_CODE);
        phone = cursor.getString(BreweryLoader.Query.PHONE);
        latitude = cursor.getDouble(BreweryLoader.Query.LATITUDE);
        longitude = cursor.getDouble(BreweryLoader.Query.LONGITUDE);
        taproomHours = cursor.getString(BreweryLoader.Query.TAPROOM_HOURS);
        tourHours = cursor.getString(BreweryLoader.Query.TOUR_HOURS);
        String beerListString = cursor.getString(BreweryLoader.Query.BEER_LIST);
        Timber.d("beerListString: %s", beerListString);
        Beer[] beers = new Gson().fromJson(beerListString, Beer[].class);
        if (beers != null) {
            beerList = Arrays.asList(beers);
        } else {
            beerList = new ArrayList<>();
        }
        logoUrl = cursor.getString(BreweryLoader.Query.LOGO_URL);
        photoUrl = cursor.getString(BreweryLoader.Query.PHOTO_URL);
        website = cursor.getString(BreweryLoader.Query.WEBSITE);
        twitter = cursor.getString(BreweryLoader.Query.TWITTER);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTaproomHours() {
        return taproomHours;
    }

    public void setTaproomHours(String taproomHours) {
        this.taproomHours = taproomHours;
    }

    public String getTourHours() {
        return tourHours;
    }

    public void setTourHours(String tourHours) {
        this.tourHours = tourHours;
    }

    public List<Beer> getBeerList() {
        return beerList;
    }

    public void setBeerList(List<Beer> beerList) {
        this.beerList = beerList;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    //
    // helpers
    //

    public String getBeerListAsJsonString() {
        if (beerList != null) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();

            JSONArray jsonArray = new JSONArray();
            for (Beer beer : beerList) {
                try {
                    jsonArray.put(new JSONObject(gson.toJson(beer)));
                } catch (Exception e) {
                    Timber.e(e, null);
                }
            }

            return jsonArray.toString();
        } else {
            return "[]";
        }
    }

    @Override
    public String toString() {
        return "Brewery{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", phone='" + phone + '\'' +
                ", taproomHours='" + taproomHours + '\'' +
                ", tourHours='" + tourHours + '\'' +
                ", beerList=" + beerList +
                ", logoUrl='" + logoUrl + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", website='" + website + '\'' +
                ", twitter='" + twitter + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeString(this.address2);
        dest.writeString(this.city);
        dest.writeString(this.state);
        dest.writeString(this.postalCode);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.phone);
        dest.writeString(this.taproomHours);
        dest.writeString(this.tourHours);
        dest.writeTypedList(this.beerList);
        dest.writeString(this.logoUrl);
        dest.writeString(this.photoUrl);
        dest.writeString(this.website);
        dest.writeString(this.twitter);
    }

    protected Brewery(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.address = in.readString();
        this.address2 = in.readString();
        this.city = in.readString();
        this.state = in.readString();
        this.postalCode = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.phone = in.readString();
        this.taproomHours = in.readString();
        this.tourHours = in.readString();
        this.beerList = in.createTypedArrayList(Beer.CREATOR);
        this.logoUrl = in.readString();
        this.photoUrl = in.readString();
        this.website = in.readString();
        this.twitter = in.readString();
    }

    public static final Creator<Brewery> CREATOR = new Creator<Brewery>() {
        @Override
        public Brewery createFromParcel(Parcel source) {
            return new Brewery(source);
        }

        @Override
        public Brewery[] newArray(int size) {
            return new Brewery[size];
        }
    };
}
