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
import android.os.IBinder;
import android.util.Log;

import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

public class PartnerEnablerImpl extends IPartnerEnabler.Stub {

    private static final String TAG = "PartnerEnablerService:" + PartnerEnablerImpl.class.getSimpleName();

    private static final String PARTNER_VERIFIER_ACTION_NAME = "technology.cariad.partnerverifierlibrary.verifier";
    private static final String PARTNER_VERIFIER_PACKAGE_NAME = "technology.cariad.partnerverifierlibrary";

    private final Context mContext;
    private ISignatureVerifier mSignatureVerifier;
    private VerifierServiceConnection mServiceConnection;

    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    class VerifierServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mSignatureVerifier = ISignatureVerifier.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected() connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            mSignatureVerifier = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
        }
    }

    PartnerEnablerImpl(Context context) {
        mContext = context;
    }

    @Override
    public void initialize() {
        Log.d(TAG,"initialize");
        mServiceConnection = new VerifierServiceConnection();
        Intent i = new Intent(PARTNER_VERIFIER_ACTION_NAME).setPackage(PARTNER_VERIFIER_PACKAGE_NAME);
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
        return mSignatureVerifier;
    }
}
