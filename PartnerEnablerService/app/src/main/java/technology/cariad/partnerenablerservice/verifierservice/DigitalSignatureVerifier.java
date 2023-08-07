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

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class DigitalSignatureVerifier extends Service {
    private static final String TAG = DigitalSignatureVerifier.class.getSimpleName();

    // declaring SignatureVerifierImpl binder instance
    private SignatureVerifierImpl mService;

    @Override
    public void onCreate() {
        Log.d(TAG,"onCreate");
        init();
        super.onCreate();
    }

    @Override
    // execution of the service will stop on calling this method
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        release();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand Service started.");
        return START_STICKY;
    }

    private void init() {
        if (mService == null) {
            mService = new SignatureVerifierImpl(this);
        }
    }

    private void release() {
        if (mService != null) {
            mService = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mService;
    }
}
