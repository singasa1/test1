package technology.cariad.partnerenablerservice;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.volkswagenag.ignite.appstore.IAppStoreService;
import com.volkswagenag.ignite.appstore.AppStoreUpdateContainer;
import com.volkswagenag.ignite.appstore.IAppStoreUpdateListener;
import com.volkswagenag.partnerlibrary.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

@Singleton
public class AppUpdateManager {
    private static final String TAG = AppUpdateManager.class.getSimpleName();
    private static final String PARTNER_API_SERVICE_PACKAGE_NAME = "technology.cariad.partnerenablerservice";

    private IAppStoreService mAppStoreService;
    private final ScheduledExecutorService scheduledExecutorService;
    private String appStoreVersion;
    private final ServiceConnection appStoreServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAppStoreService = IAppStoreService.Stub.asInterface(service);
            Log.w(TAG, "onServiceConnected to " + name);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w(TAG, "Service unexpectedly disconnected.");
            mAppStoreService = null;
        }
    };
    private final Context mContext;
    public AppUpdateManager(Context context) {
        mContext = context;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        appStoreVersion = null;
    }

    public void initialize() {
        try {
            Intent appstoreIntent = new Intent();
            appstoreIntent.setClassName(getAppStorePackageName(mContext.getApplicationContext()),
                    "com.harman.ignite.appstore.services.AppStoreService");
            boolean bindResult = mContext.bindService(appstoreIntent, appStoreServiceConnection, Context.BIND_AUTO_CREATE);
            Log.e(TAG, "AppStoreService bindResult: " + bindResult);
        } catch (SecurityException e) {
            Log.e(TAG, "Could not bind to service. (AppStoreService)" + e.getLocalizedMessage());
        }
    }

    public void release() {
        if (mAppStoreService != null) {
            mContext.unbindService(appStoreServiceConnection);
            mAppStoreService = null;
        }
    }

    public void checkAndInitiateUpdatesIfNeeded() {
            if (mAppStoreService != null) {
                try {
                    mAppStoreService.getAvailableUpdates(new IAppStoreUpdateListener.Stub() {
                        @Override
                        public void getUpdateCallback(List<AppStoreUpdateContainer> updates, int errorCode) {
                            Log.e(TAG, "errorCode: " + errorCode);
                            if(updates != null && updates.size() > 0) {
                                for (AppStoreUpdateContainer update : updates) {
                                    Log.e(TAG, "package: " + update.packageName + " version: " + update.versionName);
                                    if (update.packageName.equals(PARTNER_API_SERVICE_PACKAGE_NAME)) {
                                        compareVersionAndInitiateUpdate(update.versionName);
                                    }
                                }
                            }
                        }
                    });
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

    private void compareVersionAndInitiateUpdate(AppStoreUpdateContainer update) {
        // if current version is not available get the version
        String currentVersion = getCurrentVersionPES();
        appStoreVersion = update.versionName;
        // compare versions and show dialog
        if (currentVersion == null || currentVersion.isEmpty() ||
            update.versionName == null || update.versionName.isEmpty()) {
            return;
        }

        String[] currentVersionNumbers = currentVersion.split(".");
        String[] appStoreVersionNumbers = update.versionName.split(".");
        int minLen = currentVersionNumbers.length < appStoreVersionNumbers.length ? currentVersionNumbers.length : appStoreVersionNumbers.length;
        boolean needsUpdate = true;
        boolean equal = true;


        for (int i = 0; i < minLen; i++) {
            if (Integer.parseInt(currentVersionNumbers[i]) > Integer.parseInt(appStoreVersionNumbers[i])) {
                needsUpdate = false;
                equal = false;
            }
        }

        if (equal && appStoreVersionNumbers.length > currentVersionNumbers.length) {
            // appstore version has more digits hence set needs update to true
            needsUpdate = true;
        }
        
        if (needsUpdate) {
            showDialog(update);
        }
    }

    private void showDialog(AppStoreUpdateContainer update) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        builder.setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        // productid is the package name
                        String partnerAPIDeepLink = "ignitemarket://screen?screenName=details&productId=" + PARTNER_API_SERVICE_PACKAGE_NAME;
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(partnerAPIDeepLink)));
                        // Scheduled executor service to check the status.
                        scheduledExecutorService.scheduleWithFixedDelay(new UpdateCheckerTask(), 0, 30, TimeUnit.SECONDS);
                    }
                })
                .setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String getCurrentVersionPES() {
        String version = null;
        try {
            mContext.getPackageManager().getApplicationInfo(PARTNER_API_SERVICE_PACKAGE_NAME, q0);
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(PARTNER_API_SERVICE_PACKAGE_NAME, 0);
            Log.i(TAG,"PackageVersionName: " + packageInfo.versionName + ",versionCode; " + packageInfo.getLongVersionCode());
            version = packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            // it should not come here as app installed will be checked upon initialization
            e.printStackTrace();
        }
        return version;
    }

    public String getAppStorePackageName(Context context) {
        PackageManager pm = context.getPackageManager();
        String appstoreBasePackage = "com.harman.ignite.appstore";
        for (PackageInfo packageInfo : pm.getInstalledPackages(0)) {
            if (packageInfo.packageName.startsWith(appstoreBasePackage)) {
                return packageInfo.packageName;
            }
        }
        return null;
    }

    class UpdateCheckerTask implements Runnable {
        public UpdateCheckerTask() { }
        public void run()
        {
            if (appStoreVersion == null || appStoreVersion.isEmpty()) {
                scheduledExecutorService.shutdown();
            }
            // TODO: check logcat for failure logs here and handle failure.
            if (getCurrentVersionPES().equals(appStoreVersion)) {
                scheduledExecutorService.shutdown();
            }
        }
    }
}
