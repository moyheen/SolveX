package com.moyheen.user.solvex.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by moyheen on 02-Jan-15.
 */
public class DetailsProvider extends ContentProvider {
    private static final int DETAILS = 100;

    private static final UriMatcher sURI_MATCHER = buildUriMatcher();

    private DetailsDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DetailsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DetailsContract.PATH_DETAILS, DETAILS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DetailsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sURI_MATCHER.match(uri)) {
            case DETAILS: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DetailsContract.DetailsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case DETAILS:
                return DetailsContract.DetailsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        Uri returnUri;

        switch (match) {
            case DETAILS:
                long _id = db.insert(DetailsContract.DetailsEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = DetailsContract.DetailsEntry.buildDetailsUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        int rowsDeleted;

        switch (match) {
            case DETAILS:
                rowsDeleted = db.delete(DetailsContract.DetailsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (null == selection || 0 != rowsDeleted) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        int rowsUpdated;

        switch (match) {
            case DETAILS:
                rowsUpdated = db.delete(DetailsContract.DetailsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (0 != rowsUpdated) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sURI_MATCHER.match(uri);
        switch (match) {
            case DETAILS:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DetailsContract.DetailsEntry.TABLE_NAME, null, value);
                        if (-1 != _id) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
