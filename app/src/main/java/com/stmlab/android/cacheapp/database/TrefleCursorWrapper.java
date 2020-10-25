package com.stmlab.android.cacheapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.stmlab.android.cacheapp.models.TrefleModel;

public class TrefleCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public TrefleCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TrefleModel getTrefle() {
        int id = getInt(getColumnIndex(TrefleSchema.TrefleEntry._ID));
        int year = getInt(getColumnIndex(TrefleSchema.TrefleEntry.COLUMN_TREFLE_YEAR));
        String commonName = getString(getColumnIndex(TrefleSchema.TrefleEntry.COLUMN_TREFLE_COMMON_NAME));
        String scientificName = getString(getColumnIndex(TrefleSchema.TrefleEntry.COLUMN_TREFLE_SCIENTIFIC_NAME));
        String bibliography = getString(getColumnIndex(TrefleSchema.TrefleEntry.COLUMN_TREFLE_BIBLIOGRAPHY));
        String family = getString(getColumnIndex(TrefleSchema.TrefleEntry.COLUMN_TREFLE_FAMILY));
        String imageURL = getString(getColumnIndex(TrefleSchema.TrefleEntry.COLUMN_TREFLE_IMAGE_URL));
        TrefleModel model = new TrefleModel();
        model.setId(id);
        model.setYear(year);
        model.setCommonName(commonName);
        model.setScientificName(scientificName);
        model.setBibliography(bibliography);
        model.setFamily(family);
        model.setImageUrl(imageURL);
        return model;
    }
}
