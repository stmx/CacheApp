package com.stmlab.android.cacheapp.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stmlab.android.cacheapp.database.DBHelper;
import com.stmlab.android.cacheapp.database.TrefleSchema.TrefleEntry;


import static com.stmlab.android.cacheapp.database.TrefleSchema.CONTENT_AUTHORITY;
import static com.stmlab.android.cacheapp.database.TrefleSchema.PATH_TREFLE_TABLE;
import static com.stmlab.android.cacheapp.database.TrefleSchema.TrefleEntry.COLUMN_TREFLE_COMMON_NAME;
import static com.stmlab.android.cacheapp.database.TrefleSchema.TrefleEntry.CONTENT_URI;
import static com.stmlab.android.cacheapp.database.TrefleSchema.TrefleEntry.TABLE_NAME;
import static com.stmlab.android.cacheapp.database.TrefleSchema.TrefleEntry._ID;

public class DatabaseProvider extends ContentProvider {



    static final String TREFLE_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."+ CONTENT_AUTHORITY + "." + PATH_TREFLE_TABLE;
    static final String TREFLE_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."+ CONTENT_AUTHORITY + "." + PATH_TREFLE_TABLE;

    static final int TREFLES = 1;
    static final int TREFLE_ID = 2;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_TREFLE_TABLE, TREFLES);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_TREFLE_TABLE + "/#", TREFLE_ID);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;


    @Override
    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TREFLES:
                return TREFLE_CONTENT_TYPE;
            case TREFLE_ID:
                return TREFLE_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case TREFLES:
                if ( TextUtils.isEmpty(sortOrder)) {
                    sortOrder = COLUMN_TREFLE_COMMON_NAME + " ASC";
                }
                break;
            case TREFLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = TrefleEntry._ID + " = " + id;
                } else {
                    selection = selection + " AND " + TrefleEntry._ID  + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        if (uriMatcher.match(uri) != TREFLES)
            throw new IllegalArgumentException("Wrong URI: " + uri);

        db = dbHelper.getWritableDatabase();
        long rowID = db.insert(TABLE_NAME, null, values);
        Uri resultUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
        getContext().getContentResolver().notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case TREFLES:
                break;
            case TREFLE_ID:
                String id = uri.getLastPathSegment();
                if ( TextUtils.isEmpty(selection)) {
                    selection = _ID + " = " + id;
                } else {
                    selection = selection + " AND " + _ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.delete(TABLE_NAME, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case TREFLES:
                break;
            case TREFLE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = _ID + " = " + id;
                } else {
                    selection = selection + " AND " + _ID + " = " + id;
                }
                break;
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        db = dbHelper.getWritableDatabase();
        int cnt = db.update(TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }
}
