package technology.cariad.partnerverifierlibrary;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class SignatureVerifierImpl extends ISignatureVerifier.Stub {
    private static final String TAG = SignatureVerifierImpl.class.getSimpleName();
    private final Context mContext;
    private static final String METADATA_KEY = "VWAE_Sig_V1";

    public SignatureVerifierImpl(Context context) {
        mContext = context;
    }

    public boolean verifyDigitalSignature(String packageName) {
        boolean ret = false;
        String metadata = getMetadata(packageName);
        if (metadata == null) {
            Log.d(TAG, "API Access Key missing in given app manifest file");
            ret = false;
        } else {
            ret = Utils.verify(mContext, packageName, metadata);
        }
        return ret;
    }

    private String getMetadata(String packageName) {
        String result = null;
        // check for meta-data access key
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = mContext.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (applicationInfo == null) Log.e(TAG, "appinfo is null");
        else {
            Bundle bundle = applicationInfo.metaData;
            if (bundle != null) {
                result = bundle.getString(METADATA_KEY);
            }
        }
        return result;
    }
}
