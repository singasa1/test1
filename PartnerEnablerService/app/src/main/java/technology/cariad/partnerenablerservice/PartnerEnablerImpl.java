package technology.cariad.partnerenablerservice;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

public class PartnerEnablerImpl extends IPartnerEnabler.Stub {

    private static final String TAG = PartnerEnablerImpl.class.getSimpleName();
    private final Context mContext;
    private ISignatureVerifier mSignatureVerifier;
    private VerifierServiceConnection mServiceConnection;
    private static final String METADATA_KEY = "VWAE_Sig_V1";

    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    class VerifierServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mSignatureVerifier = ISignatureVerifier.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected() connected");
//            Toast.makeText(mContext, "Service connected", Toast.LENGTH_LONG)
//                    .show();
        }

        public void onServiceDisconnected(ComponentName name) {
            mSignatureVerifier = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
//            Toast.makeText(mContext, "Service connected", Toast.LENGTH_LONG)
//                    .show();
        }
    }

    PartnerEnablerImpl(Context context) {
        mContext = context;
    }

    @Override
    public void initialize() {
        Log.d(TAG,"initialize");
        mServiceConnection = new VerifierServiceConnection();
        Intent i = new Intent("technology.cariad.partnerverifierlibrary.verifier").setPackage("technology.cariad.partnerverifierlibrary");
        boolean ret = mContext.bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG,"bind verifier service result: " + ret);
    }

    @Override
    public void release() {
        Log.d(TAG, "release");
        mContext.unbindService(mServiceConnection);
        mServiceConnection = null;
        Log.d(TAG, "releaseService() unbound.");
    }

    @Override
    public ISignatureVerifier getPartnerVerifierService() {
        // check for meta-data access key
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = mContext.getPackageManager().getApplicationInfo("com.cariad.technology.sampleapp", PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo == null) Log.e(TAG, "appinfo is null");
        else {
            Bundle bundle = applicationInfo.metaData;
            if (bundle != null) {
                Log.d(TAG,"MetadtaKey: " + bundle.getString(METADATA_KEY));
            }
        }
        return mSignatureVerifier;
    }
}
