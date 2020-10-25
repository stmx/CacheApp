package com.stmlab.android.cacheapp.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class TrefleSchema {

    public static final String CONTENT_AUTHORITY = "com.stmlab.android.casheapp.databaseprovider";
    public static final Uri BASE_CONTENT_AUTHORITY = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_TREFLE_TABLE = "trefles";

    public static final class TrefleEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_AUTHORITY, PATH_TREFLE_TABLE);

        public final static String TABLE_NAME = "trefles";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TREFLE_COMMON_NAME = "common_name";
        public final static String COLUMN_TREFLE_FAMILY = "family";
        public final static String COLUMN_TREFLE_YEAR = "year";
        public final static String COLUMN_TREFLE_SCIENTIFIC_NAME = "scientific_name";
        public final static String COLUMN_TREFLE_BIBLIOGRAPHY = "bibliography";
        public final static String COLUMN_TREFLE_IMAGE_URL = "image_url";
    }
}
