package com.dito.mhanif.mylocationtracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class TrackingService extends Service {
    private String TAG = Service.class.getSimpleName();

    public TrackingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        buildNotification();
        loginToFirebase();
    }

    private void buildNotification() {
        String action_stop = "stop";



        registerReceiver(stopReceiver, new IntentFilter(action_stop));

        PendingIntent broadcastStopIntent = PendingIntent.getBroadcast(this,
                0, new Intent(action_stop), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String NOTI_CHANNEL_ID = "Tracking_Channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String NOTI_CHANNEL_NAME = "Tracking_Channel";
            NotificationChannel channel = new NotificationChannel(NOTI_CHANNEL_ID, NOTI_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("This is used to indicate the tracking");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTI_CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                .setOngoing(true)
                .setContentIntent(broadcastStopIntent)
                .setSmallIcon(R.drawable.tracking_enabled);

        startForeground(1, builder.build());
    }

    private void loginToFirebase() {
        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            requestLocationUpdates();
                        } else {
                            Toast.makeText(TrackingService.this, "Firebase auth failed!", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Firebase auth failed msg=" + task.getException().getMessage());
                        }
                    }
                });
    }

    private void requestLocationUpdates() {
        LocationRequest req = new LocationRequest();
        req.setInterval(10000); //10 seconds

        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        String path = getString(R.string.firebase_path);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(req, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference dbRefernce = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        dbRefernce.setValue(location);
                    }

                }
            }, null);
        }

    }


    BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(stopReceiver);
            Log.d(TAG, "Stopping the Tracking service.");
            stopSelf();
        }
    };
}
