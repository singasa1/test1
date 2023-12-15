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
package technology.cariad.partnerenablerservice.verifierservice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

/**
 * Server side implementation of IPartnerEnabler AIDL stub.
 * Responsible for verifying the authToken of the given package.
 * Forms the authToken at run time using PackageManager apis and
 * compare it with the metadata of the given package manifest xml file
 */
class SignatureVerifierImpl extends ISignatureVerifier.Stub {
    private static final String TAG = SignatureVerifierImpl.class.getSimpleName();
    private static final String METADATA_KEY = "VWAE_Sig_V1";

    private final Context mContext;

    @Inject
    SignatureVerifierImpl(@ApplicationContext Context context) {
        mContext = context;
    }

    /**
     * This method verifies the digital signature of the given packageName.
     *
     * @param packageName package name of the app that uses the PartnerLibrary
     *
     * @return  returns true if verification succeeds. false if verification fails.
     */
    public boolean verifyDigitalSignature(String packageName) {
        boolean ret = false;
        if (packageName == null || packageName.isEmpty()) {
            Log.e(TAG,"Given packagename is null or empty");
            return ret;
        }
        String metadata = getMetadata(packageName);
        if (metadata == null) {
            Log.d(TAG, "API Access Key missing in given app manifest file");
            ret = false;
        } else {
            ret = Utils.verify(mContext, packageName, metadata);
        }
        return ret;
    }

    /**
     * This method uses packagemanager to read the metadata of the given package name.
     *
     * @param packageName package name of the app that uses the PartnerLibrary
     *
     * @return  returns metadata of the given package name if available else returns null;
     */
    private String getMetadata(String packageName) {
        String result = null;
        // check for meta-data access key
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = mContext.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG,"Exception: " + e.getMessage());
        }
        if (applicationInfo == null) {
            Log.e(TAG, "appinfo is null");
        } else {
            Bundle bundle = applicationInfo.metaData;
            if (bundle != null) {
                result = bundle.getString(METADATA_KEY);
            }
        }
        return result;
    }
}
