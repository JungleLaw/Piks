package com.law.piks.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.app.base.AppBaseFragmentActivity;
import com.law.piks.home.adapter.DisplayPagerAdapter;
import com.law.piks.medias.entity.Media;
import com.law.piks.medias.utils.ContentUtils;
import com.law.piks.view.HackyViewPager;
import com.law.piks.widget.bottomsheet.MenuSheetView;
import com.law.piks.widget.bottomsheet.MenuSheetView.OnMenuItemClickListener;
import com.law.piks.widget.popmenu.MenuItem;
import com.law.piks.widget.popmenu.PopMenu;
import com.law.think.frame.imageloader.ImageLoader;
import com.law.think.frame.inject.annotation.ViewInject;
import com.law.think.frame.inject.annotation.event.OnClick;
import com.law.think.frame.utils.ConstUtils;
import com.law.think.frame.utils.Logger;
import com.law.think.frame.utils.PixelUtils;
import com.law.think.frame.utils.SDKUtils;
import com.law.think.frame.utils.ScreenUtils;
import com.law.think.frame.utils.TimeUtils;
import com.law.think.frame.widget.ThinkToast;
import com.law.think.frame.widget.TitleBar;
import com.law.think.frame.widget.bottomsheet.BottomSheetLayout;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Law on 2016/9/23.
 */

public class GalleryActivity extends AppBaseFragmentActivity implements ViewPager.OnPageChangeListener {
    public static final int GALLERY_REQUEST_CODE = 0X0001;
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
    private ImageView mDeleteDialogImg;
    private TextView mDeleteDialogDetailText;
    private AlertDialog mDeleteDialog;
    private PopMenu mPopMenu;

    private DisplayPagerAdapter mDisplayPagerAdapter;
    private boolean fullscreenmode = false;
    private boolean isExitAnimationRunning = false;

    private List<Media> mMedias;
    private int index;
    private int direction = Direction.NO_DIRECTION;
    private int lastItemIndex = -1;
    private boolean modified = false;

    public static int getStatusBarHeight(Resources r) {
        int resourceId = r.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return r.getDimensionPixelSize(resourceId);

        return 0;
    }

    public static void navigateToGallery(Activity activity, ArrayList<Media> medias, int index) {
        Intent mIntent = new Intent(activity, GalleryActivity.class);
        mIntent.putExtra(MEDIAS, medias);
        mIntent.putExtra(INDEX, index);
        activity.startActivityForResult(mIntent, GALLERY_REQUEST_CODE);
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
        if (mPopMenu == null) {
            initPopMenu();
        }
    }

    @Override
    public void initListener() {
        checkPhotoTitlebar.setOnLeftViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        checkPhotoTitlebar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mPopMenu.isShowing())
                    mPopMenu.showAsDropDown(view, 0, 0);
//                mPopupMenu.setAnchorView(view);
//                mPopupMenu.show();
            }
        });
        mDisplayViewPager.addOnPageChangeListener(this);
//        mPopupMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//
//            }
//        });
        mPopMenu.setOnItemClick(new PopMenu.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                Logger.i("PopupMenuListView OnItemClick");
                switch (position) {
                    case 0:
                        Logger.i(position);
                        break;
                    case 1:
                        Logger.i(position);
                        break;
                    case 2:
                        Logger.i(position);
                        break;
                    default:
                }
                if (mPopMenu.isShowing())
                    mPopMenu.dismiss();
            }
        });
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
    public void finish() {
        if (modified) {
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (modified) {
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Logger.i("onPageSelected " + position);
        if (lastItemIndex == -1) {
            if (position == mMedias.size() - 1) {
                direction = Direction.LEFT;
            } else {
                direction = Direction.RIGHT;
            }
        } else {
            if (lastItemIndex < position) {
                if (position == mMedias.size() - 1) {
                    direction = Direction.LEFT;
                }
                direction = Direction.RIGHT;
            } else if (lastItemIndex > position) {
                if (position == 0) {
                    direction = Direction.RIGHT;
                }
                direction = Direction.LEFT;
            } else {
                direction = Direction.RIGHT;
            }
        }
        lastItemIndex = position;
        checkPhotoTitlebar.setTitleViewText(getString(R.string.media_title, position + 1, mMedias.size()));
        mTextModifiedDate.setText(TimeUtils.milliseconds2String(mMedias.get(position).getModifiedDate() * ConstUtils.SEC, new SimpleDateFormat("yyyy年MM月dd日 a HH:mm")));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @OnClick({R.id.img_collect, R.id.img_delete, R.id.img_share, R.id.img_info})
    private void showInfo(View view) {
        int index = mDisplayViewPager.getCurrentItem();
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
//                deleteMedia(index);
                showDeleteMediaDialog(index);
                break;
            case R.id.img_share:
                shareMedia(index);

                break;
            case R.id.img_info:
                Logger.i("info");
                showMediaInfo(index);
                break;
        }
    }

    private void initPopMenu() {
        mPopMenu = new PopMenu(this);
        List<MenuItem> items = new ArrayList<>();
//            String[]{"编辑", "移动到", "重命名"};
        items.add(new MenuItem("编辑"));
        items.add(new MenuItem("移动到"));
        items.add(new MenuItem("重命名"));
        mPopMenu.setMenu(items);
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

    private void showDeleteMediaDialog(final int index) {
        if (mDeleteDialog == null) {
            final View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_media, null);
            mDeleteDialog = new AlertDialog.Builder(this).setView(dialogView).create();
            mDeleteDialog.setCanceledOnTouchOutside(true);
            mDeleteDialogImg = (ImageView) dialogView.findViewById(R.id.dialog_del_img_media);
            int width = (int) (ScreenUtils.getScreenWidth(this) * 0.85);
            dialogView.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
            mDeleteDialogImg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, width * 9 / 16));
            mDeleteDialogDetailText = (TextView) dialogView.findViewById(R.id.dialog_text_media_detail);
            dialogView.findViewById(R.id.btn_del).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    deleteMedia(index);
                    modified = true;
                    switch (direction) {
                        case Direction.LEFT:
                            Logger.i("Direction.LEFT");
                            break;
                        case Direction.RIGHT:
                            Logger.i("Direction.RIGHT");
                            break;
                        default:
                            Logger.i("Direction.NO_DIRECTION");
                    }
                    deleteMedia(mDisplayViewPager.getCurrentItem(), direction);
                    if (mDeleteDialog.isShowing())
                        mDeleteDialog.dismiss();
                }
            });
            dialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mDeleteDialog.isShowing())
                        mDeleteDialog.dismiss();
                }
            });
        }
        if (!mDeleteDialog.isShowing()) {
            mDeleteDialog.show();
        }
        ImageLoader.with(this).centerCrop().signature(mMedias.get(index).signature()).load(mMedias.get(index).getPath()).placeholder(R.color.black).into(mDeleteDialogImg);
