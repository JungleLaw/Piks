package com.law.think.frame.inject;

import android.app.Activity;
import android.content.Context;
import android.view.View;

class ViewFinder {
	private View view;
	private Activity activity;

	public ViewFinder(View view) {
		this.view = view;
	}

	public ViewFinder(Activity activity) {
		this.activity = activity;
	}

	public View findViewById(int id) {
		return activity == null ? view.findViewById(id) : activity.findViewById(id);
	}

	public View findViewByInfo(ViewInjectInfo info) {
		return findViewById((Integer) info.value, info.parentId);
	}

	public View findViewById(int id, int parentId) {
		View parentView = null;
		if (parentId > 0) {
			parentView = this.findViewById(parentId);
		}

		View view = null;
		if (parentView != null) {
			view = parentView.findViewById(id);
		} else {
			view = this.findViewById(id);
		}
		return view;
	}

	public Context getContext() {
		if (view != null)
			return view.getContext();
		if (activity != null)
			return activity;
		return null;
	}
}
