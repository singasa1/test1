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

import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;
import static android.car.VehiclePropertyIds.PERF_STEERING_ANGLE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import androidx.annotation.GuardedBy;
import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;

import technology.cariad.partnerenablerservice.PartnerAPIConstants;
import technology.cariad.partnerenablerservice.ISteeringAngleChangeListener;


public class VehicleDrivingService extends IVehicleDrivingService.Stub {
    private static final String TAG = "PartnerEnablerService.VehicleInfoService";

    /** List of clients listening to TurnSignalState */
    private final RemoteCallbackList<ISteeringAngleChangeListener> mSteeringAngleChangeListeners =
            new RemoteCallbackList<>();

    private Context mContext;

    @GuardedBy("mCarPropertyManagerLock")
    private final CarPropertyManager mCarPropertyManager;
    private final PartnerAccessManager mPartnerAccessManager;


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
                        case PERF_STEERING_ANGLE:
                            Log.d(TAG,"Dispatching Steering Angle value changed to clients: " + value);
                            int numClients = mSteeringAngleChangeListeners.beginBroadcast();
                            for (int i = 0; i < numClients; i++) {
                                ISteeringAngleChangeListener callback = mSteeringAngleChangeListeners.getBroadcastItem(i);
                                try {
                                    callback.onSteeringAngleChanged((float)value.getValue());
                                } catch (RemoteException ignores) {
                                    // ignore
                                }
                            }
                            mSteeringAngleChangeListeners.finishBroadcast();
                            break;
                        default:
                            break;
                    }

                }

                @Override
                public void onErrorEvent(int propId, int zone) {
                    Log.e(TAG, "Error in callback for steering angle");
                }
            };

    public VehicleDrivingService(Context context, CarPropertyManager carPropertyManager, PartnerAccessManager partnerAccessManager) {
        mContext = context;
        mCarPropertyManager = carPropertyManager;
        mPartnerAccessManager = partnerAccessManager;

        if(mCarPropertyManager != null) {
            if (!mCarPropertyManager.registerCallback(mCarPropertyEventCallback,
                    PERF_STEERING_ANGLE,
                    PartnerAPIConstants.PROPERTY_UPDATE_RATE_HZ)) {
                Log.e(TAG,
                        "Failed to register callback for TURN_SIGNAL_STATE with CarPropertyManager");
            return;
            }
        }
    }

    public float getSteeringAngle() {
        mPartnerAccessManager.verifyAccessAndPermission(
                mContext.getPackageManager().getNameForUid(Binder.getCallingUid()),
                PartnerAPIConstants.PERMISSION_RECEIVE_STEERING_ANGLE_INFO);

        if (mCarPropertyManager == null) {
            throw new IllegalStateException("Service not initialize properly");
        }

        float steeringAngle = (float) mCarPropertyManager.getProperty(PERF_STEERING_ANGLE, VEHICLE_AREA_TYPE_GLOBAL).getValue();
        Log.d(TAG, "get Steering Angle value: " + steeringAngle);
        return steeringAngle;
    }

    public void addSteeringAngleChangeListener(ISteeringAngleChangeListener listener) {
        mPartnerAccessManager.verifyAccessAndPermission(mContext.getPackageManager().getNameForUid(
                        Binder.getCallingUid()),
                PartnerAPIConstants.PERMISSION_RECEIVE_STEERING_ANGLE_INFO);

        if (listener == null) {
            throw new IllegalArgumentException("ISteeringAngleChangeListener is null");
        }
        mSteeringAngleChangeListeners.register(listener);

    }

    public void removeSteeringAngleChangeListener(ISteeringAngleChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("ISteeringAngleChangeListener is null");
        }
        mSteeringAngleChangeListeners.unregister(listener);

    }
}
