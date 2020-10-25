package com.stmlab.android.cacheapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.stmlab.android.cacheapp.models.TrefleModel;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    static final String DB_NAME = "mydb";
    static final int DB_VERSION = 1;

    static final String DB_CREATE = "create table " + TrefleSchema.TrefleEntry.TABLE_NAME + "("
            + TrefleSchema.TrefleEntry._ID + " integer primary key , "
            + TrefleSchema.TrefleEntry.COLUMN_TREFLE_COMMON_NAME + " text, "
            + TrefleSchema.TrefleEntry.COLUMN_TREFLE_YEAR + " integer, "
            + TrefleSchema.TrefleEntry.COLUMN_TREFLE_SCIENTIFIC_NAME + " text, "
            + TrefleSchema.TrefleEntry.COLUMN_TREFLE_BIBLIOGRAPHY + " text, "
            + TrefleSchema.TrefleEntry.COLUMN_TREFLE_IMAGE_URL + " text, "
            + TrefleSchema.TrefleEntry.COLUMN_TREFLE_FAMILY + " text" + ");";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DB_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public static ContentValues getContentValue(TrefleModel model) {
        ContentValues values = new ContentValues();
        values.put(TrefleSchema.TrefleEntry._ID, model.getId());
        values.put(TrefleSchema.TrefleEntry.COLUMN_TREFLE_YEAR, model.getYear());
        values.put(TrefleSchema.TrefleEntry.COLUMN_TREFLE_COMMON_NAME, model.getCommonName());
        values.put(TrefleSchema.TrefleEntry.COLUMN_TREFLE_FAMILY, model.getFamily());
        values.put(TrefleSchema.TrefleEntry.COLUMN_TREFLE_BIBLIOGRAPHY, model.getBibliography());
        values.put(TrefleSchema.TrefleEntry.COLUMN_TREFLE_SCIENTIFIC_NAME, model.getScientificName());
        values.put(TrefleSchema.TrefleEntry.COLUMN_TREFLE_IMAGE_URL, model.getImageUrl());
        return values;
    }

    public static ArrayList<TrefleModel> cursorToModelList(Cursor cursor) {
        ArrayList<TrefleModel> trefleList = new ArrayList<>();
        TrefleCursorWrapper trefleCursorWrapper = new TrefleCursorWrapper(cursor);
        trefleCursorWrapper.moveToFirst();
        while(!trefleCursorWrapper.isAfterLast()) {
            Log.d("Cursor", trefleCursorWrapper.getTrefle().getCommonName());
            trefleList.add(trefleCursorWrapper.getTrefle());
            trefleCursorWrapper.moveToNext();
        }
        trefleCursorWrapper.close();
        return trefleList;
    }
}
