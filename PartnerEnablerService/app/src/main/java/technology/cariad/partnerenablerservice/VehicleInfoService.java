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

public class VehicleInfoService extends IVehicleInfoService.Stub {
    private static final String TAG = "PartnerEnablerService.VehicleInfoService";

    private Context mContext;

    @GuardedBy("mCarPropertyManagerLock")
    private final CarPropertyManager mCarPropertyManager;
    private final PartnerAccessManager mPartnerAccessManager;

    public VehicleInfoService(Context context, CarPropertyManager carPropertyManager, PartnerAccessManager partnerAccessManager) {
        mContext = context;
        mCarPropertyManager = carPropertyManager;
        mPartnerAccessManager = partnerAccessManager;
    }

    public String getVehicleIdentityNumber() {
        verifyAccessAndPermission(mContext.getPackageManager().getNameForUid(
                Binder.getCallingUid()),
                PartnerAPI.PERMISSION_RECEIVE_CAR_INFO_VIN,
                "getVehicleIdentityNumber requires READ_INFO_VIN permission");

        if (mCarPropertyManager == null) {
            throw new IllegalStateException("Service not initialize properly");
        }

        String vin = (String) mCarPropertyManager.getProperty(INFO_VIN, VEHICLE_AREA_TYPE_GLOBAL).getValue();
        Log.d(TAG, "VIN number: " + vin);
        return vin;
    }

    /**
     * Check if access is allowed and throw SecurityException if the access is not allowed for the
     * package.
     * @param packageName package name of the application to which access is verified.
     * @throws SecurityException if the access is not allowed.
     */
    private void verifyAccessAndPermission(String packageName, String permission, String permissionFailureMessage) throws SecurityException {
        Log.d(TAG, "Calling app is: " + packageName);
        try {
            if (!mPartnerAccessManager.isAccessAllowed(packageName)) {
                throw new SecurityException(
                        "The app " + packageName +
                                " doesn't have the permission to access Partner API's");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (mContext.getPackageManager().checkPermission(
                permission, mContext.getPackageManager().getNameForUid(Binder.getCallingUid())) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "VWAE permission not granted");
            throw new SecurityException(permissionFailureMessage);
        }
    }


}
