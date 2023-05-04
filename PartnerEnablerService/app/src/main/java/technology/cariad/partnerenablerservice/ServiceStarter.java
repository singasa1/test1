package technology.cariad.partnerenablerservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class ServiceStarter extends BroadcastReceiver {
    private static final String TAG = ServiceStarter.class.getSimpleName();
    private static final String ACTION_ENABLER = "technology.cariad.partnerenablerservice.enabler";
    private static final String PACKAGE_NAME = "technology.cariad.partnerenablerservice";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(ACTION_ENABLER).setPackage(PACKAGE_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG,"Calling startForeground Service");
            context.startForegroundService(i);
        } else {
            Log.d(TAG,"Calling StartService");
            context.startService(i);
        }
        Log.i(TAG, "onReceive Done");
    }
}
