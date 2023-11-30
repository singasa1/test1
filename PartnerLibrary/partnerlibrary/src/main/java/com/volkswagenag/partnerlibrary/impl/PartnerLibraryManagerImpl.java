/*
 * CONFIDENTIAL CARIAD Estonia AS
 *
 * (c) 2023 CARIAD Estonia AS, All rights reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of CARIAD Estonia AS (registry code 14945253).
 * The intellectual and technical concepts contained herein are proprietary to CARIAD Estonia AS. and may be covered by
 * patents, patents in process, and are protected by trade secret or copyright law.
 * Usage or dissemination of this information or reproduction of this material is strictly forbidden unless prior
 * written permission is obtained from CARIAD Estonia AS.
 * The copyright notice above does not evidence any actual or intended publication or disclosure of this source code,
 * which includes information that is confidential and/or proprietary, and is a trade secret of CARIAD Estonia AS.
 * Any reproduction, modification, distribution, public performance, or public display of or through use of this source
 * code without the prior written consent of CARIAD Estonia AS is strictly prohibited and in violation of applicable
 * laws and international treaties. The receipt or possession of this source code and/ or related information does not
 * convey or imply any rights to reproduce, disclose or distribute its contents or to manufacture, use or sell anything
 * that it may describe in whole or in part.
 */
package com.volkswagenag.partnerlibrary.impl;

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

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.PartnerLibraryManager;

import technology.cariad.partnerenablerservice.IPartnerEnabler;
import com.volkswagenag.partnerlibrary.ILibStateChangeListener;
import com.volkswagenag.partnerlibrary.R;
import com.volkswagenag.partnerlibrary.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Partner Library</h1>
 * Partner Library Impl provides implementation for PartnerLibraryManager wrapper apis for different app developers.
 *
 * @author CARIAD Inc
 * @version 1.0
 * @since 2023-04-20
 */
public class PartnerLibraryManagerImpl implements PartnerLibraryManager {
    private static final String TAG = PartnerLibraryManagerImpl.class.getSimpleName();

    private IPartnerEnabler mService;
    private PartnerEnablerServiceConnection mServiceConnection;
    private CarDataManager mCarDataManager;
    private NavigationManager mNavigationManager;

    private Context mContext;
    private boolean mIsPartnerEnablerServiceConnected = false;
    private boolean disablePartnerAPI = true;
    private List<ILibStateChangeListener> mClientListeners = new ArrayList<>();
    private final ScheduledExecutorService scheduledExecutorService;

    private static final String partnerApiServiceName = "technology.cariad.partnerenablerservice.enabler";
    private static final String partnerApiServicePackageName = "technology.cariad.partnerenablerservice";


    private static PartnerLibraryManagerImpl mPartnerLibraryImplInstance;

    public static PartnerLibraryManagerImpl getInstance(Context context) {
        if (mPartnerLibraryImplInstance == null) {
            mPartnerLibraryImplInstance = new PartnerLibraryManagerImpl(context);
        }
        return mPartnerLibraryImplInstance;
    }
    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    class PartnerEnablerServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mService = IPartnerEnabler.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected() connected");
            mIsPartnerEnablerServiceConnected = true;

