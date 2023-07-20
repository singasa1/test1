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
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.GuardedBy;
import android.car.Car;
import android.car.hardware.property.CarPropertyManager;
import android.car.hardware.CarPropertyValue;
import static android.car.VehiclePropertyIds.PERF_ODOMETER;
import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;

import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

public class PartnerEnablerImpl extends IPartnerEnabler.Stub {

    private static final String TAG = "PartnerEnablerService:" + PartnerEnablerImpl.class.getSimpleName();

    private static final String PARTNER_VERIFIER_ACTION_NAME = "technology.cariad.partnerverifierlibrary.verifier";
    private static final String PARTNER_VERIFIER_PACKAGE_NAME = "technology.cariad.partnerverifierlibrary";
    private static final String VWAE_CAR_MILEAGE_PERMISSION = "technology.cariad.vwae.restricted.permission.CAR_MILEAGE";

    private static final int PROPERTY_UPDATE_RATE_HZ = 5000;

    private final Context mContext;
    private ISignatureVerifier mSignatureVerifier;
    private VerifierServiceConnection mServiceConnection;

    @GuardedBy("mLock")
    private Car mCar;

    @GuardedBy("mLock")
    private CarPropertyManager mCarPropertyManager;

    private final Handler mClientDispatchHandler;

    /** List of clients listening to UX restriction events */
    private final RemoteCallbackList<ICarDataChangeListener> mCarDataChangeListeners =
            new RemoteCallbackList<>();

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

    /**
     * {@link CarPropertyEvent} listener registered with the {@link CarPropertyManager} for getting
     * speed change notifications.
     */
    private final CarPropertyManager.CarPropertyEventCallback mCarPropertyEventCallback =
            new CarPropertyManager.CarPropertyEventCallback() {
                @Override
                public void onChangeEvent(CarPropertyValue value) {
                    if (value == null || value.getStatus() != CarPropertyValue.STATUS_AVAILABLE) {
                        return;
                    }
                    switch(value.getPropertyId()) {
                        case PERF_ODOMETER:
                            Log.d(TAG,"Dispatching Odometer values changed to clients: " + value);
                            int numClients = mCarDataChangeListeners.beginBroadcast();
                            for (int i = 0; i < numClients; i++) {
                                ICarDataChangeListener callback = mCarDataChangeListeners.getBroadcastItem(i);
                                try {
                                    callback.onMileageValueChanged((float)value.getValue());
                                } catch (RemoteException ignores) {
                                    // ignore
                                }
                            }
                            mCarDataChangeListeners.finishBroadcast();
                            break;
                        default:
                            break;
                    }

                }

                @Override
                public void onErrorEvent(int propId, int zone) {
                    Log.e(TAG, "Error in callback for vehicle speed");
                }
            };

    PartnerEnablerImpl(Context context) {
        mContext = context;
        mClientDispatchHandler = new Handler();
    }

    @Override
    public void initialize() {
        Log.d(TAG,"initialize");
        mCarPropertyManager =
                (CarPropertyManager) Car.createCar(mContext).getCarManager(Car.PROPERTY_SERVICE);
        if (mCarPropertyManager == null) {
            Log.e(TAG, "Failed to get CarPropertyManager");
//            return;
        }
        if (!mCarPropertyManager.registerCallback(mCarPropertyEventCallback,
                PERF_ODOMETER,
                PROPERTY_UPDATE_RATE_HZ)) {
            Log.e(TAG,
                    "Failed to register callback for PERF_ODOMETER with CarPropertyManager");
//            return;
        }
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

    @Override
    public float getCurrentMileage() throws RemoteException {
        // Permission check
        Log.d(TAG,"getCurrentMileage");
        if (PackageManager.PERMISSION_GRANTED != mContext.checkCallingOrSelfPermission(
                VWAE_CAR_MILEAGE_PERMISSION)) {
            Log.d(TAG,"VWAE permission not granted");
            throw new SecurityException("getCurrentMileage requires CAR_MILEAGE permission");
        }
        float odometerValue = (float)mCarPropertyManager.getProperty(PERF_ODOMETER, VEHICLE_AREA_TYPE_GLOBAL).getValue();
        Log.d(TAG,"Odometer Value: " + odometerValue);
        return odometerValue;
    }

    @Override
    public int getTurnSignalIndicator() throws RemoteException {
        return 0;
    }

    @Override
    public int getFogLightsState() throws RemoteException {
        return 0;
    }

    @Override
    public float getSteeringAngle() throws RemoteException {
        return 0;
    }

    @Override
    public String getVehicleIdentityNumber() throws RemoteException {
        return null;
    }

    @Override
    public void addCarDataChangeListener(ICarDataChangeListener listener) throws RemoteException{
        if (listener == null) {
            throw new IllegalArgumentException("ICarDataChaneListener is null");
        }
        mCarDataChangeListeners.register(listener);
    }

    @Override
    public void removeCarDataChangeListener(ICarDataChangeListener listener) throws RemoteException{}
}
