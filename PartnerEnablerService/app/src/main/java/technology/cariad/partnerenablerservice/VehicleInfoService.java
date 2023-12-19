package technology.cariad.partnerenablerservice;

import static android.car.VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL;
import static android.car.VehiclePropertyIds.INFO_VIN;

import android.content.Context;
import androidx.annotation.GuardedBy;
import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.property.CarPropertyManager;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.RemoteException;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Server side implementation of IVehicleInfoService AIDL stub.
 * This class reads the static VHAL property INFO_VIN from the CarPropertyManager and notify the registered clients.
 */
@Singleton
class VehicleInfoService extends IVehicleInfoService.Stub {
    private static final String TAG = "PartnerEnablerService.VehicleInfoService";

    private final Context mContext;

    @GuardedBy("mCarPropertyManagerLock")
    private final CarPropertyManager mCarPropertyManager;
    private final PartnerAccessManager mPartnerAccessManager;

    @Inject
    VehicleInfoService(@ApplicationContext Context context, CarPropertyManager carPropertyManager, PartnerAccessManager partnerAccessManager) {
        mContext = context;
        mCarPropertyManager = carPropertyManager;
        mPartnerAccessManager = partnerAccessManager;
    }

    @Override
    public int getIfcVersion() {
        return IVehicleInfoService.VERSION;
    }

    @Override
    public String getVehicleIdentityNumber() {
        if (mCarPropertyManager == null) {
            throw new IllegalStateException("Service not initialize properly");
        }

        mPartnerAccessManager.verifyAccessAndPermission(mContext.getPackageManager().getNameForUid(
                Binder.getCallingUid()),
                PartnerAPIConstants.PERMISSION_RECEIVE_CAR_INFO_VIN);



        String vin = (String) mCarPropertyManager.getProperty(INFO_VIN, VEHICLE_AREA_TYPE_GLOBAL).getValue();
        Log.d(TAG, "VIN number: " + vin);
        return vin;
    }
}
