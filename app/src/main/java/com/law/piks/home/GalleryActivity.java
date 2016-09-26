package com.law.piks.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseFragmentActivity;
import com.law.piks.home.adapter.DisplayPagerAdapter;
import com.law.piks.medias.entity.Media;
import com.law.piks.view.HackyViewPager;
import com.law.piks.widget.bottomsheet.MenuSheetView;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.inject.annotation.event.OnClick;
import com.law.think.frame.utils.ConstUtils;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.PixelUtils;
import com.law.think.frame.utils.SDKUtils;
import com.law.think.frame.utils.TimeUtils;
import com.law.think.frame.widget.TitleBar;
import com.law.think.frame.widget.bottomsheet.BottomSheetLayout;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Law on 2016/9/23.
 */

public class GalleryActivity extends AppBaseFragmentActivity implements ViewPager.OnPageChangeListener {
    private static final String MEDIAS = "medias";
    private static final String INDEX = "index";

    private static AlphaAnimation entryAnimation = new AlphaAnimation(0, 1);
    private static AlphaAnimation exitAnimation = new AlphaAnimation(1, 0);

    @ViewInject(R.id.img_display_parent)
    private View mDisplayParent;
    @ViewInject(R.id.text_modified_date)
    private TextView mTextModifiedDate;
    @ViewInject(R.id.img_collect)
    private ImageView mCollectImg;
    @ViewInject(R.id.img_delete)
    private ImageView mDelImg;
    @ViewInject(R.id.img_share)
    private ImageView mShareImg;
    @ViewInject(R.id.img_info)
    private ImageView mInfoImg;
    @ViewInject(R.id.viewpager_display)
    private HackyViewPager mDisplayViewPager;
    @ViewInject(R.id.bottomsheet_layout)
    private BottomSheetLayout mBottomSheetLayout;
    @ViewInject(R.id.check_photo_titlebar)
    private TitleBar checkPhotoTitlebar;
    @ViewInject(R.id.check_photo_toolsbar)
    private View checkPhotoToolsbar;

    private ImageButton mDialogCloseImgBtn;
    private TextView mDialogTitleText;
    private TextView mDialogContentText;
    private MenuSheetView menuSheetView;

    private AlertDialog mMediaInfoDialog;

    private DisplayPagerAdapter mDisplayPagerAdapter;
    private boolean fullscreenmode = false;
    private boolean isExitAnimationRunning = false;

    private List<Media> mMedias;
    private int index;

    public static int getStatusBarHeight(Resources r) {
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return r.getDimensionPixelSize(resourceId);

        return 0;
    }

    public static void navigateToGallery(Context context, ArrayList<Media> medias, int index) {
        Intent mIntent = new Intent(context, GalleryActivity.class);
        mIntent.putExtra(MEDIAS, medias);
        mIntent.putExtra(INDEX, index);
        context.startActivity(mIntent);
    }

    @Override
    public int setContentViewLayout() {
        return R.layout.activity_gallery;
    }

    @Override
    public void initVariables() {
        mMedias = (ArrayList<Media>) getIntent().getSerializableExtra(MEDIAS);
        index = getIntent().getIntExtra(INDEX, 0);
    }

    @Override
    public void initViews() {
        if (SDKUtils.hasLollopop()) {
            getWindow().setStatusBarColor(Color.BLACK);
            getWindow().setNavigationBarColor(Color.BLACK);
        }
    }

