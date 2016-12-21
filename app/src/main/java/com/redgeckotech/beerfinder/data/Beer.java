package com.redgeckotech.beerfinder.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Beer implements Parcelable {

    private long id;
    private String name;
    private float abv;
    private int ibu;
    private String description;

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

    public float getAbv() {
        return abv;
    }

    public void setAbv(float abv) {
        this.abv = abv;
    }

    public int getIbu() {
        return ibu;
    }

    public void setIbu(int ibu) {
        this.ibu = ibu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Beer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", abv=" + abv +
                ", ibu=" + ibu +
                ", description='" + description + '\'' +
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
        dest.writeFloat(this.abv);
        dest.writeInt(this.ibu);
        dest.writeString(this.description);
    }

    public Beer() {
    }

    protected Beer(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.abv = in.readFloat();
        this.ibu = in.readInt();
        this.description = in.readString();
    }

    public static final Parcelable.Creator<Beer> CREATOR = new Parcelable.Creator<Beer>() {
        @Override
        public Beer createFromParcel(Parcel source) {
            return new Beer(source);
        }

        @Override
        public Beer[] newArray(int size) {
            return new Beer[size];
        }
    };
}
