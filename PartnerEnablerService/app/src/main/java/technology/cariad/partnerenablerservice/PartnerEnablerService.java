package technology.cariad.partnerenablerservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class PartnerEnablerService extends Service {
    private static final String TAG = PartnerEnablerService.class.getSimpleName();

    private static final String ANDROID_CHANNEL_ID = "EnablerServiceChannel";
    private static final int NOTIFICATION_ID = 555;

    // declaring PartnerEnabler binder instance
    private PartnerEnablerImpl mService;

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");

        super.onCreate();
//        startforeground();
    }

    @Override
    // execution of the service will
    // stop on calling this method
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        mService.release();
        release();
        super.onDestroy();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            stopForeground(true);
//        } else {
//            stopSelf();
//        }
    }

      @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand Service started.");
        init();
//        startforeground();
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private void init() {
        if (mService == null) {
            mService = new PartnerEnablerImpl(this);
        }
    }

    private void release() {
        if (mService != null) {
            mService = null;
        }
    }
    private void startforeground() {
        Log.d(TAG,"Startforeground private function");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(ANDROID_CHANNEL_ID, "Background Service", NotificationManager.IMPORTANCE_NONE);
            notificationChannel.enableLights(false);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(notificationChannel);
            Notification.Builder builder = new Notification.Builder(this, ANDROID_CHANNEL_ID)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("PartnerEnablerService Running")
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);
        }  else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentTitle(getString(R.string.app_name))
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("PartnerEnablerService is Running, set default priority...")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            startForeground(NOTIFICATION_ID, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        init();
        return mService;
    }
}
