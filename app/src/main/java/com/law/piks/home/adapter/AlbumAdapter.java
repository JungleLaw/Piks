package com.law.piks.home.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.medias.Configuration;
import com.law.piks.medias.entity.Album;
import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.utils.EncryptUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Law on 2016/9/9.
 */
public class AlbumAdapter extends BaseAdapter {
    private Context mContext;
    private List<Album> albums;
    private int selectIndex = 0;

    public AlbumAdapter(Context context, List<Album> albumList) {
        this.mContext = context;
        this.albums = albumList;
    }

    @Override
    public int getCount() {
        return albums == null ? 0 : albums.size();
    }

    @Override
    public Object getItem(int position) {
        return albums == null ? null : albums.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Album mAlbum = albums.get(position);
        ViewHolder mHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_album_item, null);
            mHolder = new ViewHolder(convertView);
            mHolder.mRootLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Configuration.GalleryConstants.albumHeight));
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        mHolder.mAlbumNameTv.setText(mAlbum.getName());
        mHolder.mAlbumCompatTv.setText(mContext.getString(R.string.album_size, String.valueOf(mAlbum.getCount())));
        if (position == selectIndex) {
            mHolder.mSelectedView.setVisibility(View.VISIBLE);
        } else {
            mHolder.mSelectedView.setVisibility(View.INVISIBLE);
        }
        if (mAlbum.getName().toLowerCase().equals("all")) {
            mHolder.mDisplayIconImage.setImageResource(R.drawable.ic_folder_all);
        } else if (mAlbum.getName().toLowerCase().equals("camera")) {
            mHolder.mDisplayIconImage.setImageResource(R.drawable.ic_folder_camera);
        } else if (mAlbum.getName().toLowerCase().equals("screenshots")) {
            mHolder.mDisplayIconImage.setImageResource(R.drawable.ic_folder_screenshot);
        } else if (mAlbum.getName().toLowerCase().equals("download")) {
            mHolder.mDisplayIconImage.setImageResource(R.drawable.ic_folder_downloads);
        } else {
            mHolder.mDisplayIconImage.setImageResource(R.drawable.ic_folder_album);
        }
        if (mAlbum.getAlbumCover() != null)
            ImageLoader.with(mContext).signature(mAlbum.getAlbumCover().signature()).centerCrop().load(mAlbum.getAlbumCover().getPath()).into(mHolder.mAlbumCoverImage);
        return convertView;
    }

    public void refresh(List<Album> albumList) {
        this.albums = albumList;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int index) {
        this.selectIndex = index;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        @ViewInject(R.id.layout_album_item_root)
        public LinearLayout mRootLayout;
        @ViewInject(R.id.img_album_cover)
        public ImageView mAlbumCoverImage;
        @ViewInject(R.id.img_flag)
        public ImageView mDisplayIconImage;
        @ViewInject(R.id.tv_album_name)
        public TextView mAlbumNameTv;
        @ViewInject(R.id.tv_album_compat)
        public TextView mAlbumCompatTv;
        @ViewInject(R.id.v_selected_flag)
        public View mSelectedView;

        public ViewHolder(View view) {
            ThinkInject.bind(this, view);
        }
    }
}
