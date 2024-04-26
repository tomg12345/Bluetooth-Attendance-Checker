package com.gordon.thomas.attendanceapp;
/*
Author: Thomas Gordon
April 2021
 */
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // This is an extra: Key-Value data that can be transferred from one intent to another
    public static final String EXTRA_MESSAGE = "com.gordon.thomas.attendanceapp.MESSAGE";
    public static final String STUDENT_ID = "com.gordon.thomas.attendanceapp.STUDENT_ID";

    public TextView advertisingStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        advertisingStatus = (TextView) findViewById(R.id.advertising_status);

        BluetoothAdapter.getDefaultAdapter().enable();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("service-event"));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter("service-event"));
        super.onResume();

        if (AdvertiserService.running) {
            advertisingStatus.setText("Currently Advertising");
        } else {
            advertisingStatus.setText("Not Currently Advertising");
        }
    }

    @Override
    protected void onDestroy() {
        Intent serviceIntent = new Intent(this, AdvertiserService.class);
        stopService(serviceIntent);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    public void startAdvertising(View view) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String studentId = editText.getText().toString();

        Intent serviceIntent = new Intent(this, AdvertiserService.class);
        serviceIntent.putExtra(STUDENT_ID, studentId);
        startService(serviceIntent);

        advertisingStatus.setText("Currently Advertising");
    }

    public void stopAdvertising(View view) {
        Intent serviceIntent = new Intent(this, AdvertiserService.class);
        stopService(serviceIntent);
        advertisingStatus.setText("Not Currently Advertising");
    }

    public void displayFinishedNotification() {
        Toast.makeText(this, "All finished!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "default_channel_id";
        String channelDescription = "Default Channel";
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Sign-in finished")
                        .setContentText("Attendance sign-in has completed successfully!")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("serviceMessage");
            Log.d("BroadcastReceiver", message);

            if (message.equals(AdvertiserService.ADVERTISING_ON)) {
                advertisingStatus.setText("Currently Advertising");
            }
            else if (message.equals(AdvertiserService.ADVERTISING_OFF)) {
                advertisingStatus.setText("Not Currently Advertising");
            }
            else if (message.equals(AdvertiserService.FINISHED)) {
                advertisingStatus.setText("Not Currently Advertising");
                displayFinishedNotification();
            }
        }
    };

}
