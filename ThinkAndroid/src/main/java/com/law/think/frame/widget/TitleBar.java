package com.law.think.frame.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.law.frame.think.R;
import com.law.think.frame.utils.PixelUtils;

public class TitleBar extends RelativeLayout {
    private static final String DEFAULT_TITLE_TEXT = "TITLEBAR";
    private static final float DEFAULT_TITLE_TEXTSIZE = 14f;
    private static final String DEFAULT_LEFT_TEXT = "";
    private static final String DEFAULT_RIGHT_TEXT = "";
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_TITLE_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_LEFT_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_RIGHT_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_LEFT_DRAWABLE_ID = -1;
    private static final int DEFAULT_RIGHT_DRAWABLE_D = -1;

    private String mTitleText;
    private float mTitleTextSize;
    private String mLeftText;
    private String mRightText;
    private int mBackgroundColor;
    private int mTitleTextColor;
    private int mLeftTextColor;
    private int mRightTextColor;
    private int mLeftDrawableID;
    private int mRightDrawableID;
    private ColorStateList mLeftColorStateList;
    private ColorStateList mRightColorStateList;

    private TextView mLeftTextView;
    private TextView mRightTextView;
    private TextView mTitleView;
    private ImageButton mLeftImgBtn;
    private ImageButton mRightImgBtn;

    private RelativeLayout.LayoutParams mLeftLayoutParams;
    private RelativeLayout.LayoutParams mRightLayoutParams;
    private RelativeLayout.LayoutParams mTitleLayoutParams;

    public TitleBar(Context context) {
        this(context, null, 0);
        // TODO Auto-generated constructor stub
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        // TODO Auto-generated method stub
        initAttrs(attrs);
        initView();
    }

