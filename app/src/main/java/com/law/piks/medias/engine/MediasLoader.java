package com.law.piks.medias.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.law.piks.medias.entity.Album;
import com.law.piks.medias.entity.Media;
import com.law.think.frame.utils.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Law on 2016/9/12.
 */
public class MediasLoader {
    //MediaStore.Images.Media.INTERNAL_CONTENT_URI,
    public static final Uri[] PHOTO_URI = new Uri[]{MediaStore.Images.Media.EXTERNAL_CONTENT_URI};

    public static final String[] PHOTO_PROJECTIONS = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.WIDTH, MediaStore.Images.ImageColumns.HEIGHT, MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DATE_ADDED};
    public static final String[] ALBUM_PROJECTIONS = new String[]{"distinct " + MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.BUCKET_ID};

    private static MediasLoader instance = new MediasLoader();
    private Context mContext;
    private List<Album> albumList;
    private List<Long> collectsIds;

    private MediasLoader() {
    }

    public static MediasLoader getInstance() {
        return instance;
    }

    public void loadPhotos(Context context, PhotosLoadHandler mPhotosLoadHandler) {
        this.mContext = context;
        new LoadPhotosTask(mPhotosLoadHandler).execute();
    }

    private List<Album> load() {
        albumList = new ArrayList<>();
        collectsIds = new ArrayList<>();
        collectsIds.addAll(CollectionsEngine.getCollectsIds());
        List<String> mAlbumNames = getAlbumNames();
        for (String name : mAlbumNames) {
            Album album = getMediasByAlbumName(name);
            if (album.getMedias().size() != 0)
                albumList.add(album);
        }
        ArrayList<Media> allMedias = new ArrayList<>();
        for (Album album : albumList) {
            allMedias.addAll(album.getMedias());
        }
        Album album = new Album();
        //        album.setStorageRootPath(root.getAbsolutePath());
        Collections.sort(allMedias, new MediaComparators(false).getDateComparator());
        album.setMedias(allMedias);
        //        album.setPath(root.getAbsolutePath());
        album.setCount(allMedias.size());
        album.setName("All");
        albumList.add(0, album);
        AlbumsComparators albumsComparators = new AlbumsComparators(false);
        Collections.sort(albumList, albumsComparators.getSizeComparator());
        //        if (CollectionsEngine.getCollects() != null && CollectionsEngine.getCollects().getMedias().size() > 0) {
        //            albumList.add(CollectionsEngine.getCollects());
        //        }
        return albumList;
    }

    private void getFavorites() {

    }

    private List<String> getAlbumNames() {
        List<String> mAlbumNames = new ArrayList<>();
        for (int i = 0, size = MediasLoader.PHOTO_URI.length; i < size; i++) {
            Cursor mAlbumCursor = MediaStore.Images.Media.query(mContext.getContentResolver(), MediasLoader.PHOTO_URI[i], MediasLoader.ALBUM_PROJECTIONS, null, MediaStore.Images.ImageColumns.BUCKET_ID + " asc");
            if (mAlbumCursor.getCount() <= 0)
                continue;
            mAlbumCursor.moveToFirst();
            do {
                mAlbumNames.add(mAlbumCursor.getString(mAlbumCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
            } while (mAlbumCursor.moveToNext());
            mAlbumCursor.close();
            mAlbumCursor = null;
        }
        return mAlbumNames;
    }

    public List<Album> getAlbumsSimple() {
        List<Album> mAlbums = new ArrayList<>();
        for (Album album : albumList) {
            if (album.getName().equalsIgnoreCase("All") || album.getName().equalsIgnoreCase("Collects"))
                continue;
            Album albumSimple = new Album();
            albumSimple.setName(album.getName());
            albumSimple.setPath(album.getPath());
            albumSimple.setCount(album.getCount());
            albumSimple.setStorageRootPath(album.getStorageRootPath());
            List<Media> medias = new ArrayList<>();
            medias.add(album.getAlbumCover());
            albumSimple.setMedias(medias);
            mAlbums.add(album);
        }
        return mAlbums;
    }

    private Album getMediasByAlbumName(String albumName) {
        Album mAlbum = new Album();
        mAlbum.setName(albumName);
        mAlbum.setMedias(new ArrayList<Media>());
        for (int i = 0, size = MediasLoader.PHOTO_URI.length; i < size; i++) {
            Cursor mPhotosCursor = MediaStore.Images.Media.query(mContext.getContentResolver(), MediasLoader.PHOTO_URI[i], MediasLoader.PHOTO_PROJECTIONS, MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME + " = '" + albumName + "'" + " AND " + MediaStore.Images.Media.MIME_TYPE + " in('image/jpeg','image/png','image/gif')", MediaStore.Images.ImageColumns.DATE_ADDED + " desc");
            if (mPhotosCursor.getCount() <= 0)
                continue;
            mPhotosCursor.moveToFirst();
            do {
                Media mMedia = new Media();
                mMedia.setId(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)));
                mMedia.setName(mPhotosCursor.getString(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME)));
                mMedia.setSize(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)));
                mMedia.setPath(mPhotosCursor.getString(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                mMedia.setModifiedDate(Long.valueOf(mPhotosCursor.getString(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED))));
                mMedia.setOrientation(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)));
                //                mMedia.setWidth(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.WIDTH)));
                //                mMedia.setHeight(mPhotosCursor.getInt(mPhotosCursor.getColumnIndex(MediaStore.Images.ImageColumns.HEIGHT)));
                if (TextUtils.isEmpty(mMedia.getPath()) || TextUtils.isEmpty(mMedia.getName()) || "null".equals(mMedia.getName()) || mMedia.getMIME().equals(Media.UNKNOWN_MIME) || !new File(mMedia.getPath()).exists()) {
                    continue;
                }
                if (collectsIds.contains(mMedia.getId())) {
                    mMedia.setCollected(true);
                }
                mAlbum.getMedias().add(mMedia);
            } while (mPhotosCursor.moveToNext());
            mPhotosCursor.close();
            mPhotosCursor = null;
        }
        mAlbum.setCount(mAlbum.getMedias().size());
        if (mAlbum.getAlbumCover() != null && !TextUtils.isEmpty(mAlbum.getAlbumCover().getPath())) {
            mAlbum.setStorageRootPath(mAlbum.getAlbumCover().getPath().substring(0, mAlbum.getAlbumCover().getPath().lastIndexOf("/") + 1));
        }
        //        Logger.i(mAlbum.getAlbumCover().getPath().substring(0,mAlbum.getAlbumCover().getPath().lastIndexOf("/")));
        return mAlbum;
    }

    public interface PhotosLoadHandler {
        public void getPhotosSuc(List<Album> albums);

        public void getPhotosFail();
    }

    private final class LoadPhotosTask extends AsyncTask<Void, Void, List<Album>> {
        private PhotosLoadHandler mPhotosLoadHandler;

        public LoadPhotosTask(PhotosLoadHandler mPhotosLoadHandler) {
            this.mPhotosLoadHandler = mPhotosLoadHandler;
        }

        @Override
        protected List<Album> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return load();
        }

        @Override
        protected void onPostExecute(List<Album> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (result != null && !result.isEmpty()) {
                if (mPhotosLoadHandler != null)
                    mPhotosLoadHandler.getPhotosSuc(result);
            } else {
                if (mPhotosLoadHandler != null)
                    mPhotosLoadHandler.getPhotosFail();
            }
        }

    }
}
