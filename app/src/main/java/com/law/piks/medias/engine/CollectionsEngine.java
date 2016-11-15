package com.law.piks.medias.engine;

import com.law.piks.medias.entity.Album;
import com.law.piks.medias.entity.Media;
import com.law.think.frame.utils.Logger;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Law on 2016/10/18.
 */

public class CollectionsEngine {

    public static Album getCollects() {
        Album album = new Album();
        List<Media> collects = Realm.getDefaultInstance().where(Media.class).findAll();
        //        List<Media> medias = Realm.
        if (collects == null || collects.size() == 0) {
            return null;
        }
        album.setName("Collects");
        album.setMedias(collects);
        Logger.i("collects.size() = " + collects.size());
        album.setCount(collects.size());
        for (Media media : collects) {
            Logger.i(media);
        }
        return album;
    }

    public static List<Long> getCollectsIds() {
        List<Long> ids = new ArrayList<>();
        List<Media> collects = Realm.getDefaultInstance().where(Media.class).findAll();
        for (Media media : collects) {
            ids.add(media.getId());
            Logger.i(media.getId());
        }
        Logger.i("collects.size() = " + collects.size());
        return ids;
    }

    public static void addCollect(final Media media) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(media);
            }
        });
    }

    public static void removeCollect(final Media media) {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Media.class).equalTo("id", media.getId()).findFirst().deleteFromRealm();
            }
        });
    }
}
