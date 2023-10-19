package technology.cariad.partnerenablerservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import technology.cariad.partnerenablerservice.verifierservice.DigitalSignatureVerifier;
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

/**
 * Singleton that manages verification of partner calls to the Service.
 * Holds a cache of  verification status of previously verified applications.
 */
class PartnerAccessManager {
    private static final String TAG = PartnerAccessManager.class.getSimpleName();
    private static volatile PartnerAccessManager partnerAccessManagerInstance = null;

    private Context mContext;

    // This is a HashMap with Key as package names of the calling applications and
    // Value as their respective verification status
    private Map<String, Boolean> accessCache = Collections.synchronizedMap(new HashMap());
    private boolean isServiceConnected = false;
    private ISignatureVerifier mService;
    private SignatureVerifierConnection mServiceConnection;

    private PartnerAccessManager(Context context) {
        mContext = context;
    }

    /**
     * Initializes an instance of PartnerAccessManager if not initialized
     * and returns the Singleton instance.
     * @param context
     * @return Singleton instance of PartnerAccessManager.
     */
    public static PartnerAccessManager getInstance(Context context) {
        synchronized (PartnerAccessManager.class) {
            if (partnerAccessManagerInstance == null) {
                partnerAccessManagerInstance = new PartnerAccessManager(context);
            }
        }
        return partnerAccessManagerInstance;
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
        accessCache = null;
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
        checkAndUpdateCache(packageName);
        boolean accessAllowed = false;
        accessAllowed = accessCache.get(packageName);
        return accessAllowed;
    }

    private void checkAndUpdateCache(String packageName) throws RemoteException {
        if (!accessCache.containsKey(packageName)) {
            accessCache.put(packageName, mService.verifyDigitalSignature(packageName));
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