            if (mClientListeners != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsPartnerEnablerServiceConnected);
                    for(ILibStateChangeListener listener: mClientListeners) {
                        listener.onStateChanged(mIsPartnerEnablerServiceConnected);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
            mIsPartnerEnablerServiceConnected = false;
            if (mClientListeners != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsPartnerEnablerServiceConnected);
                    for(ILibStateChangeListener listener: mClientListeners) {
                        listener.onStateChanged(mIsPartnerEnablerServiceConnected);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private PartnerLibraryManagerImpl(Context context) {
        Log.d(TAG,"PartnerLibrary");
        mContext = context;
        disablePartnerAPI = true;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    @Override
    public Response.Status initialize() {
        Log.d(TAG,"initialize required services");
        verifyServiceVersion();
        if (disablePartnerAPI) {
            return Response.Status.SERVICE_NOT_AVAILABLE;
        }
        // bind to the enabler service.
        return initService() ? Response.Status.SUCCESS : Response.Status.SERVICE_CONNECTION_FAILURE;
    }

    @Override
    public Response.Status release() {
        Log.d(TAG,"release");
        // unbind service
        releaseService();
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status start() {
        if (disablePartnerAPI) {
            return Response.Status.SERVICE_NOT_AVAILABLE;
        }
        Log.d(TAG,"start");
        Response.Status ret = Response.Status.SUCCESS;
        if (mIsPartnerEnablerServiceConnected) {
            try {
                mService.initialize();
                mCarDataManager = new CarDataManagerImpl(mService);
                mNavigationManager = new NavigationManagerImpl(mService);
            } catch (SecurityException e) {
                ret = Response.Status.PERMISSION_DENIED;
                e.printStackTrace();
            } catch (IllegalStateException | IllegalArgumentException e) {
                e.printStackTrace();
                ret = Response.Status.INTERNAL_FAILURE;
            } catch (RemoteException e) {
                ret = Response.Status.SERVICE_COMMUNICATION_FAILURE;
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public Response.Status stop() {
        if (disablePartnerAPI) {
            return Response.Status.SERVICE_NOT_AVAILABLE;
        }
        Log.d(TAG,"stop");
        Response.Status ret = Response.Status.SUCCESS;
        if (mIsPartnerEnablerServiceConnected) {
            try {
                mService.release();
            } catch (SecurityException e) {
                ret = Response.Status.PERMISSION_DENIED;
                e.printStackTrace();
            } catch (IllegalStateException | IllegalArgumentException e) {
                e.printStackTrace();
                ret = Response.Status.INTERNAL_FAILURE;
            } catch (RemoteException e) {
                ret = Response.Status.SERVICE_COMMUNICATION_FAILURE;
                e.printStackTrace();
            }
        }
        return ret;
    }

    @Override
    public void addListener(ILibStateChangeListener listener) {
        if (disablePartnerAPI) {
            return;
        }
        mClientListeners.add(listener);
    }

    @Override
    public void removeListener(ILibStateChangeListener listener) {
        if (disablePartnerAPI) {
            return;
        }
        mClientListeners.remove(listener);
    }

    @Override
    public Response<CarDataManager> getCarDataManager() {
        Response<CarDataManager> response = new Response<>(Response.Status.SUCCESS);
        if (disablePartnerAPI) {
            response.status = Response.Status.SERVICE_NOT_AVAILABLE;
            return response;
        }
        if (!mIsPartnerEnablerServiceConnected) {
            response.status = Response.Status.SERVICE_CONNECTION_FAILURE;
            return response;
        }
        response.value = mCarDataManager;
        return response;
    }

    @Override
    public Response<NavigationManager> getNavigationManager() {
        Response<NavigationManager> response = new Response<>(Response.Status.SUCCESS);
        if (disablePartnerAPI) {
            response.status = Response.Status.SERVICE_NOT_AVAILABLE;
            return response;
        }
        if (!mIsPartnerEnablerServiceConnected) {
            response.status = Response.Status.SERVICE_CONNECTION_FAILURE;
            return response;
        }
        response.value = mNavigationManager;
        return response;
    }

    /** Binds the user activity to the service. */
    private boolean initService() {
        Log.d(TAG,"initService trying to bindService");
        mServiceConnection = new PartnerEnablerServiceConnection();
        Intent i = new Intent(partnerApiServiceName).setPackage(partnerApiServicePackageName);
        boolean ret = mContext.bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound with " + ret);
        return ret;
    }

    /** Unbinds the user activity from the service. */
    private void releaseService() {
        mContext.unbindService(mServiceConnection);
        mServiceConnection = null;
        mIsPartnerEnablerServiceConnected = false;
        Log.d(TAG, "releaseService() unbound.");
    }

    private void verifyServiceVersion() {
        disablePartnerAPI = false;

        // check the version on device.
        if (!isPackageInstalled(partnerApiServicePackageName, mContext.getPackageManager())) {
            disablePartnerAPI = true;
            // TODO: update partner that capability is not available on device.
            return;
        }

        if (getCurrentVersionPES().equals("1.0")) { // equals skeleton
            disablePartnerAPI = true;
            showDialog();
            // TODO: update partner with service disconnected and pes is getting updated.
        }
    }

    private String getCurrentVersionPES() {
        String version = null;
        try {
            mContext.getPackageManager().getApplicationInfo(partnerApiServicePackageName, 0);
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(partnerApiServicePackageName, 0);
            Log.i(TAG,"PackageVersionName: " + packageInfo.versionName + ",versionCode; " + packageInfo.getLongVersionCode());
            version = packageInfo.versionName;
        }
        catch (PackageManager.NameNotFoundException e) {
            // it should not come here as app installed will be checked upon initialization
            e.printStackTrace();
        }
        return version;
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        builder.setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        // productid is the package name
                        String partnerAPIDeepLink = "ignitemarket://screen?screenName=details&productId="+partnerApiServicePackageName;
                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(partnerAPIDeepLink)));
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

    class UpdateCheckerTask implements Runnable {
        public UpdateCheckerTask() { }
        public void run()
        {
            // TODO: check logcat for failure logs here.
            if (!getCurrentVersionPES().equals("1.0")) {
                // skeleton updated. This logic needs to be checked and updated
                scheduledExecutorService.shutdown();
                disablePartnerAPI = false;
            }
        }
    }
}