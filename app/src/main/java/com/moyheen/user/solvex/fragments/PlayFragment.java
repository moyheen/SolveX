package com.moyheen.user.solvex.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.moyheen.user.solvex.R;
import com.moyheen.user.solvex.logic.Utility;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

/**
 * Created by moyheen on 21-Dec-14.
 */
public class PlayFragment extends Fragment implements OnClickListener {

    Button[] buttons = new Button[14];
    int length = buttons.length;
    String buttonID;
    int resID;
    // Initialize variable that is used to count the number of times
    // a button was clicked.
    int count = 0;

    TextView score, timer, X;
    Button start_game;
    LinearLayout start_button_linear_layout, play_game_linear_layout;
    final Utility utility = new Utility();


//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_play,
                container, false);

        start_button_linear_layout =
                (LinearLayout) rootView.findViewById(R.id.start_button_linear_layout);
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

        return rootView;
    }


//    @Override
//    public void onStart() {
//        init();
//    }


    @Override
    public void onClick(View v) {
        int button_index;
        int temp_score;

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

                // Make a toast to confirm the results

                //Toast.makeText(getActivity(), "Button " + button_index + " clicked" + "Click count == " + count,
                //  Toast.LENGTH_SHORT).show();

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

                // Make a toast to confirm the results
                Toast.makeText(getActivity(), "Button " + button_index + " clicked" + "Click count == " + count,
                        Toast.LENGTH_SHORT).show();

                break;

            // If there has been a third click
            case 3:
                // Get the id of that button
                button_index = getIdOfClickedButton(0, 9, v);
                utility.setSecondNum(button_index);

                // Disable the numbered buttons
                disableButtons(0, 9);

                // Make a toast to confirm the results
                // Toast.makeText(getActivity(), "Button " + button_index + " clicked" + "Click count == " + count,
                //       Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "Your score is " + utility.getScore(),
                        Toast.LENGTH_SHORT).show();

                // Initialize count
                count = 0;

                // Calculate score
                utility.calculateScore();
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

    // Initialize game variables
    public void init() {

        utility.setScore(0);

        score.setText(String.valueOf(utility.getScore()));
        timer.setText("30");
        // Enable all the buttons
        enableButtons(0, 13);

        play_game_linear_layout.setVisibility(GONE);
        start_button_linear_layout.setVisibility(VISIBLE);
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
                timer.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                disableButtons(0, 13);
                score.setText(String.valueOf(utility.getScore()));

            }
        }.start();
    }
}