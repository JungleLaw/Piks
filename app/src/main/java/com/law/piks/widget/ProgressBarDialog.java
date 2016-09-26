package com.law.piks.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import com.law.piks.R;

/**
 * Created by Law on 2016/9/9.
 */
public class ProgressBarDialog extends Dialog {

    protected ProgressBarDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public static ProgressBarDialog createDialog(Context context) {
        ProgressBarDialog dialog = new ProgressBarDialog(context, R.style.progressStyle);
        dialog.setContentView(R.layout.layout_progress_dialog);
        return dialog;
    }

}
