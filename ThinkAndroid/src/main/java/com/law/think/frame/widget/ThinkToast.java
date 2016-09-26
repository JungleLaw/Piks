package com.law.think.frame.widget;

import com.law.frame.think.R;
import com.law.think.frame.utils.PixelUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ThinkToast {

	private static final int DEFAULT_RADIUS = 7;

	public static final int LENGTH_SHORT = 0;
	public static final int LENGTH_LONG = 1;

	public static final int SUCCESS = 1;
	public static final int WARNING = 2;
	public static final int ERROR = 3;
	public static final int INFO = 4;
	public static final int HELP = 5;
	public static final int DEFAULT = 6;

	private ThinkToast() {
	}

	public static void showToast(Context context, String msg, int length, int type) {
		new ThinkToast().createView(context, msg, length, type);
	}

	private void createView(Context context, String msg, int length, int type) {
		View toastView = init(context, msg, type);
		Toast mToast = new Toast(context);
		mToast.setView(toastView);
		mToast.setDuration(length);
		mToast.show();
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private View init(Context context, String msg, int type) {
		View toastView = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);

		ImageView imgToastIcon = (ImageView) toastView.findViewById(R.id.img_toast_icon);
		imgToastIcon.setImageResource(getIconResId(type));

		View contentParent = toastView.findViewById(R.id.content_parent);

		TextView contentText = (TextView) toastView.findViewById(R.id.text_content);
		contentText.setText(msg);
		contentText.setTextColor(context.getResources().getColor(getTextColorResId(type)));

		toastView.setBackground(getShapeDrawable(context, context.getResources().getColor(getColorResId(type))));
		contentParent.setBackground(getShapeDrawable(context, Color.DKGRAY));

		return toastView;
	}

	private int getIconResId(int type) {
		if (DEFAULT == type) {
			return R.drawable.icon_toast_info;
		}
		if (INFO == type) {
			return R.drawable.icon_toast_info;
		}
		if (HELP == type) {
			return R.drawable.icon_toast_help;
		}
		if (ERROR == type) {
			return R.drawable.icon_toast_error;
		}
		if (SUCCESS == type) {
			return R.drawable.icon_toast_suc;
		}
		if (WARNING == type) {
			return R.drawable.icon_toast_warning;
		}
		return R.drawable.icon_toast_info;
	}

	private int getColorResId(int type) {
		if (SUCCESS == type) {
			return R.color.color_suc;
		}
		if (WARNING == type) {
			return R.color.color_warning;
		}
		if (ERROR == type) {
			return R.color.color_error;
		}
		if (HELP == type) {
			return R.color.color_help;
		}
		if (INFO == type) {
			return R.color.color_info;
		}
		if (DEFAULT == type) {
			return R.color.color_info;
		}
		return R.color.color_info;
	}

	private int getTextColorResId(int type) {
		if (SUCCESS == type) {
			return R.color.color_suc;
		}
		if (WARNING == type) {
			return R.color.color_warning;
		}
		if (ERROR == type) {
			return R.color.color_error;
		}
		if (HELP == type) {
			return R.color.color_help;
		}
		if (INFO == type) {
			return R.color.color_white;
		}
		if (DEFAULT == type) {
			return R.color.color_white;
		}
		return R.color.color_white;
	}

	private ShapeDrawable getShapeDrawable(Context context, int color) {
		int radius = PixelUtils.dp2px(context, DEFAULT_RADIUS);
		float[] outerRadii = new float[] { radius, radius, radius, radius, radius, radius, radius, radius };
		RoundRectShape roundRectShape = new RoundRectShape(outerRadii, null, null);
		ShapeDrawable shapeDrawable = new ShapeDrawable(roundRectShape);
		shapeDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
		shapeDrawable.getPaint().setColor(color);
		return shapeDrawable;
	}
}
