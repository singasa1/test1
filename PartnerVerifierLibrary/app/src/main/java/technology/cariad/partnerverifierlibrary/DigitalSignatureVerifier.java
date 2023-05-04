package technology.cariad.partnerverifierlibrary;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class DigitalSignatureVerifier extends Service {
    private static final String TAG = DigitalSignatureVerifier.class.getSimpleName();

    // declaring SignatureVerifierImpl binder instance
    private SignatureVerifierImpl mService;

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        init();
        super.onCreate();
    }

    @Override
    // execution of the service will
    // stop on calling this method
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        release();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand Service started.");
        return START_STICKY;
    }

    private void init() {
        if (mService == null) {
            mService = new SignatureVerifierImpl(this);
        }
    }

    private void release() {
        if (mService != null) {
            mService = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }
}
