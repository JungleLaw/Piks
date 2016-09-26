package com.law.piks.widget.slidemenu;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.law.piks.R;

public class SlideMenu extends ViewGroup {
    public final static int FLAG_DIRECTION_LEFT = 1 << 0;
    public final static int FLAG_DIRECTION_RIGHT = 1 << 1;
    public final static int STATE_CLOSE = 1 << 0;
    public final static int STATE_OPEN_LEFT = 1 << 1;
    public final static int STATE_OPEN_RIGHT = 1 << 2;
    public final static int STATE_DRAG = 1 << 3;
    public final static int STATE_SCROLL = 1 << 4;
    public final static int STATE_OPEN_MASK = 6;
    public final static Interpolator DEFAULT_INTERPOLATOR = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    private static final int MAX_DURATION = 500;
    // 默认阴影宽度
    private static final int DEFAULT_SHADOW_WIDTH = 30;
    // 默认边缘滑动宽度
    private static final int DEFAULT_EDGE_SLIDE_WIDTH = 200;
    private final static int POSITION_LEFT = -1;
    private final static int POSITION_MIDDLE = 0;
    private final static int POSITION_RIGHT = 1;
    // getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动
    // 件，如viewpager就是用这个距离来判断用户是否翻页
    private int mTouchSlop;
    // 用跟踪触摸屏事件（flinging事件和其他gestures手势事件）的速率。用addMovement(MotionEvent)函数将Motion
    // event加入到VelocityTracker类
    private VelocityTracker mVelocityTracker;
    // 主界面点击区域
    private Rect mMainViewHitRect;
    // 滑动的检测区域
    private Rect mEdgeSlideDetectRect;
    ;
    // 阴影宽度
    private int mShadowWidth = DEFAULT_SHADOW_WIDTH;
    // 阴影
    @ExportedProperty
    private Drawable mPrimaryShadowDrawable;
    @ExportedProperty
    private Drawable mSecondaryShadowDrawable;
    // 插值器
    private Interpolator mInterpolator;
    // 为了实现View平滑滚动的一个Helper类
    private Scroller mScroller;
    // 滑动方向标识
    private int mSlideDirectionFlag;
    // 滑动边缘宽度
    private int mEdgeSlideWidth = DEFAULT_EDGE_SLIDE_WIDTH;
    // 主视图
    private View mMainView;
    // 左边菜单
    private View mPrimaryMenu;
    // 右边菜单
    private View mSecondaryMenu;
    private int mWidth;
    private int mHeight;
    // 当前主视图偏移量
    private volatile int mCurrentMainViewOffset;
    // 主视图向右滚动后，左边位置
    private int mMainViewBoundsLeft;
    // 主视图向左滚动后，右边位置
    private int mMainViewBoundsRight;
    // 控件当前状态
    private int mCurrentState = STATE_CLOSE;
    private float mPressedX;
    private float mPressedY;
    private float mLastMotionX;
    private int mCurrentMainViewPosition;
    // 是否处于主视图
    private boolean mIsTapInContent;
    // 是否处于滑动区域
    private boolean mIsTapInEdgeSlide;
    private OnSlideStateChangeListener mSlideStateChangeListener;
    private OnContentTapListener mContentTapListener;
    private boolean mIsEdgeSlideEnable = true;
    private boolean once = false;

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
        init(attrs, defStyleAttr);
    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.slideMenuStyle);
        // TODO Auto-generated constructor stub
    }

    public SlideMenu(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }

    /**
     * Remove view child it's parent node, if the view does not have parent.
     * ignore
     *
     * @param view
     */
    public static void removeViewFromParent(View view) {
        if (null == view) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (null == parent) {
            return;
        }
        parent.removeView(view);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int count = getChildCount();
        final int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        boolean needRemeasure = false;

        int maxChildWidth = 0, maxChildHeight = 0;
        for (int index = 0; index < count; index++) {
            View child = getChildAt(index);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            needRemeasure |= (MeasureSpec.EXACTLY != measureHeightMode && LayoutParams.MATCH_PARENT == layoutParams.height);
            switch (layoutParams.role) {
                case LayoutParams.ROLE_CONTENT:
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    break;
                case LayoutParams.ROLE_PRIMARY_MENU:
                case LayoutParams.ROLE_SECONDARY_MENU:
                    measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    break;
            }

            maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
        }
        maxChildWidth += getPaddingLeft() + getPaddingRight();
        maxChildHeight += getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(resolveSize(maxChildWidth, widthMeasureSpec), resolveSize(maxChildHeight, heightMeasureSpec));

        // we know the exactly size of SlideMenu, so we should use the new size
        // to remeasure the child
        if (needRemeasure) {
            for (int index = 0; index < count; index++) {
                View child = getChildAt(index);
                if (View.GONE != child.getVisibility() && LayoutParams.MATCH_PARENT == child.getLayoutParams().height) {
                    measureChildWithMargins(child, widthMeasureSpec, 0, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY), 0);
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        // TODO Auto-generated method stub
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (once) {
            resolveSlideMode();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        final int count = getChildCount();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        for (int index = 0; index < count; index++) {
            View child = getChildAt(index);
            final int measureWidth = child.getMeasuredWidth();
            final int measureHeight = child.getMeasuredHeight();
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            switch (layoutParams.role) {
                case LayoutParams.ROLE_CONTENT:
                    // we should display the content in front of all other views
                    child.bringToFront();
                    child.layout(mCurrentMainViewOffset + paddingLeft, paddingTop, paddingLeft + measureWidth + mCurrentMainViewOffset, paddingTop + measureHeight);
                    break;
                case LayoutParams.ROLE_PRIMARY_MENU:
                    mMainViewBoundsRight = measureWidth;
                    child.layout(paddingLeft, paddingTop, paddingLeft + measureWidth, paddingTop + measureHeight);
                    break;
                case LayoutParams.ROLE_SECONDARY_MENU:
                    mMainViewBoundsLeft = -measureWidth;
                    child.layout(r - l - paddingRight - measureWidth, paddingTop, r - l - paddingRight, paddingTop + measureHeight);
                    break;
                default:
                    continue;
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawShadow(canvas);
    }

    private void drawShadow(Canvas canvas) {
        if (null == mMainView) {
            return;
        }

        final int left = mMainView.getLeft();
        final int width = mWidth;
        final int height = mHeight;
        if (null != mPrimaryShadowDrawable) {
            mPrimaryShadowDrawable.setBounds((int) (left - mShadowWidth), 0, left, height);
            mPrimaryShadowDrawable.draw(canvas);
        }

        if (null != mSecondaryShadowDrawable) {
            mSecondaryShadowDrawable.setBounds(left + width, 0, (int) (width + left + mShadowWidth), height);
            mSecondaryShadowDrawable.draw(canvas);
        }
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        LayoutParams layoutParams = new LayoutParams(getContext(), attrs);
        return layoutParams;
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        // TODO Auto-generated method stub
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mVelocityTracker = VelocityTracker.obtain();
        mMainViewHitRect = new Rect();
        mEdgeSlideDetectRect = new Rect();
        setWillNotDraw(false);
        initAttrs(attrs, defStyleAttr);
    }

    private void initAttrs(AttributeSet attrs, int defStyleAttr) {
        // TODO Auto-generated method stub
        TypedArray mTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlideMenu, defStyleAttr, 0);
        // Set the shadow attributes

        mPrimaryShadowDrawable = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{Color.TRANSPARENT, Color.argb(99, 0, 0, 0)});

        mSecondaryShadowDrawable = new GradientDrawable(Orientation.LEFT_RIGHT, new int[]{Color.argb(99, 0, 0, 0), Color.TRANSPARENT});

        int interpolatorResId = mTypedArray.getResourceId(R.styleable.SlideMenu_interpolator, -1);
        setInterpolator(-1 == interpolatorResId ? DEFAULT_INTERPOLATOR : AnimationUtils.loadInterpolator(getContext(), interpolatorResId));

        mSlideDirectionFlag = mTypedArray.getInt(R.styleable.SlideMenu_slideDirection, FLAG_DIRECTION_LEFT | FLAG_DIRECTION_RIGHT);

        mTypedArray.recycle();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (MotionEvent.ACTION_UP == ev.getAction()) {
            requestDisallowInterceptTouchEvent(false);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final float x = ev.getX();
        final float y = ev.getY();
        final int currentState = mCurrentState;
        if (STATE_DRAG == currentState || STATE_SCROLL == currentState) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPressedX = mLastMotionX = x;
                mPressedY = y;
                mIsTapInContent = isTapInContent(x, y);
                mIsTapInEdgeSlide = isTapInEdgeSlide(x, y);
                return isOpen() && mIsTapInContent;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPressedX;
                float dy = y - mPressedY;

                if (mIsEdgeSlideEnable && !mIsTapInEdgeSlide && mCurrentState == STATE_CLOSE) {
                    return false;
                }

                // Detect the vertical scroll
                if (Math.abs(dy) >= mTouchSlop && mIsTapInContent && canScrollVertically(this, (int) dy, (int) x, (int) y)) {
                    // if the child can response the vertical scroll, we will not to
                    // steal the MotionEvent any more
                    requestDisallowInterceptTouchEvent(true);
                    return false;
                }

                if (Math.abs(dx) >= mTouchSlop && mIsTapInContent) {
                    if (!canScrollHorizontally(this, (int) dx, (int) x, (int) y)) {
                        setCurrentState(STATE_DRAG);
                        return true;
                    }
                }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final int currentState = mCurrentState;

        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPressedX = mLastMotionX = x;
                mPressedY = y;
                mIsTapInContent = isTapInContent(x, y);
                mIsTapInEdgeSlide = isTapInEdgeSlide(x, y);

                if (mIsTapInContent) {
                    mScroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);

                if (mIsEdgeSlideEnable && !mIsTapInEdgeSlide && mCurrentState == STATE_CLOSE) {
                    return false;
                }

                if (Math.abs(x - mPressedX) >= mTouchSlop && mIsTapInContent && currentState != STATE_DRAG) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    setCurrentState(STATE_DRAG);
                }
                if (STATE_DRAG != currentState) {
                    mLastMotionX = x;
                    return false;
                }
                drag(mLastMotionX, x);
                mLastMotionX = x;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (STATE_DRAG == currentState) {
                    mVelocityTracker.computeCurrentVelocity(1000);
                    endDrag(x, mVelocityTracker.getXVelocity());
                } else if (mIsTapInContent && MotionEvent.ACTION_UP == action) {
                    performContentTap();
                }
                mVelocityTracker.clear();
                getParent().requestDisallowInterceptTouchEvent(false);
                mIsTapInContent = mIsTapInEdgeSlide = false;
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.ACTION_UP == event.getAction()) {
            final boolean isOpen = isOpen();
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (isOpen) {
                        close(true);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (STATE_OPEN_LEFT == mCurrentState) {
                        close(true);
                        return true;
                    } else if (!isOpen) {
                        open(true, true);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (STATE_OPEN_RIGHT == mCurrentState) {
                        close(true);
                        return true;
                    } else if (!isOpen) {
                        open(false, true);
                        return true;
                    }
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * Perform click on the content
     */
    public void performContentTap() {
        if (isOpen()) {
            smoothScrollContentTo(0);
            return;
        }
        if (null != mContentTapListener) {
            mContentTapListener.onContentTap(this);
        }
    }

    protected void drag(float lastX, float x) {
        mCurrentMainViewOffset += (int) (x - lastX);
        setCurrentOffset(mCurrentMainViewOffset);
    }

    /**
     * Detect whether the views inside content are slidable
     */
    protected final boolean canScrollHorizontally(View v, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();

            final int childCount = viewGroup.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View child = viewGroup.getChildAt(index);
                final int left = child.getLeft();
                final int top = child.getTop();
                if (x + scrollX >= left && x + scrollX < child.getRight() && y + scrollY >= top && y + scrollY < child.getBottom() && View.VISIBLE == child.getVisibility() && (ScrollDetectors.canScrollHorizontal(child, dx) || canScrollHorizontally(child, dx, x + scrollX - left, y + scrollY - top))) {
                    return true;
                }
            }
        }

        return ViewCompat.canScrollHorizontally(v, -dx);
    }

    protected final boolean canScrollVertically(View v, int dy, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup viewGroup = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();

            final int childCount = viewGroup.getChildCount();
            for (int index = 0; index < childCount; index++) {
                View child = viewGroup.getChildAt(index);
                final int left = child.getLeft();
                final int top = child.getTop();
                if (x + scrollX >= left && x + scrollX < child.getRight() && y + scrollY >= top && y + scrollY < child.getBottom() && View.VISIBLE == child.getVisibility() && (ScrollDetectors.canScrollVertical(child, dy) || canScrollVertically(child, dy, x + scrollX - left, y + scrollY - top))) {
                    return true;
                }
            }
        }

        return ViewCompat.canScrollVertically(v, -dy);
    }

    /**
     * Resolve the attribute slideMode
     */
    protected void resolveSlideMode() {
        final ViewGroup decorView = (ViewGroup) getRootView();
        final ViewGroup contentContainer = (ViewGroup) decorView.findViewById(android.R.id.content);
        final View content = mMainView;
        if (null == decorView || null == content || 0 == getChildCount()) {
            return;
        }

        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.windowBackground, value, true);

        // remove this view from decor view
        setBackgroundResource(0);
        removeViewFromParent(this);
        // get the origin content view from the content wrapper
        View originContent = contentContainer.getChildAt(0);
        // this is the decor child remove from decor view
        View decorChild = mMainView;
        LayoutParams layoutParams = (LayoutParams) decorChild.getLayoutParams();
        // remove the origin content from content wrapper
        removeViewFromParent(originContent);
        // remove decor child from this view
        removeViewFromParent(decorChild);
        // restore the decor child to decor view
        decorChild.setBackgroundResource(value.resourceId);
        decorView.addView(decorChild);
        // add this view to content wrapper
        contentContainer.addView(this);
        // add the origin content to this view
        addView(originContent, layoutParams);

        once = true;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (null == params) {
            // Skip the view without LayoutParams
            return;
        }
        if (!(params instanceof LayoutParams)) {
            throw new IllegalArgumentException("The parameter params must a instance of " + LayoutParams.class.getName());
        }

        LayoutParams layoutParams = (LayoutParams) params;
        switch (layoutParams.role) {
            case LayoutParams.ROLE_CONTENT:
                removeView(mMainView);
                mMainView = child;
                break;
            case LayoutParams.ROLE_PRIMARY_MENU:
                removeView(mPrimaryMenu);
                mPrimaryMenu = child;
                break;
            case LayoutParams.ROLE_SECONDARY_MENU:
                removeView(mSecondaryMenu);
                mSecondaryMenu = child;
                break;
            default:
                // We will ignore the view without attribute layout_role
                return;
        }
        invalidateMenuState();
        super.addView(child, index, params);
    }

    /**
     * Set animation interpolator when SlideMenu scroll 设置滑动时动画插值器
     *
     * @param interpolator
     */
    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
        mScroller = new Scroller(getContext(), interpolator);
    }

    private void invalidateMenuState() {
        mCurrentMainViewPosition = mCurrentMainViewOffset < 0 ? POSITION_LEFT : (mCurrentMainViewOffset == 0 ? POSITION_MIDDLE : POSITION_RIGHT);
        switch (mCurrentMainViewPosition) {
            case POSITION_LEFT:
                invalidateViewVisibility(mPrimaryMenu, View.INVISIBLE);
                invalidateViewVisibility(mSecondaryMenu, View.VISIBLE);
                break;
            case POSITION_MIDDLE:
                invalidateViewVisibility(mPrimaryMenu, View.INVISIBLE);
                invalidateViewVisibility(mSecondaryMenu, View.INVISIBLE);
                break;
            case POSITION_RIGHT:
                invalidateViewVisibility(mPrimaryMenu, View.VISIBLE);
                invalidateViewVisibility(mSecondaryMenu, View.INVISIBLE);
                break;
        }
    }

    private void invalidateViewVisibility(View view, int visibility) {
        if (null != view && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mShadowWidth = mShadowWidth;
        savedState.slideDirectionFlag = mSlideDirectionFlag;
        savedState.currentState = mCurrentState;
        savedState.currentMainViewOffset = mCurrentMainViewOffset;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        mShadowWidth = (int) savedState.mShadowWidth;
        mSlideDirectionFlag = savedState.slideDirectionFlag;
        mCurrentState = savedState.currentState;
        mCurrentMainViewOffset = savedState.currentMainViewOffset;

        invalidateMenuState();
        requestLayout();
        invalidate();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Equals invoke {@link #smoothScrollContentTo(int, float)} with 0 velocity
     *
     * @param targetOffset
     */
    public void smoothScrollContentTo(int targetOffset) {
        smoothScrollContentTo(targetOffset, 0);
    }

    /**
     * Set current state
     *
     * @param currentState
     */
    protected void setCurrentState(int currentState) {
        if (null != mSlideStateChangeListener && currentState != mCurrentState) {
            mSlideStateChangeListener.onSlideStateChange(currentState);
        }
        this.mCurrentState = currentState;
    }

    /**
     * Perform a smooth slide of content, the offset of content will limited to
     * menu width
     *
     * @param targetOffset
     * @param velocity
     */
    public void smoothScrollContentTo(int targetOffset, float velocity) {
        setCurrentState(STATE_SCROLL);
        int distance = targetOffset - mCurrentMainViewOffset;
        velocity = Math.abs(velocity);
        int duration = 400;
        if (velocity > 0) {
            duration = 3 * Math.round(1000 * Math.abs(distance / velocity));
        }
        duration = Math.min(duration, MAX_DURATION);
        mScroller.abortAnimation();
        mScroller.startScroll(mCurrentMainViewOffset, 0, distance, 0, duration);
        invalidate();
    }

    private boolean isTapInContent(float x, float y) {
        final View content = mMainView;
        if (null != content) {
            content.getHitRect(mMainViewHitRect);
            return mMainViewHitRect.contains((int) x, (int) y);
        }
        return false;
    }

    private boolean isTapInEdgeSlide(float x, float y) {
        final Rect rect = mEdgeSlideDetectRect;
        boolean result = false;
        if (null != mPrimaryMenu) {
            getHitRect(rect);
            rect.right = mEdgeSlideWidth;
            result |= rect.contains((int) x, (int) y);
        }

        if (null != mSecondaryMenu) {
            getHitRect(rect);
            rect.left = rect.right - mEdgeSlideWidth;
            result |= rect.contains((int) x, (int) y);
        }
        return result;
    }

    protected void endDrag(float x, float velocity) {
        final int currentContentOffset = mCurrentMainViewOffset;
        final int currentContentPosition = mCurrentMainViewPosition;
        boolean velocityMatched = Math.abs(velocity) > 400;
        switch (currentContentPosition) {
            case POSITION_LEFT:
                if ((velocity < 0 && velocityMatched) || (velocity >= 0 && !velocityMatched)) {
                    smoothScrollContentTo(mMainViewBoundsLeft, velocity);
                } else if ((velocity > 0 && velocityMatched) || (velocity <= 0 && !velocityMatched)) {
                    smoothScrollContentTo(0, velocity);
                }
                break;
            case POSITION_MIDDLE:
                setCurrentState(STATE_CLOSE);
                break;
            case POSITION_RIGHT:
                if ((velocity > 0 && velocityMatched) || (velocity <= 0 && !velocityMatched)) {
                    smoothScrollContentTo(mMainViewBoundsRight, velocity);
                } else if ((velocity < 0 && velocityMatched) || (velocity >= 0 && !velocityMatched)) {
                    smoothScrollContentTo(0, velocity);
                }
                break;
        }
    }

    private void setCurrentOffset(int currentOffset) {
        final int slideDirectionFlag = mSlideDirectionFlag;
        final int currentContentOffset = mCurrentMainViewOffset = Math.min((slideDirectionFlag & FLAG_DIRECTION_RIGHT) == FLAG_DIRECTION_RIGHT ? mMainViewBoundsRight : 0, Math.max(currentOffset, (slideDirectionFlag & FLAG_DIRECTION_LEFT) == FLAG_DIRECTION_LEFT ? mMainViewBoundsLeft : 0));
        if (null != mSlideStateChangeListener) {
            float slideOffsetPercent = 0;
            if (0 < currentContentOffset) {
                slideOffsetPercent = currentContentOffset * 1.0f / mMainViewBoundsRight;
            } else if (0 > currentContentOffset) {
                slideOffsetPercent = -currentContentOffset * 1.0f / mMainViewBoundsLeft;
            }
            mSlideStateChangeListener.onSlideOffsetChange(slideOffsetPercent);
        }
        invalidateMenuState();
        invalidate();
        requestLayout();
    }

    @Override
    public void computeScroll() {
        if (STATE_SCROLL == mCurrentState || isOpen()) {
            if (mScroller.computeScrollOffset()) {
                setCurrentOffset(mScroller.getCurrX());
            } else {
                setCurrentState(mCurrentMainViewOffset == 0 ? STATE_CLOSE : (mCurrentMainViewOffset > 0 ? STATE_OPEN_LEFT : STATE_OPEN_RIGHT));
            }
        }
    }

    /**
     * Indicate this SlideMenu is open
     *
     * @return true open, otherwise false
     */
    public boolean isOpen() {
        return (STATE_OPEN_MASK & mCurrentState) != 0;
    }

    /**
     * Open the SlideMenu
     *
     * @param isSlideLeft
     * @param isAnimated
     */
    public void open(boolean isSlideLeft, boolean isAnimated) {
        if (isOpen()) {
            return;
        }

        int targetOffset = isSlideLeft ? mMainViewBoundsLeft : mMainViewBoundsRight;

        if (isAnimated) {
            smoothScrollContentTo(targetOffset);
        } else {
            mScroller.abortAnimation();
            setCurrentOffset(targetOffset);
            setCurrentState(isSlideLeft ? STATE_OPEN_LEFT : STATE_OPEN_RIGHT);
        }
    }

    /**
     * Close the SlideMenu
     *
     * @param isAnimated
     */
    public void close(boolean isAnimated) {
        if (STATE_CLOSE == mCurrentState) {
            return;
        }

        if (isAnimated) {
            smoothScrollContentTo(0);
        } else {
            mScroller.abortAnimation();
            setCurrentOffset(0);
            setCurrentState(STATE_CLOSE);
        }
    }

    public interface OnSlideStateChangeListener {
        /**
         * Invoked when slide state change
         *
         * @param slideState {@link SlideMenu#STATE_CLOSE},{@link SlideMenu#STATE_OPEN_LEFT},
         *                   {@link SlideMenu#STATE_OPEN_RIGHT},{@link SlideMenu#STATE_DRAG},
         *                   {@link SlideMenu#STATE_SCROLL}
         */
        public void onSlideStateChange(int slideState);

        /**
         * Invoked when slide offset change
         *
         * @param offsetPercent negative means slide left, otherwise slide right
         */
        public void onSlideOffsetChange(float offsetPercent);
    }

    public interface OnContentTapListener {
        public void onContentTap(SlideMenu slideMenu);
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        public int mShadowWidth;
        public int slideDirectionFlag;
        public int currentState;
        public int currentMainViewOffset;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            mShadowWidth = in.readInt();
            slideDirectionFlag = in.readInt();
            currentState = in.readInt();
            currentMainViewOffset = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(mShadowWidth);
            out.writeInt(slideDirectionFlag);
            out.writeInt(currentState);
            out.writeInt(currentMainViewOffset);
        }
    }

    /**
     * Add view role for {@link #SlideMenu}
     *
     * @author Tank
     */
    public static class LayoutParams extends MarginLayoutParams {
        public final static int ROLE_CONTENT = 0;
        public final static int ROLE_PRIMARY_MENU = 1;
        public final static int ROLE_SECONDARY_MENU = 2;

        public int role;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideMenu_Layout, 0, 0);

            final int indexCount = a.getIndexCount();
            for (int index = 0; index < indexCount; index++) {
                int attributeIndex = a.getIndex(index);
                if (R.styleable.SlideMenu_Layout_layout_role == attributeIndex) {
                    role = a.getInt(R.styleable.SlideMenu_Layout_layout_role, -1);
                }
            }

            switch (role) {
                // content should match whole SlidingMenu
                case ROLE_CONTENT:
                    width = MATCH_PARENT;
                    height = MATCH_PARENT;
                    break;
                case ROLE_SECONDARY_MENU:
                case ROLE_PRIMARY_MENU:
                    // add your custom layout rule here for menu
                    width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85f);
                    break;
                default:
                    throw new IllegalArgumentException("You must specified a layout_role for this view");
            }
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int role) {
            super(width, height);
            this.role = role;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);

            if (layoutParams instanceof LayoutParams) {
                role = ((LayoutParams) layoutParams).role;
            }
        }
    }
}
