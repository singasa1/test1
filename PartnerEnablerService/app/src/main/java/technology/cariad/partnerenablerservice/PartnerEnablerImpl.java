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


import android.content.Context;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;

public class PartnerEnablerImpl extends IPartnerEnabler.Stub {

    private static final String TAG = "PartnerEnablerService:" + PartnerEnablerImpl.class.getSimpleName();
    private static final boolean DEBUG_MODE = false;

    private final Context mContext;
    private PartnerAccessManager mPartnerAccessManager;

    PartnerEnablerImpl(Context context, PartnerAccessManager partnerAccessManager) {
        mContext = context;
        mPartnerAccessManager = partnerAccessManager;
    }

    @Override
    public void initialize() throws SecurityException {
        Log.d(TAG, "initialize");
        verifyAccess(mContext.getPackageManager().getNameForUid(Binder.getCallingUid()));
    }

    @Override
    public void release() throws SecurityException {
        Log.d(TAG, "release");
        verifyAccess(mContext.getPackageManager().getNameForUid(Binder.getCallingUid()));
    }

    /**
     * Check if access is allowed and throw SecurityException if the access is not allowed for the
     * package.
     * @param packageName package name of the application to which access is verified.
     * @throws SecurityException if the access is not allowed.
     */
    private void verifyAccess(String packageName) throws SecurityException {
        Log.d(TAG, "Calling app is: " + packageName);
        if (DEBUG_MODE) {
            Log.d(TAG, "Debug mode is enabled - disabling verification");
            return;
        }

        try {
            if (!mPartnerAccessManager.isAccessAllowed(packageName)) {
                throw new SecurityException(
                        "The app " + packageName +
                                " doesn't have the permission to access Partner API's");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
