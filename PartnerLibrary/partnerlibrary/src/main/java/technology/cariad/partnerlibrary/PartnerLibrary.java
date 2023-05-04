package technology.cariad.partnerlibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import technology.cariad.partnerenablerservice.IPartnerEnabler;
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

import java.util.List;

/**
 * <h1>Partner Library</h1>
 * Partner Library provides wrapper apis for different app developers.
 * It has signature verification apis and other apis for getting the Active Route, Interior/Exterior Light status.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public class PartnerLibrary {
    private static final String TAG = PartnerLibrary.class.getSimpleName();

    private IPartnerEnabler mService;
    private PartnerEnablerServiceConnection mServiceConnection;
    private Context mContext;
    private boolean mIsServiceInitialized = false;
    private ILibStateChangeListener mClientListener = null;

    private static final String partnerApiServiceName = "technology.cariad.partnerenablerservice.enabler";
    private static final String partnerApiServicePackageName = "technology.cariad.partnerenablerservice";

    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    class PartnerEnablerServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mService = IPartnerEnabler.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected() connected");
            mIsServiceInitialized = true;
            if (mClientListener != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsServiceInitialized);
                    mClientListener.onLibStateReady(mIsServiceInitialized);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
            mIsServiceInitialized = false;
            if (mClientListener != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsServiceInitialized);
                    mClientListener.onLibStateReady(mIsServiceInitialized);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PartnerLibrary(Context context) {
        Log.d(TAG,"PartnerLibrary");
        mContext = context;
    }

    /**
     * This method binds to the PartnerEnabler service.
     */
    public void initialize() {
        Log.d(TAG,"initialize required services");
        // bind to the enabler service.
        initService();
    }

    /**
     * This method unbinds the PartnerEnabler service
     */
    public void deinitialize() {
        Log.d(TAG,"deinitialize");
        // unbind service
        releaseService();

    }

    /**
     * This method initializes the PartnerEnabler service components
     */
    public void start() {
        Log.d(TAG,"start");
        if (mIsServiceInitialized) {
            try {
                mService.initialize();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method uninitializes the PartnerEnabler service components
     */
    public void stop() {
        Log.d(TAG,"stop");
        if (mIsServiceInitialized) {
            try {
                mService.release();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is to add the listener to get PartnerEnablerServiceConnection status.
     * @param listener ILibStateChangeListener object from client/app.
     */
    public void addListener(ILibStateChangeListener listener) {
        mClientListener = listener;
    }

    /**
     * This method is to remove the listener.
     */
    public void removeListener(ILibStateChangeListener listener) {
        mClientListener = null;
    }

    /**
     * This method verifies the provided package signature
     * matches with signed config provided by the SignatureGenerator tool.
     * @param packageName Package name of the 3rd party app.
     * @return true - if signature verification succeeds. False - if signature verification fails.
     */
    public boolean verifyDigitalSignature(@NonNull String packageName) {
        boolean retVal = false;
        if (mIsServiceInitialized) {
            try {
                ISignatureVerifier verifier = mService.getPartnerVerifierService();
                retVal = verifier.verifyDigitalSignature(packageName);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return retVal;
    }

    /** Binds the user activity to the service. */
    private void initService() {
        Log.d(TAG,"initService trying to bindService");
        mServiceConnection = new PartnerEnablerServiceConnection();
        Intent i = new Intent(partnerApiServiceName).setPackage(partnerApiServicePackageName);
        boolean ret = mContext.bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "initService() bound with " + ret);
    }

    /** Unbinds the user activity from the service. */
    private void releaseService() {
        mContext.unbindService(mServiceConnection);
        mServiceConnection = null;
        Log.d(TAG, "releaseService() unbound.");
    }
}