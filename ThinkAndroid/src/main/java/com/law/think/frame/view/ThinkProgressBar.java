package com.law.think.frame.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.law.frame.think.R;

public class ThinkProgressBar extends View {

	private static final int COLOR1 = 0xFF94FF94;
	private static final int COLOR2 = 0xFFFFE1C0;
	private static final int COLOR3 = 0xFFFFF08B;
	private static final int COLOR4 = 0xFF00315E;
	private static final int COLOR5 = 0xFF5C8264;
	private static final int COLOR6 = 0xFFFF6057;
	private static final int COLOR7 = 0xFF362525;
	private static final int COLOR8 = 0xFF09E8C9;
	private static final int COLOR9 = 0xFF500D0C;
	// 宽度
	private int mWidth;
	// 高度
	private int mHeight;
	// 显示文字内容
	private String mDisplayText;
	// 显示文字颜色
	private int mDisplayTextColor;
	// 显示文字大小
	private int mDisplayTextSize;
	// 方框边长
	private int mCellLenght;
	// 方框间隔
	private int mDivider;

	private Paint mBackgroundPaint;
	private Paint mFramePaint;
	private Paint mTextPaint;

	private LoadingFrame mFrame;
	private DisplayTextFrame mDisplayTextFrame;

	private static int[] COLORS = { COLOR1, COLOR2, COLOR3, COLOR4, COLOR5, COLOR6, COLOR7, COLOR8, COLOR9 };

	// private

	public ThinkProgressBar(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public ThinkProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}

