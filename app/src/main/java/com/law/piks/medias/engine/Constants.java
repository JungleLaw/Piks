package com.law.piks.medias.engine;

public interface Constants {
	public class Gallery {
		public static final String ALL_PHOTO_ALBUM_NAME = "all";
		public static final String SCREEN_SHOTS_ALBUM_NAME = "Screenshots";
		public static final String CAMERA_ALBUM_NAME = "Camera";

		public static final class Media {
			public final class MIME {
				public static final String GIF = "gif";
				public static final String IMAGE = "image";
				public static final String VIDEO = "video";
			}

			public final class ORIENTATION {
				// region ORIENTATION VALUES
				public static final int NORMAL = 1;
				public static final int ROTATE_180 = 3;
				// rotate 90 cw to right it
				public static final int ROTATE_90 = 6;
				// rotate 270 to right it
				public static final int ROTATE_270 = 8;
			}

			public static final class TAG {
				public static final String WIDTH = "Image Width";
				public static final String HEIGHT = "Image Height";
				public static final String DATE_TAKEN = "Date/Time Original";
				public static final String MAKE = "Make";
				public static final String MODEL = "Model";
				public static final String F_NUMBER = "F-Number";
				public static final String ISO = "ISO Speed Ratings";
				public static final String EXPOSURE = "Exposure Time";
				public static final String ORIENTATION = "Orientation";
			}

			public static final class DB {
				public static final int DATABASE_VERSION = 1;
				public static final String DATABASE_NAME = "Piks";

				public static final String TABLE_ALBUMS = "albums";
				public static final String ALBUM_PATH = "path";
				public static final String ALBUM_EXCLUDED = "excluded";
				public static final String ALBUM_COVER = "cover_path";
				public static final String ALBUM_DEAFAULT_SORTMODE = "sort_mode";
				public static final String ALBUM_DEAFAULT_SORT_ASCENDING = "sort_ascending";
				public static final String ALBUM_COLUMN_COUNT = "column_count";
			}
		}
	}
}
