package com.law.piks.medias.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.lang.Rational;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.GpsDirectory;
import com.law.piks.medias.engine.Constants;
import com.law.piks.medias.utils.MediaUtils;
import com.law.think.frame.utils.EncryptUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * Created by Law on 2016/9/9.
 */
public class Media extends RealmObject implements Parcelable, Serializable {
    public static final String UNKNOWN_MIME = "unknown";

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Media> CREATOR = new Parcelable.Creator<Media>() {
        @Override
        public Media createFromParcel(Parcel in) {
            return new Media(in);
        }

        @Override
        public Media[] newArray(int size) {
            return new Media[size];
        }
    };
    @Ignore
    private HashMap<String, Object> metadatas = new HashMap<>();
    private long id;
    private String name;
    private String path;
    @Ignore
    private Uri uri;
    private long modifiedDate = -1;
    private String mime;
    private long size;
    private int width;
    private int height;

    protected Media(Parcel in) {
        metadatas = in.readHashMap(HashMap.class.getClassLoader());
        id = in.readLong();
        name = in.readString();
        path = in.readString();
        modifiedDate = in.readLong();
        mime = in.readString();
        size = in.readLong();
        width = in.readInt();
        height = in.readInt();
    }

    public Media() {
    }

    public Media(String path, long modifiedDate) {
        this.path = path;
        this.modifiedDate = modifiedDate;
        setMIME();
    }

    public Media(File file) {
        this.path = file.getAbsolutePath();
        this.modifiedDate = file.lastModified();
        this.size = file.length();
        setMIME();
    }

    public Media(String path) {
        this.path = path;
        setMIME();
    }