	public ThinkProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructorstub
		initialize(context, attrs, defStyleAttr);
	}

	private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
		// TODO Auto-generated method stub
		initAttrs(context, attrs, defStyleAttr);
		initPaints();
	}

	private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
		final TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ThinkProgressBar, defStyleAttr,
				0);
		mCellLenght = mTypedArray.getDimensionPixelSize(R.styleable.ThinkProgressBar_cellLenght,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 30, getResources().getDisplayMetrics()));
		mDivider = mTypedArray.getDimensionPixelSize(R.styleable.ThinkProgressBar_cellDivider,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
		mDisplayText = mTypedArray.getString(R.styleable.ThinkProgressBar_displayText);
		mDisplayTextColor = mTypedArray.getColor(R.styleable.ThinkProgressBar_displayTextColor, Color.BLACK);
		mDisplayTextSize = mTypedArray.getDimensionPixelSize(R.styleable.ThinkProgressBar_displayTextSize,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
		mTypedArray.recycle();
	}

	private void initPaints() {
		// 设置背景的画笔
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(Color.TRANSPARENT);

		// 设置Frame的画笔
		mFramePaint = new Paint();

		// 设置文本画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(mDisplayTextColor);
		mTextPaint.setTextSize(mDisplayTextSize);
		mTextPaint.setStyle(Paint.Style.FILL);
		mTextPaint.setAntiAlias(true);
	}

	private void initFrame(int width, int height) {
		int frameWidth = mCellLenght * 3 + mDivider * 2;
		int frameHeight = mCellLenght * 3 + mDivider * 2;
		mFrame = new LoadingFrame();
		mFrame.setFrameLeft((width - frameWidth) / 2);
		mFrame.setFrameTop((height - frameHeight) / 2);
		List<Cell> mCells = new ArrayList<>();
		for (int lineIndex = 0; lineIndex < 3; lineIndex++) {
			for (int columnIndex = 0; columnIndex < 3; columnIndex++) {
				Cell mCell = new Cell();
				mCell.setLeft(mFrame.getFrameLeft() + (mCellLenght + mDivider) * columnIndex);
				mCell.setTop(mFrame.getFrameTop() + (mCellLenght + mDivider) * lineIndex);
				mCell.setLenght(mCellLenght);
				mCell.setColor(COLORS[lineIndex * 3 + columnIndex]);
				mCells.add(mCell);
			}
		}
		mFrame.setCells(mCells);

		// 初始化文字
		mDisplayTextFrame = new DisplayTextFrame();
		int mTextWidth = (int) mTextPaint.measureText(mDisplayText);
		mDisplayTextFrame.setContent(mDisplayText);
		mDisplayTextFrame.setLeft((width - mTextWidth) / 2);
		mDisplayTextFrame.setBottom(mFrame.getFrameTop() + frameHeight + mDivider + mDisplayTextSize);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 系统帮我们测量的高度和宽度都是MATCH_PARNET，当我们设置明确的宽度和高度时，系统帮我们测量的结果就是我们设置的结果，
	 * 当我们设置为WRAP_CONTENT,或者MATCH_PARENT系统帮我们测量的结果就是MATCH_PARENT的长度。
	 * 所以，当设置了WRAP_CONTENT时，我们需要自己进行测量，即重写onMesure方法”：
	 * 重写之前先了解MeasureSpec的specMode,一共三种类型： EXACTLY：一般是设置了明确的值或者是MATCH_PARENT;
	 * AT_MOST：表示子布局限制在一个最大值内，一般为WARP_CONTENT; UNSPECIFIED：表示子布局想要多大就多大，很少使用
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		// int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		// if (widthMode == MeasureSpec.EXACTLY) {
		mWidth = widthSize;
		// }
		// if (heightMode == MeasureSpec.EXACTLY) {
		mHeight = heightSize;
		// }
		initFrame(mWidth, mHeight);
		setMeasuredDimension(widthSize, heightSize);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.TRANSPARENT);
		mFrame.drawFrame(canvas);
		mDisplayTextFrame.drawDisplayText(canvas);
	}

	public class LoadingFrame {
		private int frameLeft;
		private int frameTop;
		private List<Cell> cells;

		public int getFrameLeft() {
			return frameLeft;
		}

		public void setFrameLeft(int frameLeft) {
			this.frameLeft = frameLeft;
		}

		public int getFrameTop() {
			return frameTop;
		}

		public void setFrameTop(int frameTop) {
			this.frameTop = frameTop;
		}

		public List<Cell> getCells() {
			return cells;
		}

		public void setCells(List<Cell> cells) {
			this.cells = cells;
		}

		private int step = 0;

		public void drawFrame(Canvas mCanvas) {
			for (Cell mCell : cells) {
				mCell.drawCell(mCanvas, mFramePaint);
			}
			switch (step % 9) {
			case 0:
				if (step > 0) {
					swichColor(getCells(), 8, 0);
				}
				break;
			case 1:
				swichColor(getCells(), 0, 1);
				break;
			case 2:
				swichColor(getCells(), 1, 2);
				break;
			case 3:
				swichColor(getCells(), 2, 3);
				break;
			case 4:
				swichColor(getCells(), 3, 4);
				break;
			case 5:
				swichColor(getCells(), 4, 5);
				break;
			case 6:
				swichColor(getCells(), 5, 6);
				break;
			case 7:
				swichColor(getCells(), 6, 7);
				break;
			case 8:
				swichColor(getCells(), 7, 8);
				break;
			default:
				break;
			}
			if (step > 1000) {
				step = 0;
			}
			step++;
			if (getVisibility() == View.VISIBLE)
				postInvalidateDelayed(100);
		}
	}

	private void swichColor(List<Cell> cells, int fristIndex, int secondIndex) {
		int tempColor = cells.get(fristIndex).getColor();
		cells.get(fristIndex).setColor(cells.get(secondIndex).getColor());
		cells.get(secondIndex).setColor(tempColor);
	}

	public class Cell {
		private int left;
		private int top;
		private int color;
		private int lenght;

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
		}

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
		}

		public int getColor() {
			return color;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public int getLenght() {
			return lenght;
		}

		public void setLenght(int lenght) {
			this.lenght = lenght;
		}

		public void drawCell(Canvas mCanvas, Paint mPaint) {
			mPaint.setColor(color);
			mCanvas.drawRect(left, top, left + lenght, top + lenght, mPaint);
		}
	}

	public class DisplayTextFrame {
		private String content;
		private int left;
		private int bottom;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
		}

		public int getBottom() {
			return bottom;
		}

		public void setBottom(int bottom) {
			this.bottom = bottom;
		}

		public void drawDisplayText(Canvas mCanvas) {
			mCanvas.drawText(content, left, bottom, mTextPaint);
		}
	}

	@Override
	public void setVisibility(int visibility) {
		// TODO Auto-generated method stub
		super.setVisibility(visibility);
		if (getVisibility() == VISIBLE) {
			postInvalidate();
		}
	}
}
