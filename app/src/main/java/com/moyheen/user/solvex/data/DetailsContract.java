package com.moyheen.user.solvex.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by moyheen on 02-Jan-15.
 */
public class DetailsContract {

    public static final String CONTENT_AUTHORITY = "com.moyheen.user.solvex";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" +
            CONTENT_AUTHORITY);

    public static final String PATH_DETAILS = "details";

    public static final class DetailsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DETAILS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" +
                        PATH_DETAILS;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" +
                        PATH_DETAILS;

        public static final String TABLE_NAME = "details";

        // The unique ID for a row
        public static final String _ID = "_id";

        // The count of rows in a directory
        public static final String _COUNT = "_count";

        // The display name for the particular score
        public static final String COLUMN_DISPLAY_NAME = "name";

        // The score for that data set
        public static final String COLUMN_EMAIL = "email";

        // The photo url for the particular score
        public static final String COLUMN_PHOTO_URL = "photo";

        // The score for that data set
        public static final String COLUMN_SCORE = "score";

        public static Uri buildDetailsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildDetails(String details) {
            return CONTENT_URI.buildUpon().appendPath(details).build();
        }

    }
}
