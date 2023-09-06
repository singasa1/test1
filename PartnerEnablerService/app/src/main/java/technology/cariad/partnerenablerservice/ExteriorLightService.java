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
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Telephony;
import android.util.Log;

import androidx.annotation.GuardedBy;

import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;
import static android.car.VehiclePropertyIds.TURN_SIGNAL_STATE;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;

import technology.cariad.partnerenablerservice.PartnerAPI;
import technology.cariad.partnerenablerservice.ITurnSignalStateListener;

public class ExteriorLightService extends IExteriorLightService.Stub {
    private static final String TAG = "PartnerEnablerService.ExteriorLightService";

    private Context mContext;
    /** List of clients listening to UX restriction events */
    private final RemoteCallbackList<ITurnSignalStateListener> mTurnSignalStateListener =
            new RemoteCallbackList<>();

    @GuardedBy("mLock")
    private CarPropertyManager mCarPropertyManager;

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
                        case TURN_SIGNAL_STATE:
                            Log.d(TAG,"Dispatching Turn Signal State values changed to clients: " + value);
                            int numClients = mTurnSignalStateListener.beginBroadcast();
                            for (int i = 0; i < numClients; i++) {
                                ITurnSignalStateListener callback = mTurnSignalStateListener.getBroadcastItem(i);
                                try {
                                    callback.onTurnSignalStateChanged((int)value.getValue());
                                } catch (RemoteException ignores) {
                                    // ignore
                                }
                            }
                            mTurnSignalStateListener.finishBroadcast();
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

    public ExteriorLightService(Context context, CarPropertyManager carPropertyManager) {
        mContext = context;
        mCarPropertyManager = carPropertyManager;

        if(mCarPropertyManager != null) {
            if (!mCarPropertyManager.registerCallback(mCarPropertyEventCallback,
                    TURN_SIGNAL_STATE,
                    PartnerAPI.PROPERTY_UPDATE_RATE_HZ)) {
                Log.e(TAG,
                        "Failed to register callback for TURN_SIGNAL_STATE with CarPropertyManager");
//            return;
            }
        }
    }


    @Override
    public int getTurnSignalIndicator() throws RemoteException {
        // Permission check
        Log.d(TAG,"getTurnSignalIndicator");
//        if (PackageManager.PERMISSION_GRANTED != mContext.checkCallingOrSelfPermission(
//                VWAE_CAR_MILEAGE_PERMISSION)) {
        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.d(TAG,"VWAE permission not granted");
            throw new SecurityException("getTurnSignalIndicator requires CAR_MILEAGE permission");
        }
        int turnSignalIndicator = (int)mCarPropertyManager.getProperty(TURN_SIGNAL_STATE, VEHICLE_AREA_TYPE_GLOBAL).getValue();
        Log.d(TAG,"TurnSignalState Value: " + turnSignalIndicator);
        return turnSignalIndicator;
    }

    @Override
    public void addTurnSignalStateListener(ITurnSignalStateListener listener) throws RemoteException {
        if (listener == null) {
            throw new IllegalArgumentException("ITurnSignalStateListener is null");
        }
        mTurnSignalStateListener.register(listener);
    }

    @Override
    public void removeTurnSignalStateListener(ITurnSignalStateListener listener) throws RemoteException {
        if (listener == null) {
            throw new IllegalArgumentException("ITurnSignalStateListener is null");
        }
        mTurnSignalStateListener.unregister(listener);
    }
}