    public String getMimeType() {
        return MediaUtils.getMimeType(path);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(metadatas);
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeLong(modifiedDate);
        dest.writeString(mime);
        dest.writeLong(size);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    private void setMIME() {
        String extension = path.substring(path.lastIndexOf('.') + 1);
        mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        if (mime == null)
            mime = UNKNOWN_MIME;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMIME() {
        if (TextUtils.isEmpty(mime))
            setMIME();
        return mime;
    }

    public boolean isGif() {
        return getMIME().endsWith("gif");
    }

    public boolean isImage() {
        return getMIME().startsWith("image");
    }

    public boolean isVideo() {
        return getMIME().startsWith("video");
    }

    public Uri getUri() {
        return isMediaInStorage() ? Uri.fromFile(new File(path)) : uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isMediaInStorage() {
        return path == null;
    }

    public byte[] getThumbnail() {

        ExifInterface exif;
        try {
            exif = new ExifInterface(getPath());
        } catch (IOException e) {
            return null;
        }
        byte[] imageData = exif.getThumbnail();
        if (imageData != null)
            return imageData;
        return null;

        // NOTE: ExifInterface is faster than metadata-extractor to get the
        // thumbnail data
        /*
         * try { Metadata metadata = ImageMetadataReader.readMetadata(new
		 * File(getPath())); ExifThumbnailDirectory thumbnailDirectory =
		 * metadata.getFirstDirectoryOfType(ExifThumbnailDirectory.class); if
		 * (thumbnailDirectory.hasThumbnailData()) return
		 * thumbnailDirectory.getThumbnailData(); } catch (Exception e) { return
		 * null; }
		 */
    }

    public GeoLocation getGeoLocation() {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(new File(getPath()));
            GpsDirectory thumbnailDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            return thumbnailDirectory.getGeoLocation();
        } catch (Exception e) {
            return null;
        }
    }

    private void loadMetadata() {
        if (metadatas.isEmpty()) {
            Metadata metadata = null;
            try {
                metadata = ImageMetadataReader.readMetadata(new File(getPath()));
                for (Directory directory : metadata.getDirectories())
                    for (Tag tag : directory.getTags()) {
                        metadatas.put(tag.getTagName(), directory.getObject(tag.getTagType()));
                    }
            } catch (ImageProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getOrientation() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.ORIENTATION)) {
            switch (Integer.parseInt(metadatas.get(Constants.Gallery.Media.TAG.ORIENTATION).toString())) {
                case Constants.Gallery.Media.ORIENTATION.NORMAL:
                    return 0;
                case Constants.Gallery.Media.ORIENTATION.ROTATE_90:
                    return 90;
                case Constants.Gallery.Media.ORIENTATION.ROTATE_180:
                    return 180;
                case Constants.Gallery.Media.ORIENTATION.ROTATE_270:
                    return 270;
                default:
                    return -1;
            }
        }
        return -1;
    }

    public boolean setOrientation(int orientation) {
        /*
         * int asd; ExifInterface exif; try { exif = new
		 * ExifInterface(getPath()); } catch (IOException ex) { return false; }
		 * switch (orientation) { case 90: asd =
		 * ExifInterface.ORIENTATION_ROTATE_90; break; case 180: asd =
		 * ExifInterface.ORIENTATION_ROTATE_180; break; case 270: asd =
		 * ExifInterface.ORIENTATION_ROTATE_270; break; case 0: asd = 1; break;
		 * default: return false; }
		 * exif.setAttribute(ExifInterface.TAG_ORIENTATION, asd+""); try {
		 * exif.saveAttributes(); } catch (IOException e) { return false;}
		 * return true;
		 */
        return false;
    }

    public String getExifInfo() {
        return String.format("%s %s %s", getFNumber(), getExposureTime(), getISO());
    }

    private Rational getRational(Object o) {
        if (o == null)
            return null;

        if (o instanceof Rational)
            return (Rational) o;
        if (o instanceof Integer)
            return new Rational((Integer) o, 1);
        if (o instanceof Long)
            return new Rational((Long) o, 1);

        // NOTE not doing conversions for real number types

        return null;
    }

    private String getFNumber() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.F_NUMBER)) {
            DecimalFormat format = new DecimalFormat("0.0");
            format.setRoundingMode(RoundingMode.HALF_UP);
            Rational f = getRational(metadatas.get(Constants.Gallery.Media.TAG.F_NUMBER));
            return "f/" + format.format(f.doubleValue());
        }
        return null;
    }

    private String getExposureTime() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.EXPOSURE)) {
            DecimalFormat format = new DecimalFormat("0.000");
            format.setRoundingMode(RoundingMode.HALF_UP);
            Rational f = getRational(metadatas.get(Constants.Gallery.Media.TAG.EXPOSURE));
            return format.format(f.doubleValue()) + "s";
        }
        return null;
    }

    private String getISO() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.ISO))
            return "ISO-" + metadatas.get(Constants.Gallery.Media.TAG.ISO).toString();
        return null;
    }

    public String getCameraInfo() {
        String make;
        if ((make = getMake()) != null)
            return String.format("%s %s", make, getModel());
        return null;
    }

    public String getMake() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.MAKE))
            return metadatas.get(Constants.Gallery.Media.TAG.MAKE).toString();
        return null;
    }

    public String getModel() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.MODEL))
            return metadatas.get(Constants.Gallery.Media.TAG.MODEL).toString();
        return null;
    }

    public int getWidth() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.WIDTH))
            return Integer.parseInt(metadatas.get(Constants.Gallery.Media.TAG.WIDTH).toString());
        return -1;
    }

    public int getHeight() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.HEIGHT))
            return Integer.parseInt(metadatas.get(Constants.Gallery.Media.TAG.HEIGHT).toString());
        return -1;
    }

    //    public int getWidth() {
    //        return width;
    //    }
    //
    //    public void setWidth(int width) {
    //        this.width = width;
    //    }
    //
    //    public int getHeight() {
    //        return height;
    //    }
    //
    //    public void setHeight(int height) {
    //        this.height = height;
    //    }

    public long getDateTaken() {
        loadMetadata();
        if (!metadatas.isEmpty() && metadatas.containsKey(Constants.Gallery.Media.TAG.DATE_TAKEN))
            try {
                return new SimpleDateFormat("yyyy:MM:dd HH:mm:ss").parse(metadatas.get(Constants.Gallery.Media.TAG.DATE_TAKEN).toString()).getTime();
            } catch (ParseException e) {
                return -1;
            } catch (NullPointerException e) {
                return -1;
            }
        return -1;
    }

    public boolean fixDate() {
        long newDate = getDateTaken();
        if (newDate != -1) {
            File f = new File(getPath());
            if (f.setLastModified(newDate)) {
                modifiedDate = newDate;
                return true;
            }
        }
        return false;
    }

    public String getResolution() {
        return String.format(Locale.getDefault(), "%dx%d", getWidth(), getHeight());
    }

    public String getHumanReadableSize() {
        return MediaUtils.humanReadableByteCount(size, true);
    }

    public long getDate() {
        long exifDate = getDateTaken();
        return exifDate != -1 ? exifDate : modifiedDate;
    }

    public long getDateModified() {
        return modifiedDate;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Bitmap getBitmap() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        return bitmap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public String signature() {
        return EncryptUtils.encryptMD5ToString(getPath() + "_" + getDateModified());
    }

    public HashMap<String, Object> getMetadatas() {
        return metadatas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    @Override
    public String toString() {
        return "Media{" +
                "metadatas=" + metadatas +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", uri=" + uri +
                ", modifiedDate=" + modifiedDate +
                ", mime='" + mime + '\'' +
                ", size=" + size +
                ", width=" + getWidth() +
                ", height=" + getHeight() +
                '}';
    }
}
