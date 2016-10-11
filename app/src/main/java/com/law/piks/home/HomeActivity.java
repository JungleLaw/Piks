package com.law.piks.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseActivity;
import com.law.piks.home.adapter.AlbumAdapter;
import com.law.piks.home.adapter.MediaAdapter;
import com.law.piks.home.share.ShareSheetView;
import com.law.piks.medias.Configuration;
import com.law.piks.medias.engine.MediasLoader;
import com.law.piks.medias.entity.Album;
import com.law.piks.medias.entity.Media;
import com.law.piks.other.AboutActivity;
import com.law.piks.widget.peekandpop.PeekAndPop;
import com.law.piks.widget.slidemenu.SlideMenu;
import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.ScreenUtils;
import com.law.think.frame.view.GridViewWithHeaderAndFooter;
import com.law.think.frame.widget.ThinkToast;
import com.law.think.frame.widget.TitleBar;
import com.law.think.frame.widget.bottomsheet.BottomSheetLayout;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Law on 2016/9/7.
 */
public class HomeActivity extends AppBaseActivity {

    @ViewInject(R.id.slideMenu)
    private SlideMenu mSlideMenu;
    @ViewInject(R.id.bottomsheet_layout)
    private BottomSheetLayout mBottomSheetLayout;

    private TitleBar mTitleBar;
    private TextView mAlbumName;
    private TextView mAlbumSize;
    private ImageView mAlbumFolder;


    private ImageView mAlbumCover;
    private boolean isExit;
    private View mMediasCoverHeaderView;
    private GridViewWithHeaderAndFooter mGridViewWithHeaderAndFooter;
    private ListView mAlbumsListView;
    private PeekAndPop mPeekAndPop;
    private ShareSheetView shareSheetView;

    private int headerHeight;
    private List<Album> mAlbums;
    private ArrayList<Media> mMedias;
    private AlbumAdapter mAlbumAdapter;
    private MediaAdapter mMediaAdapter;


    @Override
    public int setContentViewLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void initVariables() {

    }

    @Override
    public void initViews() {
        getLayoutInflater().inflate(R.layout.layout_primary_menu, mSlideMenu, true);
        getLayoutInflater().inflate(R.layout.layout_secondary_menu, mSlideMenu, true);
        getLayoutInflater().inflate(R.layout.layout_content, mSlideMenu, true);
        mTitleBar = (TitleBar) findViewById(R.id.titlebar);
        mAlbumsListView = (ListView) findViewById(R.id.lv_albums);
        mGridViewWithHeaderAndFooter = (GridViewWithHeaderAndFooter) findViewById(R.id.gridview_medias);

        int width = ScreenUtils.getScreenWidth(this);
        headerHeight = width * 2 / 3;
        mMediasCoverHeaderView = getLayoutInflater().inflate(R.layout.layout_medias_cover_header, null, false);
        mMediasCoverHeaderView.setLayoutParams(new ViewGroup.LayoutParams(width, headerHeight));
        mAlbumFolder = (ImageView) mMediasCoverHeaderView.findViewById(R.id.img_album_folder);
        mAlbumCover = (ImageView) mMediasCoverHeaderView.findViewById(R.id.img_medias_cover);
        mAlbumName = (TextView) mMediasCoverHeaderView.findViewById(R.id.text_album_name);
        mAlbumSize = (TextView) mMediasCoverHeaderView.findViewById(R.id.text_album_size);

        mPeekAndPop = new PeekAndPop.Builder(this).blurBackground(true).peekLayout(R.layout.layout_home_peek_and_pop).parentViewGroupToDisallowTouchEvents(mGridViewWithHeaderAndFooter).build();

    }