//        mDeleteDialogImg.setImageURI(Uri.fromFile(new File(mMedias.get(index).getPath())));
        mDeleteDialogDetailText.setText(getMediaDetail(mMedias.get(index)));
    }

    private boolean deleteMedia(int index, int viewPagerDirection) {
        Logger.i("del current " + index);
        boolean success;
        Media media = mMedias.get(index);
        File file = new File(media.getPath());
        if (success = ContentUtils.deleteFile(this, file)) {
            scanFile(this, new String[]{file.getAbsolutePath()});
        }
        switch (viewPagerDirection) {
            case Direction.LEFT:
                Logger.i("Direction.LEFT");
                break;
            case Direction.RIGHT:
                Logger.i("Direction.RIGHT");
                break;
            default:
                Logger.i("Direction.NO_DIRECTION");
        }
        mMedias.remove(index);
        mDisplayPagerAdapter.notifyDataSetChanged();
        checkPhotoTitlebar.setTitleViewText(getString(R.string.media_title, mDisplayViewPager.getCurrentItem() + 1, mMedias.size()));
        mTextModifiedDate.setText(TimeUtils.milliseconds2String(mMedias.get(mDisplayViewPager.getCurrentItem()).getModifiedDate() * ConstUtils.SEC, new SimpleDateFormat("yyyy年MM月dd日 a HH:mm")));
        return success;
    }

    private void shareMedia(int index) {
//        Media media = mMedias.get(index);
//        Intent share = new Intent(Intent.ACTION_SEND);
//        share.setType(media.getMimeType());
//        share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(media.getPath())));
//        startActivity(Intent.createChooser(share, "分享"));
        if (menuSheetView == null) {
            mBottomSheetLayout.setPeekOnDismiss(true);
            menuSheetView = new MenuSheetView(GalleryActivity.this, MenuSheetView.MenuType.LIST, getString(R.string.share), new OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(android.view.MenuItem item) {
                    if (mBottomSheetLayout.isSheetShowing()) {
                        mBottomSheetLayout.dismissSheet();
                    }
                    switch (item.getItemId()) {
                        case R.id.menu_wechat_item:
                            ThinkToast.showToast(GalleryActivity.this, "Share Wechat", ThinkToast.LENGTH_SHORT, ThinkToast.SUCCESS);
                            break;
                        case R.id.menu_weibo_item:
                            ThinkToast.showToast(GalleryActivity.this, "Share Weibo", ThinkToast.LENGTH_SHORT, ThinkToast.SUCCESS);
                            break;
                        case R.id.menu_moments_item:
                            ThinkToast.showToast(GalleryActivity.this, "Share Moments", ThinkToast.LENGTH_SHORT, ThinkToast.SUCCESS);
                            break;
                        default:
                    }
                    return true;
                }

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
    }

    private void showMediaInfo(int position) {
        Media media = mMedias.get(position);
        if (mMediaInfoDialog == null) {
            View view = getLayoutInflater().inflate(R.layout.dialog_media_info, null);
            mMediaInfoDialog = new AlertDialog.Builder(this).setView(view).create();
            mMediaInfoDialog.setCanceledOnTouchOutside(true);
            mDialogCloseImgBtn = (ImageButton) view.findViewById(R.id.imgBtn_close);
            mDialogTitleText = (TextView) view.findViewById(R.id.text_title);
            mDialogContentText = (TextView) view.findViewById(R.id.text_media_info);
        }

        mDialogTitleText.setText(getString(R.string.media_title, position + 1, mMedias.size()));
        mDialogContentText.setText(getMediaDetail(media));
        mDialogCloseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMediaInfoDialog.dismiss();
            }
        });
        mMediaInfoDialog.show();
    }

    private String getMediaDetail(Media media) {
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
        return sb.toString();
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

    public void scanFile(Context context, String[] path) {
        MediaScannerConnection.scanFile(context, path, null, null);
    }

    public void scanFile(Context context, String[] path, MediaScannerConnection.OnScanCompletedListener onScanCompletedListener) {
        MediaScannerConnection.scanFile(context, path, null, onScanCompletedListener);
    }

    private class Direction {
        public static final int LEFT = -1;
        public static final int RIGHT = 1;
        public static final int NO_DIRECTION = 0;
    }
}
