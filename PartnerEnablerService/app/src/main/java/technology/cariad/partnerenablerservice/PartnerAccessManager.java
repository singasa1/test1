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
package technology.cariad.partnerenablerservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import technology.cariad.partnerenablerservice.verifierservice.DigitalSignatureVerifier;
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

/**
 * Singleton that manages verification of partner calls to the Service.
 * Holds a cache of  verification status of previously verified applications.
 */
@Singleton
class PartnerAccessManager {
    private static final String TAG = PartnerAccessManager.class.getSimpleName();

    // This flag is only for testing purposes, to allow access without verification. This should be set to false in production.
    private static final boolean DEBUG_MODE = false;

    private final Context mContext;

    // This is a HashMap with Key as package names of the calling applications and
    // Value as their respective verification status
    private Map<String, Boolean> mAccessCache = Collections.synchronizedMap(new HashMap());
    private boolean mIsServiceConnected = false;
    private ISignatureVerifier mSignatureVerifierService;
    private SignatureVerifierConnection mServiceConnection;

    @Inject
    PartnerAccessManager(@ApplicationContext Context context) {
        mContext = context;
    }

    /**
     * Initialize and connect to VerifierService.
     */
    public void initialize() {
        if (!mIsServiceConnected || mSignatureVerifierService == null) {
            mServiceConnection = new SignatureVerifierConnection();
            Intent i = new Intent(mContext, DigitalSignatureVerifier.class);
            boolean ret = mContext.bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
            Log.d(TAG, "bindService done: " + ret);
        }
    }

    /**
     * Clean up cache and service connection.
     */
    public void cleanUp() {
        synchronized (mAccessCache) {
            mAccessCache = null;
        }
        if (mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        mIsServiceConnected = false;
        mSignatureVerifierService = null;
    }

    /**
     * Uses cache and Verifier service to determine whether access is allowed for the
     * given package name
     * @param packageName package name of the application to be verified
     * @return true if access is allowed and false if not.
     * @throws RemoteException
     * @throws IllegalStateException
     */
    public boolean isAccessAllowed(String packageName) throws RemoteException, IllegalStateException {
        if (DEBUG_MODE) return true;

        if (!mIsServiceConnected || mSignatureVerifierService == null) {
            // TODO: Check why service connection is delayed causing exception. Uncomment below lines once that is resolved
            //throw new IllegalStateException("Service is not connected to verify");
            Log.d(TAG, "Service is not connected to verify");
        }

        checkAndUpdateCache(packageName);
        boolean accessAllowed = false;
        synchronized (mAccessCache) {
            accessAllowed = mAccessCache.get(packageName);
        }
        return accessAllowed;
    }

    /**
     * Check if access is allowed and throw SecurityException if the access is not allowed for the
     * package.
     * @param packageName package name of the application to which access is verified.
     * @throws SecurityException if the access is not allowed.
     */
    public void verifyAccess(String packageName) throws SecurityException {
        Log.d(TAG, "Calling app is: " + packageName);
        try {
            if (!isAccessAllowed(packageName)) {
                throw new SecurityException(
                        "The app " + packageName +
                                " doesn't have the permission to access Partner API's");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if access is allowed and throw SecurityException if the access is not allowed for the
     * package.
     * @param packageName package name of the application to which access is verified.
     * @throws SecurityException if the access is not allowed.
     */
    public void verifyAccessAndPermission(String packageName, String permission) throws SecurityException {
        Log.d(TAG, "Calling app is: " + packageName);

        if (mContext.getPackageManager().checkPermission(permission, packageName) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "VWAE permission " + permission + " not granted");
            throw new SecurityException("Permission" + permission + " is required to access API");
        }

        verifyAccess(packageName);
    }

    private void checkAndUpdateCache(String packageName) throws RemoteException {
        synchronized (mAccessCache) {
            if (!mAccessCache.containsKey(packageName)) {
                mAccessCache.put(packageName, mSignatureVerifierService.verifyDigitalSignature(packageName));
            }
        }
    }

    class SignatureVerifierConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSignatureVerifierService = ISignatureVerifier.Stub.asInterface((IBinder) service);
            Log.d(TAG, "onServiceConnected");
            mIsServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSignatureVerifierService = null;
            mIsServiceConnected = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    }
}