    @Override
    public void initListener() {
        mTitleBar.setOnLeftViewClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mSlideMenu.isOpen()) {
                    mSlideMenu.close(true);
                } else {
                    mSlideMenu.open(false, true);
                }
            }
        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Logger.i("titlebar right onclick");
            }
        });
        mAlbumsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mAlbumAdapter.getSelectIndex() != position) {
                    mAlbumAdapter.setSelectIndex(position);
                    mMedias.clear();
                    mMedias.addAll(mAlbums.get(position).getMedias());
                    mMediaAdapter = new MediaAdapter(HomeActivity.this, mMedias, mPeekAndPop);
                    mMediaAdapter.setOnClickCallback(new MediaAdapter.OnClickCallback() {
                        @Override
                        public void OnClickCallback(int position) {
                            GalleryActivity.navigateToGallery(HomeActivity.this, mMedias, position);
                        }
                    });
                    mGridViewWithHeaderAndFooter.setAdapter(mMediaAdapter);

                    updateHeaderView(position);
                }
                mSlideMenu.close(true);
            }
        });
        mGridViewWithHeaderAndFooter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                GalleryActivity.navigateToGallery(HomeActivity.this, mMedias, position);
            }
        });
        findViewById(R.id.btn_about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AboutActivity.class));
            }
        });
        findViewById(R.id.btn_recommened).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePiks();
            }
        });
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        mAlbumAdapter = new AlbumAdapter(this, mAlbums);
        mAlbumsListView.setAdapter(mAlbumAdapter);
        mTitleBar.setTitleViewText("");

        mMediaAdapter = new MediaAdapter(this, mMedias, mPeekAndPop);
        mMediaAdapter.setOnClickCallback(new MediaAdapter.OnClickCallback() {
            @Override
            public void OnClickCallback(int position) {
                GalleryActivity.navigateToGallery(HomeActivity.this, mMedias, position);
            }
        });
        mGridViewWithHeaderAndFooter.addHeaderView(mMediasCoverHeaderView);
        mGridViewWithHeaderAndFooter.setAdapter(mMediaAdapter);


        mGridViewWithHeaderAndFooter.setVerticalSpacing(Configuration.GalleryConstants.divider);
        mGridViewWithHeaderAndFooter.setHorizontalSpacing(Configuration.GalleryConstants.divider);
        mGridViewWithHeaderAndFooter.setNumColumns(Configuration.GalleryConstants.numColumns);

        loadMedia(0);
    }

    @Override
    public void destroyTask() {

    }

    @Override
    public int[] loadExitAnimation() {
        return null;
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        if (mSlideMenu.isOpen()) {
            mSlideMenu.close(true);
        } else {
            exit();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImageLoader.clear(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        if (requestCode == GalleryActivity.GALLERY_REQUEST_CODE) {
            Logger.i("onActivityResult");
            loadMedia(mAlbumAdapter.getSelectIndex());
        }

    }

    private void loadMedia(final int albumIndex) {
        Logger.i("loadMedia " + albumIndex);
        showProgress();
        MediasLoader.getInstance().loadPhotos(this, new MediasLoader.PhotosLoadHandler() {
            @Override
            public void getPhotosSuc(List<Album> albums) {
                mAlbums = new ArrayList<>();
                mAlbums.addAll(albums);
                dismissProgress();
                mAlbumAdapter.refresh(mAlbums);
                updateHeaderView(albumIndex);
                mMedias = new ArrayList<>();
                mMedias.addAll(mAlbums.get(albumIndex).getMedias());
                mMediaAdapter.refresh(mMedias);
            }

            @Override
            public void getPhotosFail() {

            }
        });
    }

    public void updateHeaderView(int position) {
        Album album = mAlbums.get(position);
        mAlbumSize.setText(getString(R.string.album_size, String.valueOf(album.getCount())));
        mAlbumName.setText(album.getName());
        if (album.getName().toLowerCase().equals("all")) {
            mAlbumFolder.setImageResource(R.drawable.ic_cover_all);
        } else if (album.getName().toLowerCase().equals("camera")) {
            mAlbumFolder.setImageResource(R.drawable.ic_cover_camera);
        } else if (album.getName().toLowerCase().equals("screenshots")) {
            mAlbumFolder.setImageResource(R.drawable.ic_cover_screenshot);
        } else if (album.getName().toLowerCase().equals("download")) {
            mAlbumFolder.setImageResource(R.drawable.ic_cover_download);
        } else {
            mAlbumFolder.setImageResource(R.drawable.ic_cover_album);
        }
        if (album.getAlbumCover() != null)
            ImageLoader.with(this).signature(album.getAlbumCover().signature()).centerCrop().load(album.getAlbumCover().getPath()).into(mAlbumCover);
    }

    private void exit() {
        Timer mTimer = null;
        if (isExit == false) {
            isExit = true;
            ThinkToast.showToast(this, "再次点击退出程序", ThinkToast.LENGTH_SHORT, ThinkToast.INFO);
            mTimer = new Timer();

            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }

    private void sharePiks() {
        if (shareSheetView == null) {
            mBottomSheetLayout.setPeekOnDismiss(true);
            shareSheetView = new ShareSheetView(this, "分享", new ShareSheetView.OnShareItemClickListener() {
                @Override
                public void onShareItemClick(ShareSheetView.ShareItem item) {
                    if (mBottomSheetLayout.isSheetShowing()) {
                        mBottomSheetLayout.dismissSheet();
                    }
                    switch (item.getItemShareType()) {
                        case ShareSheetView.ShareType.WECHAT:
                            //                            shareToWx(SendMessageToWX.Req.WXSceneSession);
                            Logger.i("WECHAT");
                            break;
                        case ShareSheetView.ShareType.MOMENTS:
                            //                            shareToWx(SendMessageToWX.Req.WXSceneTimeline);
                            Logger.i("MOMENTS");
                            break;
                        case ShareSheetView.ShareType.WXFAVORITE:
                            //                            shareToWx(SendMessageToWX.Req.WXSceneFavorite);
                            Logger.i("WXFAVORITE");
                            break;
                        case ShareSheetView.ShareType.WEIBO:
                            Logger.i("WEIBO");
                            break;
                        case ShareSheetView.ShareType.EMAIL:
                            Logger.i("EMAIL");
                            break;
                        case ShareSheetView.ShareType.SMS:
                            Logger.i("SMS");
                            break;
                        default:
                    }
                }
            });
        }
        mBottomSheetLayout.showWithSheetView(shareSheetView);
    }
}
