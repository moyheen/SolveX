package com.moyheen.user.solvex.fragments;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.PlusShare;
import com.moyheen.user.solvex.R;
import com.moyheen.user.solvex.data.DetailsContract;

import java.io.InputStream;

import static com.google.android.gms.games.Games.API;
import static com.google.android.gms.games.Games.Leaderboards;
import static com.google.android.gms.games.Games.SCOPE_GAMES;

/**
 * Created by moyheen on 01-Jan-15.
 */
public class DisplayScoreFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private String score_value;
    private long high = 0;
    private static final int DETAILS_LOADER = 0;

    private TextView score_display;
    private static final int REQUEST_LEADERBOARD = 2;

    // Request code used to invoke sign in user interaction
    private static final int RC_SIGN_IN = 0;

    // Client used to interact with Google APIs.
    private static GoogleApiClient mGoogleApiClient;

    /* A flag indicating that a PendingIntent is in progress and prevents
* us from starting further intents.
*/
    private boolean mIntentInProgress;

    /* Track whether the sign-in button has been clicked so that we know to resolve
 * all issues preventing sign-in without waiting.
 */
    private boolean mSignInClicked;

    /* Store the connection result from onConnectionFailed callbacks so that we can
     * resolve them when the user clicks sign-in.
     */
    private ConnectionResult mConnectionResult;


    private static final String[] DETAILS_COLUMNS = {
            DetailsContract.DetailsEntry.TABLE_NAME + "." + DetailsContract.DetailsEntry._ID,
            DetailsContract.DetailsEntry.COLUMN_DISPLAY_NAME,
            DetailsContract.DetailsEntry.COLUMN_PHOTO_URL,
            DetailsContract.DetailsEntry.COLUMN_SCORE
    };

       @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_display_score, container, false);

        ImageView mCircleImageView = (ImageView) rootView.findViewById(R.id.display_score_image);
        score_display = (TextView) rootView.findViewById(R.id.score_display);
        TextView high_score = (TextView) rootView.findViewById(R.id.high_score);
        Button play_again = (Button) rootView.findViewById(R.id.play_again);
        Button view_leaderboard = (Button) rootView.findViewById(R.id.view_leaderboard);
        Button share_button = (Button) rootView.findViewById(R.id.share_button);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(API)
                .addScope(SCOPE_GAMES)
                .build();

        // Set the visibility of the high score text to null
        high_score.setVisibility(View.GONE);

        // Get the new score
        Bundle bundle = this.getArguments();
           int defaultValue = bundle.getInt(getString(R.string.score));

           SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Load the profile image
        new LoadProfileImage(mCircleImageView).execute(preferences.getString("personPhoto", ""));

        // Check if there is a value stored in the shared provider. If not, store 0
        if (String.valueOf(preferences.getInt(getString(R.string.score), defaultValue)).equals(null)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getString(R.string.score), 0);
            editor.apply();
        } else {
            high = preferences.getInt(getString(R.string.score), defaultValue);
        }

        // If the new score is greater than the last score, it becomes the high score
        if (defaultValue > high) {
            high_score.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getString(R.string.score), defaultValue);
            editor.apply();
        }

        score_value = String.valueOf(defaultValue);
        score_display.setText(score_value);

        play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new PlayFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        view_leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.content_frame, new LeaderboardFragment());
//                transaction.addToBackStack(null);
//                transaction.commit();


                startActivityForResult(Leaderboards.getLeaderboardIntent(
                                mGoogleApiClient, getString(R.string.LEADERBOARD_ID)),
                        REQUEST_LEADERBOARD);
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new PlusShare.Builder(getActivity())
                        .setType("text/plain")
                        .setText("I just scored " + score_value + " in SolveX! " +
                                "Beat me if you can!")
                        .setContentUrl(Uri.parse("https://moyheen.blogspot.com"))
                        .getIntent();

                startActivityForResult(shareIntent, 0);

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);

        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
        }

    }

    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                getActivity().startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(),
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != getActivity().RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri baseUri = DetailsContract.DetailsEntry.CONTENT_URI;

        // SELECT * FROM TABLE WHERE ID = (SELECT MAX(ID) FROM TABLE);
        String selectionClause = DetailsContract.DetailsEntry._ID + " =?";

        String[] selectionArgs = {"MAX(" + DetailsContract.DetailsEntry._ID + ")"};

        return new CursorLoader(
                getActivity(),
                baseUri,
                DETAILS_COLUMNS,
                selectionClause,
                selectionArgs,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        String data_score = cursor.getString(cursor.getColumnIndex(DetailsContract.DetailsEntry.COLUMN_SCORE));
        score_display.setText(data_score);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    /**
     * Background Async task to load user profile picture from url
     */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
