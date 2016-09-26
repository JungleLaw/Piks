package com.law.think.frame.protocol;

import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.protocol.interfaces.ActivityProtocol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * 
 * @author Law
 * 
 */
public abstract class ProtocolFragmentActivity extends FragmentActivity implements ActivityProtocol {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (setContentViewLayout() <= 0) {
			throw new IllegalArgumentException("contentview layout id must greater than 0");
		}
		setContentView(setContentViewLayout());
		// ButterKnife.bind(this);
		ThinkInject.bind(this);
		initVariables();
		initViews();
		initListener();
		loadData(savedInstanceState);
		if (loadEntryAnimation() != null) {
			overridePendingTransition(loadEntryAnimation()[0], loadEntryAnimation()[1]);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		destroyTask();
		// ButterKnife.unbind(this);
		super.onDestroy();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		if (loadExitAnimation() != null) {
			overridePendingTransition(loadExitAnimation()[0], loadExitAnimation()[1]);
		}
	}
}
