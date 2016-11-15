package com.law.piks.browse;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.law.piks.R;
import com.law.piks.app.Constants;
import com.law.piks.app.base.AppBaseFragmentActivity;
import com.law.piks.browse.adapter.DisplayPagerAdapter;
import com.law.piks.browse.bottomsheet.MoveSheetView;
import com.law.piks.browse.bottomsheet.SettingSheetView;
import com.law.piks.browse.bottomsheet.ShareSheetView;
import com.law.piks.edit.FilterActivity;
import com.law.piks.medias.engine.CollectionsEngine;
import com.law.piks.medias.engine.MediasLoader;
import com.law.piks.medias.entity.Album;
import com.law.piks.medias.entity.Media;
import com.law.piks.medias.utils.ContentUtils;
import com.law.piks.view.HackyViewPager;
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
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Law on 2016/9/23.
 */

public class GalleryActivity extends AppBaseFragmentActivity implements ViewPager.OnPageChangeListener {
    public static final int GALLERY_REQUEST_CODE = 0X0001;
    public static final int UCROP_REQUEST_CODE = 0X0002;
    public static final int FILTER_REQUEST_CODE = 0X0003;

    public static final String RESULT_KEY = "result_key";
    public static final String RESULT_COLLECT = "collect";
    public static final String RESULT_MODIFY = "modify";

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
    private ShareSheetView mShareSheetView;
    private MoveSheetView mMoveSheetView;
    private SettingSheetView mSettingSheetView;
    private AlertDialog mMediaInfoDialog;
    private ImageView mDeleteDialogImg;
    private TextView mDeleteDialogDetailText;
    private AlertDialog mDeleteDialog;
    private AlertDialog mRenameDialog;
    private PopMenu mPopMenu;
    private PopMenu mGifMenu;

    private DisplayPagerAdapter mDisplayPagerAdapter;
    private boolean fullscreenmode = false;
    private boolean isExitAnimationRunning = false;

