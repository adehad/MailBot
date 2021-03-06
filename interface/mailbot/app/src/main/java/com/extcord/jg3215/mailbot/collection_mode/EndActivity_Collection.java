package com.extcord.jg3215.mailbot.collection_mode;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.extcord.jg3215.mailbot.LockerManager;
import com.extcord.jg3215.mailbot.PackageData;
import com.extcord.jg3215.mailbot.R;
import com.extcord.jg3215.mailbot.email.eMailService;

/**
 * NAME:        EndActivity_Collection.java
 * PURPOSE:     If the locker is not full, the application will start this activity after LockerActivity
 *              given the mail item fits in the locker. Here, the user can end their interaction with
 *              MailBot using the returnButton or send another mail item to the same recipient or
 *              another using the reDeliverButton. The reDeliverButton retrieves a fragment activity
 *              that allows the user to select a mail item size as with MainActivity
 *
 * AUTHORS:     Ifeanyi Chinweze, Javi Geis
 * NOTES:       Runs the EndActivityDialogFragment on top of EndActivity
 * REVISION:    13/12/2018
 */

public class EndActivity_Collection extends FragmentActivity implements EndActivityDialogFragment.EndActivityDialogListener {

    private static final String TAG = "EndActivity";

    // Integers used to represent the type of mail that is being sent
    /* private static final int LETTER_STANDARD = 1;
    private static final int LETTER_LARGE = 2;
    private static final int PARCEL = 3; */

    // Incoming extras
    private PackageData senderData;
    private PackageData recipientData;
    private String pinCode;

    // Extra but might be changed if user opts to send another mail item
    private int packageType;

    Button reDeliverButton;
    Button returnButton;

