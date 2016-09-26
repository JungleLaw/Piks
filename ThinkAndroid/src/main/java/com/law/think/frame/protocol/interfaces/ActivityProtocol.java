package com.law.think.frame.protocol.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;

public interface ActivityProtocol {
	public int setContentViewLayout();

	public abstract void initVariables();

	public abstract void initViews();

	public abstract void initListener();

	public abstract void loadData(@Nullable Bundle savedInstanceState);

	public abstract void destroyTask();

	public abstract int[] loadEntryAnimation();

	public abstract int[] loadExitAnimation();
}
