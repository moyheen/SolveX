package com.moyheen.user.solvex.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by moyheen on 02-Jan-15.
 */
public class DetailsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "userdetails.db";


    public DetailsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DETAILS_TABLE =
                "CREATE TABLE " + DetailsContract.DetailsEntry.TABLE_NAME + " (" +
                        DetailsContract.DetailsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        DetailsContract.DetailsEntry.COLUMN_DISPLAY_NAME + " TEXT NOT NULL, " +
                        DetailsContract.DetailsEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                        DetailsContract.DetailsEntry.COLUMN_PHOTO_URL + " TEXT NOT NULL, " +
                        DetailsContract.DetailsEntry.COLUMN_SCORE + " TEXT NOT NULL" +
                        " );";

        db.execSQL(SQL_CREATE_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DetailsContract.DetailsEntry.TABLE_NAME);
        onCreate(db);
    }
}
