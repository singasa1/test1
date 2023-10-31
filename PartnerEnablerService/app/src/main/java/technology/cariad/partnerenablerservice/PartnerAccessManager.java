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
    private static final boolean DEBUG_MODE = true;

    private final Context mContext;

    // This is a HashMap with Key as package names of the calling applications and
    // Value as their respective verification status
    private Map<String, Boolean> accessCache = Collections.synchronizedMap(new HashMap());
    private boolean isServiceConnected = false;
    private ISignatureVerifier mService;
    private SignatureVerifierConnection mServiceConnection;

    @Inject
    PartnerAccessManager(@ApplicationContext Context context) {
        mContext = context;
    }

    /**
     * Initialize and connect to VerifierService.
     */
    public void initialize() {
        if (!isServiceConnected || mService == null) {
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
        synchronized (accessCache) {
            accessCache = null;
        }
        if (mServiceConnection != null) {
            mContext.unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        isServiceConnected = false;
        mService = null;
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
        if (!isServiceConnected || mService == null) {
            throw new IllegalStateException("Service is not connected to verify");
        }

        if (DEBUG_MODE) return true;

        checkAndUpdateCache(packageName);
        boolean accessAllowed = false;
        synchronized (accessCache) {
            accessAllowed = accessCache.get(packageName);
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
        synchronized (accessCache) {
            if (!accessCache.containsKey(packageName)) {
                accessCache.put(packageName, mService.verifyDigitalSignature(packageName));
            }
        }
    }

    class SignatureVerifierConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ISignatureVerifier.Stub.asInterface((IBinder) service);
            Log.d(TAG, "onServiceConnected");
            isServiceConnected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isServiceConnected = false;
            Log.d(TAG, "onServiceDisconnected");
        }
    }
}
