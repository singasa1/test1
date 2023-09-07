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
package com.volkswagenag.partnerlibrary.impl;

import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_LIGHT_STATE_DAYTIME_RUNNING;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_LIGHT_STATE_OFF;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_LIGHT_STATE_ON;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_SIGNAL_INDICATOR_LEFT;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_SIGNAL_INDICATOR_NONE;
import static technology.cariad.partnerenablerservice.IPartnerEnabler.VEHICLE_SIGNAL_INDICATOR_RIGHT;

import android.os.RemoteException;
import android.util.Log;

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.FogLightStateListener;
import com.volkswagenag.partnerlibrary.MileageListener;
import com.volkswagenag.partnerlibrary.SteeringAngleListener;
import com.volkswagenag.partnerlibrary.TurnSignalListener;
import com.volkswagenag.partnerlibrary.VehicleLightState;
import com.volkswagenag.partnerlibrary.VehicleSignalIndicator;

import java.util.HashSet;

import technology.cariad.partnerenablerservice.ICarDataChangeListener;
import technology.cariad.partnerenablerservice.IPartnerEnabler;

/**
 * <h1>Partner Library</h1>
 * Partner Library provides wrapper apis for different app developers.
 * It has signature verification apis and other apis for getting the Active Route, Interior/Exterior Light status.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public class CarDataManagerImpl implements CarDataManager {
    private static final String TAG = CarDataManagerImpl.class.getSimpleName();

    private IPartnerEnabler mService;

    private ICarDataChangeListener mCarDataChangeListener = new CarDataChangeListener();

    private HashSet<MileageListener> mMileageListeners = new HashSet<>();
    private HashSet<TurnSignalListener> mTurnSignalListener = new HashSet<>();
    private HashSet<FogLightStateListener> mFogLightStateListener = new HashSet<>();
    private HashSet<SteeringAngleListener> mSteeringAngleListener = new HashSet<>();

    public CarDataManagerImpl(IPartnerEnabler service) {
        Log.d(TAG,"CarDataManager");
        mService = service;
        addCarDataListener();
    }

    private void addCarDataListener() {
        try {
            if (!isClientListenerNotRegistered()) {
                mService.addCarDataChangeListener(mCarDataChangeListener);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private void removeCarDataListener() {
        try {
            if (isClientListenerNotRegistered()) {
                mService.removeCarDataChangeListener(mCarDataChangeListener);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isClientListenerNotRegistered() {
        boolean retVal = false;
        if (mMileageListeners.isEmpty() && mTurnSignalListener.isEmpty() &&
                mFogLightStateListener.isEmpty() && mSteeringAngleListener.isEmpty()) {
            retVal = true;
        }
        return retVal;
    }

    /**
     * This method is to add the listener to get Odometer/Mileage value.
     * @param mileageListerer MileageListener object from client/app.
     * if client doesn't have permission to access odometer value, it doesn't add this listener
     */
    public void registerMileageListener(MileageListener mileageListener) {
        // Add this client to listeners only if it has permission to access the odometer value by calling getCurrentMileage
        if (getCurrentMileage() != -1) {
            mMileageListeners.add(mileageListener);
        }
    }

    /**
     * This method is to remove the listener.
     */
    public void unregisterMileageListener(MileageListener mileageListener) {
        mMileageListeners.remove(mileageListener);
        removeCarDataListener();
    }

    /**
     * This method gets the Car current Odometer value from PartnerEnablerService
     * @return float - return current odometer value in Kilometer
     * -1 - if permission to access the odometer api is denied
     *  0 - if there is no odometer value/vhal property available.
     */
    public float getCurrentMileage() {
        float mileage = 0.0f;
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
        mTurnSignalListener.remove(turnSignalListener);
        removeCarDataListener();
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
            indicator = convertTurnSignalIndicator(retVal);
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
        removeCarDataListener();
    }

    /**
     * This method gets the current fog lights state from PartnerEnablerService
     * @return VehicleLightState - return current fog light state value(PermissionDenied/Off/On/DayTimeRunning).
     */
    public VehicleLightState getFogLightsState() {
        VehicleLightState lightState = VehicleLightState.OFF;
        try {
            int retVal = mService.getFogLightsState();
            lightState = convertToVehicleLightState(retVal);
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
        removeCarDataListener();
    }

    /**
     * This method returns the Car steering angle in degrees. positive - right; negative - left.
     *  @return float - return current steering angle value in degrees.
     * -1 - if permission to access the steering angle api is denied
     * positive value - right
     * negative value - left
     */
    public float getSteeringAngle() {
        float steeringAngle = 0;
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

    private VehicleLightState convertToVehicleLightState(int lightState) {
        VehicleLightState retVal = VehicleLightState.OFF;
        switch (lightState) {
            case VEHICLE_LIGHT_STATE_ON:
                retVal = VehicleLightState.ON;
                break;
            case VEHICLE_LIGHT_STATE_DAYTIME_RUNNING:
                retVal = VehicleLightState.DAYTIME_RUNNING;
                break;
            case VEHICLE_LIGHT_STATE_OFF:
            default:
                retVal = VehicleLightState.OFF;
                break;
        }
        return retVal;
    }

    private VehicleSignalIndicator convertTurnSignalIndicator(int indicator) {
        VehicleSignalIndicator turnSignalIndicator = VehicleSignalIndicator.NONE;
        switch (indicator) {
            case VEHICLE_SIGNAL_INDICATOR_RIGHT:
                turnSignalIndicator = VehicleSignalIndicator.RIGHT;
                break;
            case VEHICLE_SIGNAL_INDICATOR_LEFT:
                turnSignalIndicator = VehicleSignalIndicator.LEFT;
                break;
            case VEHICLE_SIGNAL_INDICATOR_NONE:
            default:
                turnSignalIndicator = VehicleSignalIndicator.NONE;
                break;
        }
        return turnSignalIndicator;
    }

    private class CarDataChangeListener extends ICarDataChangeListener.Stub {
        public void onMileageValueChanged(float mileageValue) {
            Log.d(TAG, "calling listener onMileageValueChanged with value: " + mileageValue);
            for(MileageListener listener: mMileageListeners) {
                listener.onMileageValueChanged(mileageValue);
            }
        }

        public void onFogLightsChanged(int fogLightState) {
            Log.d(TAG, "calling listener onFogLightStateChange with value: " + fogLightState);
            for(FogLightStateListener listener: mFogLightStateListener) {
                VehicleLightState lightState = convertToVehicleLightState(fogLightState);
                listener.onFogLightsChanged(lightState);
            }
        }

        public void onSteeringAngleChanged(float steeringAngle) {
            Log.d(TAG, "calling listener onSteeringAngleChanged with value: " + steeringAngle);
            for(SteeringAngleListener listener: mSteeringAngleListener) {
                listener.onSteeringAngleChanged(steeringAngle);
            }
        }

        public void onTurnSignalStateChanged(int signalIndicator) {
            Log.d(TAG, "calling listener onTurnSignalStateChanged with value: " + signalIndicator);
            for(TurnSignalListener listener: mTurnSignalListener) {
                VehicleSignalIndicator indicator = convertTurnSignalIndicator(signalIndicator);
                listener.onTurnSignalStateChanged(indicator);
            }
        }
    }
}