    private LockerManager mLockerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_activity_end);
        hideNavigationBar();

        // Get data from previous activity stored in a bundle
        Bundle lockerActivityData = this.getIntent().getExtras();

        mLockerManager = new LockerManager(this);

        if (lockerActivityData != null) {
            /* try {
                packageType = lockerActivityData.getInt("packageType");
                Log.i(TAG, "Package Type: " + String.valueOf(packageType));
            } catch (NullPointerException e) {
                Log.i(TAG, "Error in communicating integer data from LockerActivity: " + e.getMessage());
            } */

            try {
                senderData = lockerActivityData.getParcelable("senderData");
                if (senderData != null) {
                    Log.i(TAG, "Sender data: User Name: " + senderData.getName() + ", User Email: " + senderData.getEmailAddress());
                }
            } catch (NullPointerException e) {
                Log.i(TAG, "No sender data received: " + e.getMessage());
            }

            try {
                recipientData = lockerActivityData.getParcelable("recipientData");
                if (recipientData != null) {
                    Log.i(TAG, " Recipient data: Recipient Name: " + recipientData.getName() + ", Recipient Email: " + recipientData.getEmailAddress() + ", Recipient Location: " + recipientData.getDeliveryLocation());
                }
            } catch (NullPointerException e) {
                Log.i(TAG, "No recipient data received: " + e.getMessage());
            }

            try {
                pinCode = lockerActivityData.getString("pinCode");
                if (pinCode != null) {
                    Log.i(TAG, "PIN data: " + pinCode);
                }
            } catch (NullPointerException e) {
                Log.i(TAG, "No PIN data received: " + e.getMessage());
            }

        } else {
            // No intent data received from the previous activity
            throw new NullPointerException("No data sent from previous activity");
        }

        sendEmails();

        reDeliverButton = (Button) findViewById(R.id.btnToSameRecipient);
        reDeliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Empty the package type variable so it can be reset
                packageType = 0;

                // Create the popup dialog (fragment activity)
                showDialog();
            }
        });

        returnButton = (Button) findViewById(R.id.btnStartAgain);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                senderData = null;
                recipientData = null;
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideNavigationBar();
    }

    private void hideNavigationBar() {
        Log.i(TAG, "hideNavigationBar() method called");
        this.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );
    }

    // Produces the dialogFragment on the screen
    // IT WORKS HOW YOU EXPECTED YAY
    public void showDialog() {
        // Create an instance of the dialog fragment and show it
        Log.i(TAG, "showDialog() method called");
        DialogFragment dialogFragment = new EndActivityDialogFragment();
        dialogFragment.show(getFragmentManager(), "EndActivityDialogFragment");
    }

    // The dialog fragment receives a reference to this Activity through the Fragment.onAttach()
    // It uses the reference to call the following methods defined by the EndActivityDialogFragment listener interface
    @Override
    public void onSmallLockerSelect(DialogFragment dialogFragment) {
        Log.i(TAG, "Small locker selected to send to the same recipient");

        int aLockers = mLockerManager.getAvailability(LockerManager.LETTER_STANDARD);
        if (aLockers > 0) {
            // The dialog fragment is dismissed in the redoDetailsActivity() method
            // Change the packageType variable to the selected packageType
            packageType = LockerManager.LETTER_STANDARD;
            redoDetailsActivity(dialogFragment);
        } else {
            Toast.makeText(EndActivity_Collection.this, getResources().getString(R.string.lockerSizeUnavailable), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMediumLockerSelect(DialogFragment dialogFragment) {
        Log.i(TAG, "Medium locker selected to send to the same recipient");

        int aLockers = mLockerManager.getAvailability(LockerManager.LETTER_LARGE);
        if (aLockers > 0) {
            packageType = LockerManager.LETTER_LARGE;
            redoDetailsActivity(dialogFragment);
        } else {
            Toast.makeText(EndActivity_Collection.this, getResources().getString(R.string.lockerSizeUnavailable), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLargeLockerSelect(DialogFragment dialogFragment) {
        Log.i(TAG, "Large locker selected to send to the same recipient");

        int aLockers = mLockerManager.getAvailability(LockerManager.PARCEL);
        if (aLockers > 0) {
            packageType = LockerManager.PARCEL;
            redoDetailsActivity(dialogFragment);
        } else {
            Toast.makeText(EndActivity_Collection.this, getResources().getString(R.string.lockerSizeUnavailable), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {
        Log.i(TAG, "Dialog negative button clicked");

        // Finish the activity
        dialogFragment.dismiss();
        finish();
    }

    private void redoDetailsActivity(DialogFragment dialogFragment) {
        Log.i(TAG, "redoDetailsActivity() method called");

        String packageTag = "packageType";
        String senderDataTag = "senderData";
        String recipientDataTag = "recipientData";
        String dataProvidedTag = "dataProvided";

        Intent redoDetailsActivityIntent = new Intent(this, DetailsActivity_Collection.class);

        // Create a bundle for holding the extras
        Bundle extras = new Bundle();

        // Adds this extra detail to the intent which indicates:
            // The kind of package the user is sending
            // The data given to MailBot about the sender
            // The data given to MailBot about the recipient
        extras.putInt(packageTag, packageType);
        extras.putParcelable(senderDataTag, senderData);
        extras.putParcelable(recipientDataTag, recipientData);

        // Boolean represents whether or not data has been provided by a previous activity
        extras.putBoolean(dataProvidedTag, true);
        Log.i(TAG, "Extras added to bundle");

        // Add all the extras content to the intent
        redoDetailsActivityIntent.putExtras(extras);

        startActivity(redoDetailsActivityIntent);
        Log.i(TAG, "To Locker Activity with the extras: " + packageTag + ", " + senderDataTag + ", " + recipientDataTag);
        dialogFragment.dismiss();
        finish();
    }

    private void sendEmails() {
        Log.i(TAG, "sendEmails() method called");

        String recipientEmail = senderData.getEmailAddress();
        // send email to recipient

        final ProgressDialog dialog = new ProgressDialog(EndActivity_Collection.this);
        dialog.setTitle("Sending Confirmation Emails");
        dialog.setMessage("Please wait");
        dialog.show();

        final Thread recipient = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eMailService sender = new eMailService("mailbot.noreply18@gmail.com", "mailbotHCR");
                    sender.sendMail("Someone gave me a mail item for you!",
                            "Dear " + recipientData.getName() + "\n\nI am MailBot and I have received a mail item for you"
                            + " from " + senderData.getName() + ". It will be delivered to "
                            + recipientData.getDeliveryLocation() + " within the next half hour to your"
                            + " office. Your password for collecting it will be:\n\n" + pinCode + "\n\nReme"
                            + "mber the password - it will be needed to open the locker containing your mai"
                            + "l.\n\nSee you soon!\n\nMailBot",
                            senderData.getEmailAddress(),
                            recipientData.getEmailAddress());
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, "Error: " + e.getMessage());
                }
            }
        });
        recipient.start();

        //
        Log.i(TAG, "Email sent to: " + recipientEmail);

        String senderEmail = recipientData.getEmailAddress();

        // send email to sender

        final ProgressDialog dialog2 = new ProgressDialog(EndActivity_Collection.this);
        dialog2.setTitle("Sending Confirmation Emails");
        dialog2.setMessage("Please wait");
        dialog2.show();

        Thread sender = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eMailService sender = new eMailService("mailbot.noreply18@gmail.com", "mailbotHCR");
                    sender.sendMail("Confirmation: You asked me to deliver a mail item to " + recipientData.getName(),
                            "Hey " + senderData.getName() + ",\n\nThis is Mailbot! I'm just messaging you to conf"
                            + "irm you have given me a mail item that you want me to deliver to " +
                            recipientData.getName() + ". I will have this delivered to " + recipientData.getDeliveryLocation() +
                            " as soon as I can!\n\nI will let you know if it goes well," + "\n\nMailBot",
                            recipientData.getEmailAddress(),
                            senderData.getEmailAddress());
                    dialog2.dismiss();
                } catch (Exception e) {
                    Log.e("mylog", "Error: " + e.getMessage());
                }
            }
        });
        sender.start();

        //
        Log.i(TAG, "Email sent to: " + senderEmail);
    }

    protected void onDestroy() {
        if (mLockerManager != null) {
            mLockerManager.unregisterListener();
        }
        super.onDestroy();
    }
}