    private List<Media> mMedias;
    private int index;
    private int direction = Direction.NO_DIRECTION;
    private int lastItemIndex = -1;
    private boolean modified = false;
    //    private boolean collected = false;
    private IWXAPI api;
    private EditText mEditRename;
    private String cropFilePath;

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
        regToWx();
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
        mBottomSheetLayout.setPeekOnDismiss(true);
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
                if (mMedias.get(mDisplayViewPager.getCurrentItem()).isGif()) {
                    if (!mGifMenu.isShowing()) {
                        mGifMenu.showAsDropDown(view);
                    }
                } else {
                    if (!mPopMenu.isShowing()) {
                        mPopMenu.showAsDropDown(view);
                    }
                }
            }
        });
        mDisplayViewPager.addOnPageChangeListener(this);
        mPopMenu.setOnItemClick(new PopMenu.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                Logger.i(position + " clicked");
                switch (position) {
                    case 0:
                        //                        startActivity(new Intent(GalleryActivity.this, FilterActivity.class));
                        Media media = mMedias.get(mDisplayViewPager.getCurrentItem());
                        if (media.getOrientation() == 90 || media.getOrientation() == 270) {
                            FilterActivity.navigateToFilterActivity(GalleryActivity.this, media.getPath(), media.getHeight(), media.getWidth(), FILTER_REQUEST_CODE);
                        } else {
                            FilterActivity.navigateToFilterActivity(GalleryActivity.this, media.getPath(), media.getWidth(), media.getHeight(), FILTER_REQUEST_CODE);
                        }
                        break;
                    case 1:
                        startUCrop(GalleryActivity.this, mMedias.get(mDisplayViewPager.getCurrentItem()).getPath(), UCROP_REQUEST_CODE);
                        break;
                    case 2:
                        showMoveSheet();
                        break;
                    case 3:
                        showRenameDialog();
                        break;
                    case 4:
                        showSettingSheet();
                        break;
                    default:
                }
                if (mPopMenu.isShowing())
                    mPopMenu.dismiss();
            }
        });
        mGifMenu.setOnItemClick(new PopMenu.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0:
                        showMoveSheet();
                        break;
                    case 1:
                        showRenameDialog();
                        break;
                    default:
                }
                if (mGifMenu.isShowing())
                    mGifMenu.dismiss();
            }
        });
    }

    @Override
    public void loadData(@Nullable Bundle savedInstanceState) {
        checkPhotoTitlebar.setTitleViewText("");

        mDisplayPagerAdapter = new DisplayPagerAdapter(getSupportFragmentManager(), mMedias);
        mDisplayViewPager.setAdapter(mDisplayPagerAdapter);

        mDisplayViewPager.setPageMargin(10 * (int) PixelUtils.getDensity(this));
        mDisplayViewPager.setOffscreenPageLimit(2);
        checkPhotoTitlebar.setTitleViewText(getString(R.string.media_title, index + 1, mMedias.size()));
        mTextModifiedDate.setText(TimeUtils.milliseconds2String(mMedias.get(index).getModifiedDate() * ConstUtils.SEC, new SimpleDateFormat("yyyy年MM月dd日 a HH:mm")));
        if (mMedias.get(index).isCollected()) {
            mCollectImg.setSelected(true);
        } else {
            mCollectImg.setSelected(false);
        }
        mDisplayViewPager.setCurrentItem(index, false);
    }

    @Override
    public void destroyTask() {
        mMedias.clear();
        mMedias = null;
    }

    @Override
    public void finish() {
        if (modified) {
            //            if (collected) {
            //                setResult(RESULT_OK, new Intent().putExtra(RESULT_KEY, RESULT_COLLECT));
            //            }
            //            if (modified) {
            //                setResult(RESULT_OK, new Intent().putExtra(RESULT_KEY, RESULT_MODIFY));
            //            }
            setResult(RESULT_OK);
        }
        super.finish();
    }

    @Override
    public void onBackPressed() {
        if (modified) {
            //            if (collected) {
            //                setResult(RESULT_OK, new Intent().putExtra(RESULT_KEY, RESULT_COLLECT));
            //            }
            //            if (modified) {
            //                setResult(RESULT_OK, new Intent().putExtra(RESULT_KEY, RESULT_MODIFY));
            //            }
            setResult(RESULT_OK);
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case UCROP_REQUEST_CODE:
                modified = true;
                final Uri resultUri = UCrop.getOutput(data);
                //                String path = new File(resultUri.toString()).getAbsolutePath().substring(new File(resultUri.toString()).getAbsolutePath().indexOf(":"), new File(resultUri.toString()).getAbsolutePath().length());
                //                Logger.i("TAG", "resultUri.toString()).getAbsolutePath() = " + new File(resultUri.toString()).getAbsolutePath());
                //                Logger.i("TAG", "resultUri.toString()).getAbsoluteFile() = " + new File(resultUri.toString()).getAbsoluteFile());
                //                Logger.i("TAG", "resultUri.toString() = " + resultUri.toString());

                scanFile(GalleryActivity.this, new String[]{cropFilePath});
                //                scanFile(GalleryActivity.this, new String[]{path});
                cropFilePath = null;
                break;
            case FILTER_REQUEST_CODE:
                modified = true;
                break;
            default:
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
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
        Logger.i("Selected = " + mMedias.get(position).isCollected());
        if (mMedias.get(position).isCollected()) {
            mCollectImg.setSelected(true);
        } else {
            mCollectImg.setSelected(false);
        }
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
                //                if (!mCollectImg.isSelected()) {
                //                    mCollectImg.setSelected(true);
                //                } else {
                //                    mCollectImg.setSelected(false);
                //                }
                if (!mCollectImg.isSelected()) {
                    mMedias.get(index).setCollected(true);
                    CollectionsEngine.addCollect(mMedias.get(index));
                    mCollectImg.setSelected(true);
                    Logger.i("mMedias.get(" + index + ").isCollected() = " + mMedias.get(index).isCollected());
                } else {
                    mMedias.get(index).setCollected(false);
                    CollectionsEngine.removeCollect(mMedias.get(index));
                    mCollectImg.setSelected(false);
                    Logger.i("mMedias.get(" + index + ").isCollected() = " + mMedias.get(index).isCollected());
                }
                //                collected = true;
                modified = true;
                break;
            case R.id.img_delete:
                Logger.i("delete");
                //                deleteMedia(index);
                showDeleteMediaDialog(index);
                break;
            case R.id.img_share:
                shareMedia();
                break;
            case R.id.img_info:
                Logger.i("info");
                showMediaInfo(index);
                break;
        }
    }

    private void initPopMenu() {
        List<MenuItem> items = new ArrayList<>();
        items.add(new MenuItem("滤镜", R.drawable.icon_popmenu_filter));
        items.add(new MenuItem("裁剪", R.drawable.icon_popmenu_crop));
        items.add(new MenuItem("移动", R.drawable.icon_popmenu_move));
        items.add(new MenuItem("重命名", R.drawable.icon_popmenu_rename));
        items.add(new MenuItem("将照片设为", R.drawable.icon_popmenu_setting));
        mPopMenu = new PopMenu(this, items);
        //        mPopMenu.setMenu(items);

        List<MenuItem> gifItems = new ArrayList<>();
        gifItems.add(new MenuItem("移动", R.drawable.icon_popmenu_move));
        gifItems.add(new MenuItem("重命名", R.drawable.icon_popmenu_rename));
        mGifMenu = new PopMenu(this, gifItems);
        //        mGifMenu.setMenu(gifItems);
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
                    if (mDeleteDialog.isShowing())
                        mDeleteDialog.dismiss();
                    deleteMedia(mDisplayViewPager.getCurrentItem(), direction);
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
        if (mMedias.size() == 0) {
            finish();
        } else {
            mDisplayPagerAdapter.notifyDataSetChanged();
            checkPhotoTitlebar.setTitleViewText(getString(R.string.media_title, mDisplayViewPager.getCurrentItem() + 1, mMedias.size()));
            mTextModifiedDate.setText(TimeUtils.milliseconds2String(mMedias.get(mDisplayViewPager.getCurrentItem()).getModifiedDate() * ConstUtils.SEC, new SimpleDateFormat("yyyy年MM月dd日 a HH:mm")));
        }
        return success;
    }

    private void shareMedia() {
        if (mShareSheetView == null) {
            mShareSheetView = new ShareSheetView(this, "分享", new ShareSheetView.OnShareItemClickListener() {
                @Override
                public void onItemClick(ShareSheetView.ShareItem item) {
                    if (mBottomSheetLayout.isSheetShowing()) {
                        mBottomSheetLayout.dismissSheet();
                    }
                    switch (item.getItemShareType()) {
                        case ShareSheetView.ShareItem.ShareType.WECHAT:
                            shareToWx(SendMessageToWX.Req.WXSceneSession);
                            break;
                        case ShareSheetView.ShareItem.ShareType.MOMENTS:
                            shareToWx(SendMessageToWX.Req.WXSceneTimeline);
                            break;
                        case ShareSheetView.ShareItem.ShareType.WXFAVORITE:
                            shareToWx(SendMessageToWX.Req.WXSceneFavorite);
                            break;
                        case ShareSheetView.ShareItem.ShareType.WEIBO:
                            break;
                        case ShareSheetView.ShareItem.ShareType.EMAIL:
                            shareToMail();
                            break;
                        case ShareSheetView.ShareItem.ShareType.SMS:
                            shareToSMS();
                            break;
                        default:
                    }
                }

            });
        }
        mBottomSheetLayout.showWithSheetView(mShareSheetView);
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

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, Constants.SHARE.WX.APP_ID, true);
        api.registerApp(Constants.SHARE.WX.APP_ID);
    }

    private void shareToWx(int scene) {
        WXImageObject imageObject = new WXImageObject();
        imageObject.imagePath = mMedias.get(mDisplayViewPager.getCurrentItem()).getPath();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imageObject;
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        api.sendReq(req);
    }

    //发邮件
    private void shareToMail() {
        Media media = mMedias.get(mDisplayViewPager.getCurrentItem());
        Intent email = new Intent(android.content.Intent.ACTION_SEND);
        email.setType("image/*");
        String emailSubject = "共享软件";
        File file = new File(media.getPath());
        Uri outputFileUri = Uri.fromFile(file);
        email.putExtra(Intent.EXTRA_STREAM, outputFileUri);
        //设置邮件默认地址
        // email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
        //设置邮件默认标题
        email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
        //设置要默认发送的内容
        //        email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
        //调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
    }

    //发短信
    private void shareToSMS() {
        Media media = mMedias.get(mDisplayViewPager.getCurrentItem());
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // intent.setType("text/plain"); //纯文本
        // 图片分享
        //        String type = "image/" + media.getMimeType();
        shareIntent.setType("image/*");
        // 添加图片
        File f = new File(media.getPath());
        Uri u = Uri.fromFile(f);
        shareIntent.putExtra(Intent.EXTRA_STREAM, u); //添加图片
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "I would like to share this with you...");
        startActivity(Intent.createChooser(shareIntent, "来自Piks"));
    }

    //重命名
    private void showRenameDialog() {
        if (mRenameDialog == null) {
            View renameDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rename, null);
            mRenameDialog = new AlertDialog.Builder(this).setView(renameDialogView).create();
            mRenameDialog.setCanceledOnTouchOutside(true);
            mEditRename = (EditText) renameDialogView.findViewById(R.id.edit_rename);
            renameDialogView.findViewById(R.id.btn_rename).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (renameCurrentMedia(GalleryActivity.this, mEditRename.getText().toString().trim())) {
                        //                        ThinkToast.showToast(GalleryActivity.this, getString(R.string.rename_suc), ThinkToast.LENGTH_SHORT, ThinkToast.SUCCESS);
                    } else {
                        ThinkToast.showToast(GalleryActivity.this, getString(R.string.rename_fail), ThinkToast.LENGTH_SHORT, ThinkToast.ERROR);
                    }
                    if (mRenameDialog.isShowing())
                        mRenameDialog.dismiss();
                }
            });
            renameDialogView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRenameDialog.isShowing())
                        mRenameDialog.dismiss();
                }
            });
        }
        mEditRename.setText("");
        mEditRename.setHint(mMedias.get(mDisplayViewPager.getCurrentItem()).getName().substring(0, mMedias.get(mDisplayViewPager.getCurrentItem()).getName().lastIndexOf('.')));
        if (!mRenameDialog.isShowing())
            mRenameDialog.show();
    }

    public boolean renameCurrentMedia(Context context, String newName) {
        Media currentMedia = mMedias.get(mDisplayViewPager.getCurrentItem());
        boolean success = false;
        try {
            File from = new File(currentMedia.getPath());
            File to = new File(getPhotoPathRenamed(currentMedia.getPath(), newName));
            if (success = ContentUtils.moveFile(context, from, to)) {
                Logger.i("TAG", "from = " + from.getAbsolutePath() + ", to = " + to.getAbsolutePath());
                scanFile(context, new String[]{to.getAbsolutePath(), from.getAbsolutePath()});
                currentMedia.setPath(to.getAbsolutePath());
                modified = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public String getPhotoPathRenamed(String olderPath, String newName) {
        String c = "", b[] = olderPath.split("/");
        for (int x = 0; x < b.length - 1; x++)
            c += b[x] + "/";
        c += newName;
        String name = b[b.length - 1];
        c += name.substring(name.lastIndexOf('.'));
        return c;
    }

    //移动
    private void showMoveSheet() {
        if (mMoveSheetView == null) {
            List<MoveSheetView.MoveItem> items = new ArrayList<>();
            List<Album> albumList = MediasLoader.getInstance().getAlbumsSimple();
            for (Album album : albumList) {
                MoveSheetView.MoveItem item = new MoveSheetView.MoveItem(album.getAlbumCover().getPath(), album.getName(), album.getStorageRootPath(), album.getCount());
                items.add(item);
            }
            mMoveSheetView = new MoveSheetView(this, "移动到", items, new MoveSheetView.OnMoveItemClickListener() {
                @Override
                public void onItemClick(MoveSheetView.MoveItem item) {
                    Logger.i("TAG", item.getStorageRootPath());
                    if (moveCurrentPhoto(GalleryActivity.this, item.getStorageRootPath())) {
                        //                        ThinkToast.showToast(GalleryActivity.this, getString(R.string.move_suc), ThinkToast.LENGTH_SHORT, ThinkToast.SUCCESS);
                    } else {
                        ThinkToast.showToast(GalleryActivity.this, getString(R.string.move_fail), ThinkToast.LENGTH_SHORT, ThinkToast.ERROR);
                    }
                    if (mBottomSheetLayout.isSheetShowing()) {
                        mBottomSheetLayout.dismissSheet();
                    }
                }
            });
        }
        mBottomSheetLayout.showWithSheetView(mMoveSheetView);
    }

    public boolean moveCurrentPhoto(Context context, String path) {
        Media currentMedia = mMedias.get(mDisplayViewPager.getCurrentItem());
        boolean success = false;
        try {
            File from = new File(currentMedia.getPath());
            File to = new File(getPhotoPathMoved(currentMedia.getPath(), path));
            if (success = ContentUtils.moveFile(context, from, to)) {
                scanFile(context, new String[]{to.getAbsolutePath(), from.getAbsolutePath()});
                currentMedia.setPath(to.getAbsolutePath());
                modified = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public String getPhotoPathMoved(String olderPath, String folderPath) {
        String b[] = olderPath.split("/");
        String fi = b[b.length - 1];
        String path = folderPath + "/";
        path += fi;
        return path;
    }

    //设置为
    private void showSettingSheet() {
        //        if (mSettingSheetView == null) {
        //            mSettingSheetView = new SettingSheetView(this, "将照片设置为", new SettingSheetView.OnSettingItemClickListener() {
        //                @Override
        //                public void onItemClick(SettingSheetView.SettingItem item) {
        //
        //                }
        //
        //            });
        //        }
        //        mBottomSheetLayout.showWithSheetView(mSettingSheetView);
        new SetWallpaperTask(mDisplayViewPager.getCurrentItem()).execute();
    }

    /**
     * 启动裁剪
     *
     * @param activity       上下文
     * @param sourceFilePath 需要裁剪图片的绝对路径
     * @param requestCode    比如：UCrop.REQUEST_CROP
     * @return
     */
    private void startUCrop(Activity activity, String sourceFilePath, int requestCode) {
        Uri sourceUri = Uri.fromFile(new File(sourceFilePath));
        //        File outDir = Environment.getExternalStorageDirectory();
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
        cropFilePath = outFile.getAbsolutePath();
        //        Logger.i("TAG", "outFile.getAbsolutePath() = " + outFile.getAbsolutePath());
        //        Logger.i("TAG", "cropFilePath = " + cropFilePath);
        //        Logger.i("TAG", "outFile.getParent() = " + outFile.getParent());
        //        Logger.i("TAG", "outFile.getAbsoluteFile() = " + outFile.getAbsoluteFile());
        //裁剪后图片的绝对路径
        //        String cameraScalePath = outFile.getAbsolutePath();
        Uri destinationUri = Uri.fromFile(outFile);
        //初始化，第一个参数：需要裁剪的图片；第二个参数：裁剪后图片
        UCrop uCrop = UCrop.of(sourceUri, destinationUri);
        //UCrop配置
        uCrop.withOptions(getUcropOptions());
        //跳转裁剪页面
        uCrop.start(activity, requestCode);
    }

    private UCrop.Options getUcropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setCompressionQuality(90);
        options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.primary));
        options.setToolbarColor(ContextCompat.getColor(this, R.color.primary));
        options.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_dark));
        options.setCropFrameColor(ContextCompat.getColor(this, R.color.primary));
        options.setFreeStyleCropEnabled(true);
        return options;
    }

    private class SetWallpaperTask extends AsyncTask<Void, Void, Void> {
        int position;

        public SetWallpaperTask(int position) {
            this.position = position;
        }

        @Override
        protected void onPreExecute() {
            showProgress(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeFile(mMedias.get(position).getPath());
                setWallpaper(bitmap);
            } catch (IOException e) {

            } finally {
                if (bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dismissProgress();
        }
    }

    private class Direction {
        public static final int LEFT = -1;
        public static final int RIGHT = 1;
        public static final int NO_DIRECTION = 0;
    }
}
