package com.moyheen.user.solvex.logic;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.moyheen.user.solvex.data.DetailsContract;

/**
 * Created by moyheen on 02-Jan-15.
 */
public class FetchDetailsTask {
    private Context mContext;

    public FetchDetailsTask(Context context) {
        this.mContext = context;
    }

    public long addDetails(String personName, String personEmail, String personPhotoUrl, String personScore) {

        Cursor cursor = mContext.getContentResolver().query(
                DetailsContract.DetailsEntry.CONTENT_URI,
                new String[]{DetailsContract.DetailsEntry._ID},
                DetailsContract.DetailsEntry.COLUMN_DISPLAY_NAME + " = ?",
                new String[]{personName},
                null);

        if (cursor.moveToFirst()) {
            int detailsIdIndex = cursor.getColumnIndex(DetailsContract.DetailsEntry._ID);
            return cursor.getLong(detailsIdIndex);
        } else {
            ContentValues detailsValues = new ContentValues();
            detailsValues.put(DetailsContract.DetailsEntry.COLUMN_DISPLAY_NAME, personName);
            detailsValues.put(DetailsContract.DetailsEntry.COLUMN_EMAIL, personEmail);
            detailsValues.put(DetailsContract.DetailsEntry.COLUMN_PHOTO_URL, personPhotoUrl);
            detailsValues.put(DetailsContract.DetailsEntry.COLUMN_SCORE, personScore);


            Uri detailsInsertUri = mContext.getContentResolver()
                    .insert(DetailsContract.DetailsEntry.CONTENT_URI, detailsValues);

            return ContentUris.parseId(detailsInsertUri);
        }
    }


}