    private void initAttrs(AttributeSet attrs) {
        // TODO Auto-generated method stub
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);
        mTitleText = mTypedArray.getString(R.styleable.TitleBar_titleText);
        if (mTitleText == null) {
            mTitleText = DEFAULT_TITLE_TEXT;
        }
        mTitleTextSize = (int) mTypedArray.getDimension(R.styleable.TitleBar_titleTextSize, DEFAULT_TITLE_TEXTSIZE);
        mLeftText = mTypedArray.getString(R.styleable.TitleBar_leftText);
        if (mLeftText == null) {
            mLeftText = DEFAULT_LEFT_TEXT;
        }
        mRightText = mTypedArray.getString(R.styleable.TitleBar_rightText);
        if (mRightText == null) {
            mRightText = DEFAULT_RIGHT_TEXT;
        }
        mBackgroundColor = mTypedArray.getColor(R.styleable.TitleBar_backgroudColor, DEFAULT_BACKGROUND_COLOR);
        mTitleTextColor = mTypedArray.getColor(R.styleable.TitleBar_titleColor, DEFAULT_TITLE_TEXT_COLOR);
        mLeftColorStateList = mTypedArray.getColorStateList(R.styleable.TitleBar_leftTextColor);
        if (mLeftColorStateList == null)
            mLeftTextColor = mTypedArray.getColor(R.styleable.TitleBar_leftTextColor, DEFAULT_LEFT_TEXT_COLOR);
        mRightColorStateList = mTypedArray.getColorStateList(R.styleable.TitleBar_rightTextColor);
        if (mRightColorStateList == null)
            mRightTextColor = mTypedArray.getColor(R.styleable.TitleBar_rightTextColor, DEFAULT_RIGHT_TEXT_COLOR);
        mLeftDrawableID = mTypedArray.getResourceId(R.styleable.TitleBar_leftDrawableID, DEFAULT_LEFT_DRAWABLE_ID);
        mRightDrawableID = mTypedArray.getResourceId(R.styleable.TitleBar_rightDrawableID, DEFAULT_RIGHT_DRAWABLE_D);
        mTypedArray.recycle();
    }

    private void initView() {
        addTitleView();
        addLeftView();
        addRightView();
    }

    private void addTitleView() {
        // TODO Auto-generated method stub
        mTitleView = new TextView(getContext());
        mTitleLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mTitleLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, TRUE);
        mTitleView.setText(mTitleText);
        mTitleView.setTextSize(PixelUtils.px2sp(getContext(), mTitleTextSize));
        mTitleView.setGravity(Gravity.CENTER);
        mTitleView.setTextColor(mTitleTextColor);
        mTitleView.setSingleLine();
        mTitleView.setBackgroundColor(mBackgroundColor);
        //        mTitleView.setAlpha(0.6f);
        //        mTitleView.setBackgroundColor(Color.BLACK);
        addView(mTitleView, mTitleLayoutParams);
    }

    private void addLeftView() {
        // TODO Auto-generated method stub
        mLeftLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mLeftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, TRUE);
        if (mLeftDrawableID < 0) {
            mLeftTextView = new TextView(getContext());
            mLeftTextView.setText(mLeftText);
            mLeftTextView.setTextSize(DEFAULT_TITLE_TEXTSIZE);
            mLeftTextView.setPadding(PixelUtils.dp2px(getContext(), 12), 0, PixelUtils.dp2px(getContext(), 12), 0);
            mLeftTextView.setGravity(Gravity.CENTER);
            if (mLeftColorStateList != null) {
                mLeftTextView.setTextColor(mLeftColorStateList);
            } else {
                mLeftTextView.setTextColor(mLeftTextColor);
            }
            mLeftTextView.setSingleLine();
            mLeftTextView.setBackgroundColor(Color.TRANSPARENT);
            addView(mLeftTextView, mLeftLayoutParams);
        } else {
            mLeftImgBtn = new ImageButton(getContext());
            mLeftImgBtn.setImageResource(mLeftDrawableID);
            mLeftImgBtn.setScaleType(ScaleType.CENTER_INSIDE);
            mLeftImgBtn.setBackgroundColor(Color.TRANSPARENT);
            mLeftImgBtn.setPadding(PixelUtils.dp2px(getContext(), 12), 0, PixelUtils.dp2px(getContext(), 12), 0);
            addView(mLeftImgBtn, mLeftLayoutParams);
        }
    }

    private void addRightView() {
        // TODO Auto-generated method stub

        mRightLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mRightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, TRUE);
        if (mRightDrawableID < 0) {
            mRightTextView = new TextView(getContext());
            mRightTextView.setText(mRightText);
            mRightTextView.setTextSize(DEFAULT_TITLE_TEXTSIZE);
            mRightTextView.setPadding(PixelUtils.dp2px(getContext(), 12), 0, PixelUtils.dp2px(getContext(), 12), 0);
            mRightTextView.setGravity(Gravity.CENTER);
            if (mRightColorStateList != null) {
                mRightTextView.setTextColor(mRightColorStateList);
            } else {
                mRightTextView.setTextColor(mRightTextColor);
            }
            mRightTextView.setSingleLine();
            mRightTextView.setBackgroundColor(Color.TRANSPARENT);
            addView(mRightTextView, mRightLayoutParams);
        } else {
            mRightImgBtn = new ImageButton(getContext());
            mRightImgBtn.setImageResource(mRightDrawableID);
            mRightImgBtn.setScaleType(ScaleType.CENTER_INSIDE);
            mRightImgBtn.setBackgroundColor(Color.TRANSPARENT);
            mRightImgBtn.setPadding(PixelUtils.dp2px(getContext(), 12), 0, PixelUtils.dp2px(getContext(), 12), 0);
            addView(mRightImgBtn, mRightLayoutParams);
        }
    }

    public void setOnLeftViewClickListener(OnClickListener mListener) {
        if (mLeftDrawableID < 0) {
            if (mLeftTextView != null)
                mLeftTextView.setOnClickListener(mListener);
        } else {
            if (mLeftImgBtn != null)
                mLeftImgBtn.setOnClickListener(mListener);
        }
    }

    public void setOnRightViewClickListener(OnClickListener mListener) {
        if (mRightDrawableID < 0) {
            if (mRightTextView != null)
                mRightTextView.setOnClickListener(mListener);
        } else {
            if (mRightImgBtn != null)
                mRightImgBtn.setOnClickListener(mListener);
        }
    }

    public void setTitleViewText(String mTitle) {
        mTitleView.setText(mTitle);
    }

    @Override
    public void setBackgroundColor(int color) {
        mTitleView.setBackgroundColor(color);
    }

    @Override
    public void setBackground(Drawable drawable) {
        mTitleView.setBackground(drawable);
    }

    public View getTitleView() {
        return mTitleView;
    }
}
