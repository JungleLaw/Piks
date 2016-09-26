package com.law.piks.medias.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Law on 2016/9/9.
 */
public class Album implements Serializable, Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    private String name;
    private String path;
    private String storageRootPath;
    private List<Media> medias;
    private int count;

    public Album() {
    }

    private Album(Parcel in) {
        name = in.readString();
        path = in.readString();
        storageRootPath = in.readString();
        medias = in.readArrayList(List.class.getClassLoader());
        count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(path);
        parcel.writeString(storageRootPath);
        parcel.writeList(medias);
        parcel.writeInt(count);
    }

    public String getStorageRootPath() {
        return storageRootPath;
    }

    public void setStorageRootPath(String storageRootPath) {
        this.storageRootPath = storageRootPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public List<Media> getMedias() {
        if (medias == null)
            medias = new ArrayList<>();
        return medias;
    }

    public void setMedias(List<Media> medias) {
        this.medias = medias;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Media getAlbumCover() {
        return medias != null && medias.size() > 0 ? medias.get(0) : null;
    }


    @Override
    public String toString() {
        return "Album{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", storageRootPath='" + storageRootPath + '\'' +
                ", medias=" + medias +
                ", count=" + count +
                '}';
    }
}
