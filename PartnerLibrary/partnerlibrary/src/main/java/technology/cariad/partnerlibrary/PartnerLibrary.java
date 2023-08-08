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
package technology.cariad.partnerlibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import technology.cariad.partnerenablerservice.IPartnerEnabler;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Partner Library</h1>
 * Partner Library provides wrapper apis for different app developers.
 * It has apis for getting the Active Route, Interior/Exterior Light status.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public class PartnerLibrary {
    private static final String TAG = PartnerLibrary.class.getSimpleName();
    private static final String PARTNER_API_SERVICE_NAME = "technology.cariad.partnerenablerservice.enabler";
    private static final String PARTNER_API_SERVICE_PACKAGE_NAME = "technology.cariad.partnerenablerservice";

    private IPartnerEnabler mService;
    private PartnerEnablerServiceConnection mServiceConnection;
    private CarDataManager mCarDataManager;

    private Context mContext;
    private boolean mIsPartnerEnablerServiceConnected = false;
    private List<ILibStateChangeListener> mClientListeners = new ArrayList<>();


    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    class PartnerEnablerServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            mService = IPartnerEnabler.Stub.asInterface((IBinder) boundService);
            Log.d(TAG, "onServiceConnected() connected");
            mIsPartnerEnablerServiceConnected = true;
            mCarDataManager = new CarDataManager(mService);
            if (mClientListeners != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsPartnerEnablerServiceConnected);
                    for(ILibStateChangeListener listener: mClientListeners) {
                        listener.onStateChanged(mIsPartnerEnablerServiceConnected);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            Log.d(TAG, "onServiceDisconnected() disconnected");
            mIsPartnerEnablerServiceConnected = false;
            if (mClientListeners != null) {
                try {
                    Log.d(TAG, "calling listener onLibStateReady with value: " + mIsPartnerEnablerServiceConnected);
                    for(ILibStateChangeListener listener: mClientListeners) {
                        listener.onStateChanged(mIsPartnerEnablerServiceConnected);
                    }
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
    public void release() {
        Log.d(TAG,"release");
        // unbind service
        releaseService();
    }

    /**
     * This method initializes the PartnerEnabler service components
     */
    public void start() throws SecurityException, RemoteException {
        Log.d(TAG,"start");
        if (mIsPartnerEnablerServiceConnected) {
            try {
                mService.initialize();
            } catch(SecurityException e) {
                // TODO: Add proper error communication for all APIs
                e.printStackTrace();
            }
        }
    }

    /**
     * This method uninitializes the PartnerEnabler service components
     */
    public void stop() throws SecurityException, RemoteException {
        Log.d(TAG,"stop");
        if (mIsPartnerEnablerServiceConnected) {
            try {
                mService.release();
            } catch(SecurityException e) {
                // TODO: Add proper error communication for all APIs
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is to add the listener to get PartnerEnablerServiceConnection status.
     * @param listener ILibStateChangeListener object from client/app.
     */
    public void addListener(ILibStateChangeListener listener) {
        mClientListeners.add(listener);
    }

    /**
     * This method is to remove the listener.
     */
    public void removeListener(ILibStateChangeListener listener) {
        mClientListeners.remove(listener);
    }

    public CarDataManager getCarDataManager() {
        return mCarDataManager;
    }

    /** Binds the user activity to the service. */
    private void initService() {
        Log.d(TAG,"initService trying to bindService");
        mServiceConnection = new PartnerEnablerServiceConnection();
        Intent i = new Intent(PARTNER_API_SERVICE_NAME).setPackage(PARTNER_API_SERVICE_PACKAGE_NAME);
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