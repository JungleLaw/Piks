package com.law.think.frame.db.query;

import com.law.think.frame.db.DataSupport;

public final class ThinkQuery<T extends DataSupport> {
	private Class<? extends DataSupport> clazz;

	private ThinkQuery(Class<? extends DataSupport> clazz) {
		this.clazz = clazz;
	}

	public static ThinkQuery<?> newThinkQuery(Class<? extends DataSupport> clazz) {
		return new ThinkQuery(clazz);
	}

	public ThinkQuery<T> where() {
		return this;
	}

}
