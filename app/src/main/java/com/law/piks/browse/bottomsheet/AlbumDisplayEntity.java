package com.law.piks.browse.bottomsheet;

import android.os.Parcel;
import android.os.Parcelable;

import com.law.piks.medias.entity.Media;

import java.io.Serializable;

/**
 * Created by Law on 2016/10/13.
 */

public class AlbumDisplayEntity implements Serializable, Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AlbumDisplayEntity createFromParcel(Parcel in) {
            return new AlbumDisplayEntity(in);
        }

        public AlbumDisplayEntity[] newArray(int size) {
            return new AlbumDisplayEntity[size];
        }
    };
    private String name;
    private String path;
    private String storageRootPath;
    private String imgCoverPath;
    private int size;

    public AlbumDisplayEntity() {
    }

    private AlbumDisplayEntity(Parcel in) {
        name = in.readString();
        path = in.readString();
        storageRootPath = in.readString();
        imgCoverPath = in.readString();
        size = in.readInt();
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
        parcel.writeString(imgCoverPath);
        parcel.writeInt(size);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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

    public String getImgCoverPath() {
        return imgCoverPath;
    }

    public void setImgCoverPath(String imgCoverPath) {
        this.imgCoverPath = imgCoverPath;
    }

    public String getStorageRootPath() {
        return storageRootPath;
    }

    public void setStorageRootPath(String storageRootPath) {
        this.storageRootPath = storageRootPath;
    }
}
