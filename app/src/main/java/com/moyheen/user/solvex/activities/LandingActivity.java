package com.moyheen.user.solvex.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.moyheen.user.solvex.R;
import com.moyheen.user.solvex.logic.UserDetails;
import com.splunk.mint.Mint;


public class LandingActivity extends Activity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private static final int PROFILE_PIC_DIMEN = 400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Adding bugsense to track errors
        Mint.initAndStartSession(LandingActivity.this, getString(R.string.bugsense_api_key));

        setContentView(R.layout.activity_landing);

        //Button tour_button = (Button) findViewById(R.id.tour_button);

//        tour_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(LandingActivity.this, MainActivity.class);
//                startActivity(i);
//                finish();
//            }
//        });

        SignInButton sign_in_button = (SignInButton) findViewById(R.id.landing_button);
        sign_in_button.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Method to resolve any signin errors
     */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = connectionResult;

            if (mSignInClicked) {
                // The user has already clicked 'sign-in' so we attempt to
                // resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }

        // Put a dialog here
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
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
        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        getProfileInformation();

        Intent i = new Intent(LandingActivity.this, MainActivity.class);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.landing_button) {
            if (!mGoogleApiClient.isConnecting()) {
                mSignInClicked = true;
                resolveSignInError();
            }
        }
    }

    private void getProfileInformation() {
        UserDetails userDetails = new UserDetails();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();


        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                editor.putString("personName", personName);

                String personPhotoUrl = currentPerson.getImage().getUrl();
                personPhotoUrl = personPhotoUrl.substring(0, personPhotoUrl
                        .length() - 2) + PROFILE_PIC_DIMEN;
                editor.putString("personPhoto", personPhotoUrl);

                String personEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
                editor.putString("personEmail", personEmail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

}

