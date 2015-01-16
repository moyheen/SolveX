package com.moyheen.user.solvex.fragments;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.moyheen.user.solvex.R;
import com.moyheen.user.solvex.logic.FetchDetailsTask;
import com.moyheen.user.solvex.logic.Utility;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

/**
 * Created by moyheen on 21-Dec-14.
 */
public class PlayFragment extends Fragment implements OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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

    private SharedPreferences sharedPreferences;

    Button[] buttons = new Button[14];
    int length = buttons.length;
    String buttonID;
    int resID;
    // Initialize variable that is used to count the number of times
    // a button was clicked.
    int count = 0;

    TextView score;
    TextView timer;
    TextView X;
    Button start_game;
    ScrollView start_button_linear_layout;
    LinearLayout play_game_linear_layout;
    final Utility utility = new Utility();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play,
                container, false);

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .build();

        start_button_linear_layout =
                (ScrollView) rootView.findViewById(R.id.start_button_linear_layout);
        play_game_linear_layout =
                (LinearLayout) rootView.findViewById(R.id.play_game_linear_layout);

        // The start button
        start_game = (Button) rootView.findViewById(R.id.start_game);

        // The dynamic score and timer text
        score = (TextView) rootView.findViewById(R.id.score);
        timer = (TextView) rootView.findViewById(R.id.timer);

        // The changing X
        X = (TextView) rootView.findViewById(R.id.X);

        // Loop through the 14 GridLayout buttons to initialize them
        for (int index = 0; index < length; index++) {
            buttonID = "button" + (index + 1);

            resID = getResources().getIdentifier(buttonID, "id", getActivity()
                    .getPackageName());
            buttons[index] = ((Button) rootView.findViewById(resID));
            buttons[index].setOnClickListener(this);

        }
        // Initialize the game variables
        init();

        start_game.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
        }

        return rootView;
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
    public void onClick(View v) {

        int button_index;

        // Increase count
        ++count;

        switch (count) {

            // If there has been a first click
            case 1:
                // Get the id of the clicked button
                // The numbered buttons are labelled 1 - 10
                button_index = getIdOfClickedButton(0, 9, v);

                utility.setFirstNum(button_index);

                // Disable the numbered buttons 1 - 10
                disableButtons(0, 9);

                // Enable the signed buttons
                // The signed buttons are labelled 11 -  14
                enableButtons(10, 13);

                break;

            // If there has been a second click
            case 2:
                // Get the id of the clicked button
                // The clicked buttons are labelled 11 - 14
                button_index = getIdOfClickedButton(10, 13, v);
                utility.setSign(button_index);

                // Disable the signed buttons
                // The signed buttons are labelled 11 -  14
                disableButtons(10, 13);

                // Enable the numbered buttons
                enableButtons(0, 9);

                break;

            // If there has been a third click
            case 3:
                // Get the id of that button
                button_index = getIdOfClickedButton(0, 9, v);
                utility.setSecondNum(button_index);

                // Calculate score
                utility.calculateScore();

                // Disable the numbered buttons
                disableButtons(0, 9);

                // Initialize count
                count = 0;

                score.setText(String.valueOf(utility.getScore()));
                X.setText(String.valueOf(utility.generateRandom(1, 20)));
                enableButtons(0, 9);
                break;
        }
    }

    // Return the ID of the clicked button in the view
    private int getIdOfClickedButton(int start, int end, View view) {
        int button_index = 0;

        for (int index = start; index <= end; index++) {
            if (buttons[index].getId() == view.getId()) {
                button_index = index;
                break;
            }
        }

        return button_index;
    }

    // Disable a range of button items
    private void disableButtons(int start, int end) {

        for (int index = start; index <= end; index++) {
            buttons[index].setClickable(false);
        }
    }

    // Allow a range of buttons to be clickable
    private void enableButtons(int start, int end) {

        for (int index = start; index <= end; index++) {
            buttons[index].setClickable(true);
        }
    }

    public void play() {
        start_button_linear_layout.setVisibility(View.GONE);
        play_game_linear_layout.setVisibility(View.VISIBLE);

        disableButtons(10, 13);

        X.setText(String.valueOf(utility.generateRandom(1, 20)));

        //When timer ends, display toast with score for now.
        new CountDownTimer(30000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {
                if ((millisUntilFinished / 1000) < 10) {
                    timer.setText("00:0" + String.valueOf(millisUntilFinished / 1000));
                } else {
                    timer.setText("00:" + String.valueOf(millisUntilFinished / 1000));
                }
            }

            @Override
            public void onFinish() {
                if (!isAdded()) {
                    return;
                }

                if (mGoogleApiClient != null) {
                    Games.Leaderboards.submitScore(mGoogleApiClient, getString(R.string.LEADERBOARD_ID), utility.getScore());
                }

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                String name = sharedPreferences.getString("personName", "");
                String email = sharedPreferences.getString("personEmail", "");
                String photo = sharedPreferences.getString("personPhoto", "");
                String score = String.valueOf(utility.getScore());

                FetchDetailsTask mFetchDetailsTask = new FetchDetailsTask(getActivity());
                mFetchDetailsTask.addDetails(name, email, photo, score);

                Fragment fragment = new DisplayScoreFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(getString(R.string.score), utility.getScore());

                fragment.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
            }
        }.start();
    }

    // Initialize game variables
    public void init() {

        utility.setScore(0);

        score.setText(String.valueOf(utility.getScore()));
        timer.setText(R.string.timer);
        // Enable all the buttons
        enableButtons(0, 13);

        play_game_linear_layout.setVisibility(GONE);
        start_button_linear_layout.setVisibility(VISIBLE);
    }

}