    @Override
    public void initListener() {
        checkPhotoTitlebar.setOnLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //                mBackgroundDisplay.startAnimation(exitAnimation);
                //                mDisplayParent.startAnimation(exitAnimation);
                finish();
            }
        });
        mDisplayViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        checkPhotoTitlebar.setTitleViewText("");
        //        mDisplayAdapter = new DisplayPagerAdapter(mMedias);
        //        mDisplayAdapter.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
        //            @Override
        //            public void onViewTap(View view, float x, float y) {
        //                Logger.i("PhotoView onClicked!");
        //                toggleUI();
        //            }
        //        });
        //        mDisplayAdapter.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View v) {
        //                toggleUI();
        //            }
        //        });
        //        mDisplayViewPager.setAdapter(mDisplayAdapter);
        //        //        mDisplayViewPager.setPageTransformer(true, new CardSlideTransformer());

        mDisplayPagerAdapter = new DisplayPagerAdapter(getSupportFragmentManager(), mMedias);
        mDisplayViewPager.setAdapter(mDisplayPagerAdapter);

        mDisplayViewPager.setPageMargin(10 * (int) PixelUtils.getDensity(this));
        mDisplayViewPager.setOffscreenPageLimit(2);
        checkPhotoTitlebar.setTitleViewText(getString(R.string.media_title, index + 1, mMedias.size()));
        mTextModifiedDate.setText(TimeUtils.milliseconds2String(mMedias.get(index).getModifiedDate() * ConstUtils.SEC, new SimpleDateFormat("yyyy年MM月dd日 a HH:mm")));
        mDisplayViewPager.setCurrentItem(index, false);

    }

    @Override
    public void destroyTask() {
        //        mDisplayAdapter = null;
        mMedias.clear();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        checkPhotoTitlebar.setTitleViewText(getString(R.string.media_title, position + 1, mMedias.size()));
        mTextModifiedDate.setText(TimeUtils.milliseconds2String(mMedias.get(position).getModifiedDate() * ConstUtils.SEC, new SimpleDateFormat("yyyy年MM月dd日 a HH:mm")));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @OnClick({R.id.img_collect, R.id.img_delete, R.id.img_share, R.id.img_info})
    private void showInfo(View view) {
        switch (view.getId()) {
            case R.id.img_collect:
                Logger.i("collect");
                if (!mCollectImg.isSelected()) {
                    mCollectImg.setSelected(true);
                } else {
                    mCollectImg.setSelected(false);
                }
                break;
            case R.id.img_delete:
                Logger.i("delete");
                break;
            case R.id.img_share:
                Logger.i("share");
                if (menuSheetView == null) {
                    mBottomSheetLayout.setPeekOnDismiss(true);
                    menuSheetView = new MenuSheetView(GalleryActivity.this, MenuSheetView.MenuType.LIST, getString(R.string.share), new MenuSheetView.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (mBottomSheetLayout.isSheetShowing()) {
                                mBottomSheetLayout.dismissSheet();
                            }
                            return true;
                        }
                    });
                    menuSheetView.inflateMenu(R.menu.share_menu);
                }
                mBottomSheetLayout.showWithSheetView(menuSheetView);
                break;
            case R.id.img_info:
                Logger.i("info");
                showMediaInfo(mDisplayViewPager.getCurrentItem());
                break;
        }
    }

    private void initAnimation() {
        // TODO Auto-generated method stub
        entryAnimation.setDuration(300);
        entryAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
        exitAnimation.setDuration(300);
        exitAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                isExitAnimationRunning = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                // mBackgroundDisplay.setVisibility(View.INVISIBLE);
                mDisplayParent.setVisibility(View.GONE);
                if (fullscreenmode)
                    showSystemUI();
                isExitAnimationRunning = false;
            }
        });
    }

    private void showMediaInfo(int position) {
        Media media = mMedias.get(position);
        if (mMediaInfoDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.layout_media_info_dialog, null);
            mMediaInfoDialog = new AlertDialog.Builder(this).setView(view).create();
            mMediaInfoDialog.setCanceledOnTouchOutside(true);
            mDialogCloseImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_close);
            mDialogTitleText = (TextView) view.findViewById(R.id.text_title);
            mDialogContentText = (TextView) view.findViewById(R.id.text_media_info);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.media_name, media.getName())).append("\n");
        sb.append(getString(R.string.media_time, TimeUtils.milliseconds2String(media.getModifiedDate() * ConstUtils.SEC, new SimpleDateFormat("yyyy年MM月dd日 a HH:mm")))).append("\n");
        sb.append(getString(R.string.media_width, media.getWidth())).append("\n");
        sb.append(getString(R.string.media_height, media.getHeight())).append("\n");
        sb.append(getString(R.string.media_orientation, media.getOrientation() == -1 ? 0 : media.getOrientation())).append("\n");
        String style = "0.00";
        //在构造函数中设置数字格式
        DecimalFormat df = new DecimalFormat(style);
        sb.append(getString(R.string.media_size, df.format((float) media.getSize() / ConstUtils.MB))).append("\n");
        sb.append(getString(R.string.media_path, media.getPath()));


        mDialogTitleText.setText(getString(R.string.media_title, position + 1, mMedias.size()));
        mDialogContentText.setText(sb.toString());
        mDialogCloseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaInfoDialog.dismiss();
            }
        });
        mMediaInfoDialog.show();
    }

    public void toggleUI() {
        if (fullscreenmode)
            showSystemUI();
        else
            hideSystemUI();
    }

    private void hideSystemUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                checkPhotoTitlebar.animate().translationY(-checkPhotoTitlebar.getHeight() - getStatusBarHeight(getResources())).setInterpolator(new AccelerateInterpolator()).setDuration(200).start();
                checkPhotoToolsbar.animate().translationY(checkPhotoToolsbar.getHeight()).setInterpolator(new AccelerateInterpolator()).setDuration(200).start();
                //                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //                        //                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //                        //                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hidenavbar
                //                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide statusbar
                //                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                //                mDisplayParent.setSystemUiVisibility(uiOptions);
                fullscreenmode = true;
                //                changeBackGroundColor();
            }
        });
    }

    private void showSystemUI() {
        runOnUiThread(new Runnable() {
            public void run() {
                checkPhotoTitlebar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(240).start();
                checkPhotoToolsbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).setDuration(240).start();
                //                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                //                        //                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                //                        //                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                //                        //                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hidenavbar
                //                        //                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide statusbar
                //                        //                        | View.SYSTEM_UI_LAYOUT_FLAGS
                //                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                //                mDisplayParent.setSystemUiVisibility(uiOptions);
                fullscreenmode = false;
                //                changeBackGroundColor();
            }
        });
    }
}
