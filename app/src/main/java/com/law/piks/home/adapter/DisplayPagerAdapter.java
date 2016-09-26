package com.law.piks.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.law.piks.home.adapter.fragment.GifImageFragment;
import com.law.piks.home.adapter.fragment.ImageFragment;
import com.law.piks.home.adapter.fragment.LargeImageFragment;
import com.law.piks.medias.entity.Media;
import com.law.think.frame.utils.ScreenUtils;

import java.util.List;

/**
 * Created by Law on 2016/9/25.
 */

public class DisplayPagerAdapter extends FragmentStatePagerAdapter {
    private List<Media> medias;
    private SparseArray<Fragment> fragments = new SparseArray<>();

    public DisplayPagerAdapter(FragmentManager fm, List<Media> medias) {
        super(fm);
        this.medias = medias;
    }

    @Override
    public Fragment getItem(int position) {
        Media media = medias.get(position);
        if (media.isGif()) {
            return GifImageFragment.newInstance(media);
        } else if (media.getHeight() / media.getWidth() > 2 && media.getHeight() > 2000) {
            return LargeImageFragment.newInstance(media);
        } else {
            return ImageFragment.newInstance(media);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return medias != null ? medias.size() : 0;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        fragments.remove(position);
        super.destroyItem(container, position, object);
    }
}
