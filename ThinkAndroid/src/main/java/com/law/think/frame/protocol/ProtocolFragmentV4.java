package com.law.think.frame.protocol;

import com.law.think.frame.inject.ThinkInject;
import com.law.think.frame.protocol.interfaces.FragmentProtocol;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class ProtocolFragmentV4 extends Fragment implements FragmentProtocol {
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initVariables();
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if (setContentViewLayout() <= 0) {
			throw new IllegalArgumentException("contentview layout id must greater than 0");
		}
		View contentView = inflater.inflate(setContentViewLayout(), null);
		// ButterKnife.bind(getActivity());
		ThinkInject.bind(this, contentView);
		return contentView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
		initListener();
		loadData();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		destroyTask();
		// ButterKnife.unbind(getActivity());
		super.onDestroy();
	}
}
