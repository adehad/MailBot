package com.extcord.jg3215.mailbot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity_Collection1 extends AppCompatActivity {

    // TODO: Do not forget to account (in robot) for what happens if user cancels midway
    // TODO: Check if skipping frames is a problem to do with the emulator
        // TODO: Make images smaller sizes (memory-wise)

    // The image views that are being used like buttons
    ImageView letterView;
    ImageView largeLetterView;
    ImageView parcelView;

    // Integers used to represent the type of mail that is being sent
    private static final int LETTER_STANDARD = 1;
    private static final int LETTER_LARGE = 2;
    private static final int PARCEL = 3;

    // Tag for debugging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_collection1);

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        letterView = (ImageView) findViewById(R.id.letter);
        letterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "letter View selected");
                // TODO: Communicate to the robot (via Serial) that a small letter is being delivered
                // if (robot says there is space) {
                    setDetailsIntent(LETTER_STANDARD);
                // else {
                    // inform the user that there is no space using a Toast }
            }
        });

        largeLetterView = (ImageView) findViewById(R.id.largeLetter);
        largeLetterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Large letter View selected");
                // TODO: Communicate to the robot (via Serial) that a small letter is being delivered
                // if (robot says there is space) {
                    setDetailsIntent(LETTER_LARGE);
                // else {
                    // inform the user that there is no space using a Toast }
            }
        });

        parcelView = (ImageView) findViewById(R.id.largeParcel);
        parcelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "parcel View selected");
                // TODO: Communicate to the robot (via Serial) that a small letter is being delivered
                // if (robot says there is space) {
                    setDetailsIntent(PARCEL);
                // else {
                    // inform the user that there is no space using a Toast }
            }
        });
    }

    private void setDetailsIntent(int packageType) {
        // TODO: Check that this activity is starting when you want it to (use Logging)
        String intentTag = "packageType";
        Intent detailActivityIntent = new Intent(this, DetailsActivity_Collection1.class);

        // Adds this extra detail to the intent which indicates what kind of package the user is sending
        detailActivityIntent.putExtra(intentTag, packageType);
        startActivity(detailActivityIntent);
    }
}