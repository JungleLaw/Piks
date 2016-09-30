package com.law.think.frame.widget.labelview;

import com.law.frame.think.R;
import com.law.think.frame.utils.PixelUtils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class LabelView extends View {
    private static final int LAYER_FLAGS = Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG
            | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG;

    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_SIZE = 20;// sp
    private static final String TYPEFACE = "sans-serif-black";
    private static final int TYPEFACE_STYLE = Typeface.BOLD;
    private static final int CORNER_RADIUS = 2; // dp
    private static final int PADDING = 3; // dp

    private int mBackgroundColor;
    private int mTextSize;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private Paint mCanvasPaint;
    private Rect mTextBounds;

    private String text;

    private float mCornerRadius;
    private float mPadding;
    private int width;
    private int height;

    public LabelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(attrs);

    }

    public LabelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        // TODO Auto-generated constructor stub
    }

    public LabelView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        if (width > 0 && height > 0) {
            setMeasuredDimension(width, height);
        } else {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(1, 1);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        // super.onDraw(canvas);
        drawLabelView(canvas);
    }

    private void init(AttributeSet attrs) {
        // TODO Auto-generated method stub
        initAttrs(attrs);
        initPaint();
        mCornerRadius = PixelUtils.dp2px(getContext(), CORNER_RADIUS);
        mPadding = PixelUtils.dp2px(getContext(), PADDING);
    }

    private void initAttrs(AttributeSet attrs) {
        // TODO Auto-generated method stub
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.LabelView, 0, 0);
        mBackgroundColor = mTypedArray.getColor(R.styleable.LabelView_label_BackgroundColor, DEFAULT_BACKGROUND_COLOR);
        mTextSize = (int) mTypedArray.getDimension(R.styleable.LabelView_label_TextSize, DEFAULT_TEXT_SIZE);
        text = mTypedArray.getString(R.styleable.LabelView_label_Text);
        mTypedArray.recycle();
    }

    private void initPaint() {
        // TODO Auto-generated method stub
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(mBackgroundColor);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        // mTextPaint.setTypeface(Typeface.create(TYPEFACE, TYPEFACE_STYLE));
        mTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, TYPEFACE_STYLE));
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setFilterBitmap(true);
        mTextPaint.setStyle(Style.STROKE.FILL);
        mTextPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mTextPaint.setColor(Color.TRANSPARENT);

        mCanvasPaint = new Paint();
        mCanvasPaint.setColor(Color.TRANSPARENT);
    }

    public void setLabel(String text) {
        this.text = text;
        setLabel(text, mBackgroundColor);
    }

    private void setLabel(String text, int color) {
        mTextBounds = new Rect();
        mTextPaint.getTextBounds(text, 0, text.length(), mTextBounds);
        width = (int) (mTextBounds.width() + mPadding * 2);
        height = (int) (mTextBounds.height() + mPadding * 2);
        requestLayout();
    }

    private void drawLabelView(Canvas mCanvas) {
        int i = mCanvas.saveLayer(0, 0, width, height, null, LAYER_FLAGS);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);
        mCanvas.drawBitmap(bitmap, 0, 0, mCanvasPaint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCanvas.drawRoundRect(0, 0, width, height, mCornerRadius, mCornerRadius, mBackgroundPaint);
        } else {
            mCanvas.drawRect(0, 0, width, height, mBackgroundPaint);
        }
        // punch out the text leaving transparency
        mCanvas.drawText(text, mPadding, height - mPadding, mTextPaint);
        mCanvas.restoreToCount(i);
    }

}
