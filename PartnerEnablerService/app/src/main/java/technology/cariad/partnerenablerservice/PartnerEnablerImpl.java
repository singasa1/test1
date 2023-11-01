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

import android.car.Car;
import android.car.hardware.property.CarPropertyManager;
import android.content.Context;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.GuardedBy;

public class PartnerEnablerImpl extends IPartnerEnabler.Stub {

    private static final String TAG = "PartnerEnablerService:" + PartnerEnablerImpl.class.getSimpleName();

    private final Context mContext;
    private PartnerAccessManager mPartnerAccessManager;
    private ExteriorLightService mExteriorLightService;
    private NavigationService mNavigationService;
    private VehicleInfoService mVehicleInfoService;
    private VehicleDrivingService mVehicleDrivingService;

    @GuardedBy("mLock")
    private CarPropertyManager mCarPropertyManager;

    PartnerEnablerImpl(Context context, PartnerAccessManager partnerAccessManager) {
        mContext = context;
        mPartnerAccessManager = partnerAccessManager;
    }

    @Override
    public void initialize() throws SecurityException {
        Log.d(TAG, "initialize");
        mPartnerAccessManager.verifyAccess(mContext.getPackageManager().getNameForUid(Binder.getCallingUid()));

        mCarPropertyManager =
                (CarPropertyManager) Car.createCar(mContext).getCarManager(Car.PROPERTY_SERVICE);
        if (mCarPropertyManager == null) {
            Log.e(TAG, "Failed to get CarPropertyManager");
            throw new IllegalStateException("CAR Property Service not ready");
        }

        mExteriorLightService= new ExteriorLightService(mContext, mCarPropertyManager, mPartnerAccessManager);
        mVehicleInfoService = new VehicleInfoService(mContext, mCarPropertyManager, mPartnerAccessManager);
        mNavigationService = new NavigationService(mContext, mPartnerAccessManager);
        mVehicleDrivingService = new VehicleDrivingService(mContext, mCarPropertyManager, mPartnerAccessManager);
    }

    @Override
    public void release() throws SecurityException {
        Log.d(TAG, "release");
        mPartnerAccessManager.verifyAccess(mContext.getPackageManager().getNameForUid(Binder.getCallingUid()));
    }

    @Override
    public IBinder getAPIService(String serviceName) throws RemoteException {
        Log.i(TAG, "calling getAPIService for service:" + serviceName);
        switch (serviceName) {
            case PartnerAPIConstants.EXTERIOR_LIGHT_SERVICE:
                Log.i(TAG, " getAPIService: mExteriorLightService=" + mExteriorLightService);
                return mExteriorLightService;
            case PartnerAPIConstants.NAVIGATION_SERVICE:
                Log.i(TAG, " getAPIService: mNavigationService=" + mNavigationService);
                return mNavigationService;
            case PartnerAPIConstants.VEHICLE_INFO_SERVICE:
                Log.i(TAG, "getAPIService: mVehicleInfoService = " + mVehicleInfoService);
                return mVehicleInfoService;
            case PartnerAPIConstants.VEHICLE_DRIVING_SERVICE:
                Log.i(TAG, "getAPIService: mVehicleDrivingService = " + mVehicleDrivingService);
                return mVehicleDrivingService;
            default:
                Log.w(TAG, "getAPIService for unknown service:" + serviceName);
                return null;
        }
    }
}
