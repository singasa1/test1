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

import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_LIGHT_STATE_DAYTIME_RUNNING;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_LIGHT_STATE_OFF;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_LIGHT_STATE_ON;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_SIGNAL_INDICATOR_LEFT;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_SIGNAL_INDICATOR_NONE;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_SIGNAL_INDICATOR_RIGHT;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import technology.cariad.partnerenablerservice.IPartnerEnabler;
import technology.cariad.partnerverifierlibrary.ISignatureVerifier;

/**
 * <h1>Partner Library</h1>
 * Partner Library provides wrapper apis for different app developers.
 * It has signature verification apis and other apis for getting the Active Route, Interior/Exterior Light status.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public class CarDataManager {
    private static final String TAG = CarDataManager.class.getSimpleName();

    private IPartnerEnabler mService;

    private List<MileageListener> mMileageListeners = new ArrayList<>();
    private List<TurnSignalListener> mTurnSignalListener = new ArrayList<>();
    private List<FogLightStateListener> mFogLightStateListener = new ArrayList<>();
    private List<SteeringAngleListener> mSteeringAngleListener = new ArrayList<>();


    enum ListenerType {
        MILEAGE,
        TURN_SIGNAL_STATE,
        FOG_LIGHT_STATE,
        STEERING_ANGLE
    }

    public CarDataManager(IPartnerEnabler service) {
        Log.d(TAG,"CarDataManager");
        mService = service;
    }

    /**
     * This method is to add the listener to get Odometer/Mileage value.
     * @param mileageListerer MileageListener object from client/app.
     * if client doesn't have permission to access odometer value, it doesn't add this listener
     */
    public void registerMileageListener(MileageListener mileageListerer) {
        // Add this client to listeners only if it has permission to access the odometer value by calling getCurrentMileage
        if (getCurrentMileage() != -1) {
            mMileageListeners.add(mileageListerer);
        }
    }

    /**
     * This method is to remove the listener.
     */
    public void unregisterMileageListener(MileageListener mileageListener) {
        mMileageListeners.remove(mileageListener);
    }

    /**
     * This method gets the Car current Odometer value from PartnerEnablerService
     * @return int - return current odometer value in Kilometer
     * -1 - if permission to access the odometer api is denied
     *  0 - if there is no odometer value/vhal property available.
     */
    public int getCurrentMileage() {
        int mileage = 0;
        try {
            mileage = mService.getCurrentMileage(); // TODO: Do we need to return some value if there is a permission failure or just throwing security exception is good enough
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e instanceof SecurityException) {
                Log.d(TAG, e.getMessage());
                mileage = -1;
            }
        }
        return mileage;
    }

    /**
     * This method is to add the listener to get Vehicle Turn Signal State value.
     * @param turnSignalListener TurnSignalListener object from client/app.
     * if client doesn't have permission to access the turn signal indicator value, it doesn't add this listener
     */
    public void registerTurnSignalListener(TurnSignalListener turnSignalListener) {
        // Add this client to listeners only if it has permission to access the turn signal indicator value by calling getTurnSignalIndicator
        if (getTurnSignalIndicator() != VehicleSignalIndicator.PERMISSION_DENIED) {
            mTurnSignalListener.add(turnSignalListener);
        }
    }

    /**
     * This method is to remove the listener.
     */
    public void unregisterTurnSignalListener(TurnSignalListener turnSignalListener) {
        mMileageListeners.remove(turnSignalListener);
    }

    /**
     * This method gets the current turn signal indicator value from PartnerEnablerService
     * @return VehicleSignalIndicator - return current signal indicator value(PermissionDenied/None/Right/Left).
     *
     */
    public VehicleSignalIndicator getTurnSignalIndicator() {
        VehicleSignalIndicator indicator = VehicleSignalIndicator.NONE;
        try {
            int retVal = mService.getTurnSignalIndicator();
            switch (retVal) {
                case VEHICLE_SIGNAL_INDICATOR_RIGHT:
                    indicator = VehicleSignalIndicator.RIGHT;
                    break;
                case VEHICLE_SIGNAL_INDICATOR_LEFT:
                    indicator = VehicleSignalIndicator.LEFT;
                    break;
                case VEHICLE_SIGNAL_INDICATOR_NONE:
                default:
                    indicator = VehicleSignalIndicator.NONE;
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e instanceof SecurityException) {
                Log.d(TAG, e.getMessage());
                indicator = VehicleSignalIndicator.PERMISSION_DENIED;
            }
        }
        return indicator;
    }

    /**
     * This method is to add the listener to get Fog Lights State value.
     * @param lightStateListener FogLightStateListener object from client/app.
     * if client doesn't have permission to access the fog light state value, it doesn't add this listener
     */
    public void registerFogLightStateListener(FogLightStateListener lightStateListener) {
        // Add this client to listeners only if it has permission to access the fog light state value by calling getFogLightState
        if (getFogLightsState() != VehicleLightState.PERMISSION_DENIED) {
            mFogLightStateListener.add(lightStateListener);
        }
    }

    /**
     * This method is to remove the listener.
     */
    public void unregisterFogLightStateListener(FogLightStateListener lightStateListener) {
        mFogLightStateListener.remove(lightStateListener);
    }

    /**
     * This method gets the current fog lights state from PartnerEnablerService
     * @return VehicleLightState - return current fog light state value(PermissionDenied/Off/On/DayTimeRunning).
     */
    public VehicleLightState getFogLightsState() {
        VehicleLightState lightState = VehicleLightState.OFF;
        try {
            int retVal = mService.getFogLightsState();
            switch (retVal) {
                case VEHICLE_LIGHT_STATE_ON:
                    lightState = VehicleLightState.ON;
                    break;
                case VEHICLE_LIGHT_STATE_DAYTIME_RUNNING:
                    lightState = VehicleLightState.DAYTIME_RUNNING;
                    break;
                case VEHICLE_LIGHT_STATE_OFF:
                default:
                    lightState = VehicleLightState.OFF;
                    break;
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e instanceof SecurityException) {
                Log.d(TAG, e.getMessage());
                lightState = VehicleLightState.PERMISSION_DENIED;
            }
        }
        return lightState;
    }

    /**
     * This method is to add the listener to get steering angle value.
     * @param steeringAngleListener SteeringAngleListener object from client/app.
     * if client doesn't have permission to access the fog light state value, it doesn't add this listener
     */
    public void registerSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        // Add this client to listeners only if it has permission to access the steering angle value by calling getSteeringAngle
        if (getSteeringAngle() != -1) {
            mSteeringAngleListener.add(steeringAngleListener);
        }
    }

    /**
     * This method is to remove the listener.
     */
    public void unregisterSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        mSteeringAngleListener.remove(steeringAngleListener);
    }

    /**
     * This method returns the Car steering angle in degrees. positive - right; negative - left.
     *  @return int - return current steering angle value in degrees.
     * -1 - if permission to access the steering angle api is denied
     * positive value - right
     * negative value - left
     */
    public int getSteeringAngle() {
        int steeringAngle = 0;
        try {
            steeringAngle = mService.getSteeringAngle(); // TODO: Do we need to return some value if there is a permission failure or just throwing security exception is good enough
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e instanceof SecurityException) {
                Log.d(TAG, e.getMessage());
                steeringAngle = -1;
            }
        }
        return steeringAngle;
    }

    /**
     * This method returns the Car VIN Number.
     *  @return int - return car VIN no
     *  -1 - if permission to access the vin no . else return valid vin no,
     */
    public String getVehicleIdentityNumber() {
        String vinNo = null;
        try {
            vinNo = mService.getVehicleIdentityNumber();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            if (e instanceof SecurityException) {
                Log.d(TAG, e.getMessage());
                Integer ret = -1;
                vinNo = ret.toString();
            }
        }
        Log.d(TAG,"VinNo: " + Integer.parseInt(vinNo));
        return vinNo;
    }
}