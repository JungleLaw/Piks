package com.law.piks.medias.engine;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.law.piks.medias.entity.Album;
import com.law.piks.medias.entity.Media;
import com.law.piks.medias.utils.ContentUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Law on 2016/9/9.
 */
public class MediasLoadEngine {
    private static MediasLoadEngine instance = new MediasLoadEngine();

    private File root;

    private ArrayList<Media> mAllMedias;
    private List<Album> mAlbums;

    private MediasLoadEngine() {
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android");
    }

    public static MediasLoadEngine getInstance() {
        return instance;
    }

    public void loadPreviewAlbums(Context context) {
        mAllMedias = new ArrayList<>();
        HashSet<File> roots = listStorages(context);
        ArrayList<Album> albumArrayList = new ArrayList<>();
        for (File storage : roots)
            fetchRecursivelyFolder(storage, albumArrayList, storage.getAbsolutePath());
        Album album = new Album();
        album.setStorageRootPath(root.getAbsolutePath());
        Collections.sort(mAllMedias, new MediaComparators(false).getDateComparator());
        album.setMedias(mAllMedias);
        album.setPath(root.getAbsolutePath());
        album.setCount(mAllMedias.size());
        album.setName("All");
        albumArrayList.add(0, album);
        this.mAlbums = albumArrayList;
        sortAlbumsBySize();
    }

    public void sortAlbumsBySize() {
        AlbumsComparators albumsComparators = new AlbumsComparators(false);
        Collections.sort(mAlbums, albumsComparators.getSizeComparator());
    }

    private HashSet<File> listStorages(Context context) {
        HashSet<File> roots = new HashSet<>();
        roots.add(Environment.getExternalStorageDirectory());

        String[] extSdCardPaths = ContentUtils.getExtSdCardPaths(context);
        for (String extSdCardPath : extSdCardPaths) {
            File mas = new File(extSdCardPath);
            if (mas.canRead())
                roots.add(mas);
        }

        String sdCard = System.getenv("SECONDARY_STORAGE");
        if (sdCard != null)
            roots.add(new File(sdCard));
        return roots;
    }

    private void fetchRecursivelyFolder(File dir, ArrayList<Album> albumArrayList, String rootExternalStorage) {
        checkAndAddAlbum(dir, albumArrayList, rootExternalStorage);
        File[] children = dir.listFiles(new FoldersFileFilter());
        if (children != null) {
            for (File temp : children) {
                File nomedia = new File(temp, ".nomedia");
                if (!temp.isHidden() && !nomedia.exists()) {
                    // not excluded/hidden folder
                    fetchRecursivelyFolder(temp, albumArrayList, rootExternalStorage);
                }
            }
        }
    }

    private void checkAndAddAlbum(File temp, ArrayList<Album> albumArrayList, String rootExternalStorage) {
        File[] files = temp.listFiles(new ImageFileFilter(ImageFileFilter.FILTER_NO_VIDEO, false));
        if (files != null && files.length > 0) {
            // valid folder
            // Album mAlbum = new Album(temp.getAbsolutePath(), temp.getName(), files.length, rootExternalStorage);
            Album mAlbum = new Album();
            mAlbum.setPath(temp.getAbsolutePath());
            mAlbum.setName(temp.getName());
            mAlbum.setStorageRootPath(rootExternalStorage);
            ArrayList<Media> medias = new ArrayList<>();

            long lastMod = Long.MIN_VALUE;
            File choice = null;
            for (File file : files) {
                if (file.lastModified() > lastMod) {
                    choice = file;
                    lastMod = file.lastModified();
                }
                Media media = new Media(file);
                if (TextUtils.isEmpty(media.getMIME()) || media.getMIME().equals(Media.UNKNOWN_MIME))
                    continue;
                medias.add(0, media);
                mAllMedias.add(0, media);
            }
            MediaComparators comparators = new MediaComparators(false);
            Collections.sort(medias, comparators.getDateComparator());
            if (medias.size() == 0)
                return;
            mAlbum.setMedias(medias);
            mAlbum.setCount(medias.size());
            //            if (choice != null)
            //                mAlbum.setAlbumCover(new Media(choice));
            //            if (choice != null)
            //                mAlbum.setAlbumCover(new Media(choice.getAbsolutePath(), choice.lastModified()));
            albumArrayList.add(mAlbum);
        }
    }

    public List<Album> getAlbums() {
        return mAlbums;
    }

    public void setAlbums(List<Album> mAlbums) {
        this.mAlbums = mAlbums;
    }

    public void loadFromDb(Context context) {

    }
}
