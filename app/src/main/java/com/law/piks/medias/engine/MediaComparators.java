package com.law.piks.medias.engine;

import android.annotation.TargetApi;
import android.os.Build;

import com.law.piks.medias.entity.Media;
import com.law.think.frame.utils.SDKUtils;

import java.util.Comparator;

/**
 * Created by dnld on 26/04/16.
 */

public class MediaComparators {
	private boolean ascending = true;

	public MediaComparators(boolean ascending) {
		this.ascending = ascending;
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public Comparator<Media> getDateComparator() {
		return new Comparator<Media>() {
			public int compare(Media f1, Media f2) {
				if (SDKUtils.hasKitKat())
					return ascending ? Long.compare(f1.getDateModified(), f2.getDateModified())
							: Long.compare(f2.getDateModified(), f1.getDateModified());
				if (f1.getDateModified() > f2.getDateModified()) {
					return 1;
				} else if (f1.getDateModified() == f2.getDateModified()) {
					return 0;
				} else {
					return -1;
				}
			}
		};
	}

	public Comparator<Media> getNameComparator() {
		return new Comparator<Media>() {
			public int compare(Media f1, Media f2) {
				return ascending ? f1.getPath().compareTo(f2.getPath()) : f2.getPath().compareTo(f1.getPath());

			}
		};
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	public Comparator<Media> getSizeComparator() {
		return new Comparator<Media>() {
			public int compare(Media f1, Media f2) {
				if (SDKUtils.hasKitKat())
					return ascending ? Long.compare(f1.getSize(), f2.getSize())
							: Long.compare(f2.getSize(), f1.getSize());
				if (f1.getSize() > f2.getSize()) {
					return 1;
				} else if (f1.getSize() == f2.getSize()) {
					return 0;
				} else {
					return -1;
				}
			}
		};
	}

	public Comparator<Media> getTypeComparator() {
		return new Comparator<Media>() {
			public int compare(Media f1, Media f2) {
				return ascending ? f1.getMIME().compareTo(f2.getMIME()) : f2.getMIME().compareTo(f1.getMIME());
			}
		};
	}
}
