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

import androidx.annotation.RequiresPermission;

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.FogLightStateListener;
import com.volkswagenag.partnerlibrary.MileageListener;
import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.Response;
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

    private final IPartnerEnabler mService;

    private final ICarDataChangeListener mCarDataChangeListener = new CarDataChangeListener();

    private final HashSet<MileageListener> mMileageListeners = new HashSet<MileageListener>();
    private final HashSet<TurnSignalListener> mTurnSignalListener = new HashSet<TurnSignalListener>();
    private final HashSet<FogLightStateListener> mFogLightStateListener = new HashSet<FogLightStateListener>();
    private final HashSet<SteeringAngleListener> mSteeringAngleListener = new HashSet<SteeringAngleListener>();

    public CarDataManagerImpl(IPartnerEnabler service) {
        Log.d(TAG,"CarDataManager");
        mService = service;
        addCarDataListener();
    }

    private void addCarDataListener() {
        try {
            mService.addCarDataChangeListener(mCarDataChangeListener);
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
        boolean retVal = mMileageListeners.isEmpty() && mTurnSignalListener.isEmpty() &&
                mFogLightStateListener.isEmpty() && mSteeringAngleListener.isEmpty();
        return retVal;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)
    public Response<Float> getCurrentMileage() {
        Response<Float> response = new Response<>(Response.Status.VALUE_NOT_AVAILABLE, 0.0f);
        try {
            response.value = mService.getCurrentMileage();
            response.status = Response.Status.SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.status = Response.Status.INTERNAL_FAILURE;
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            response.status = Response.Status.PERMISSION_DENIED;
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            response.status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
        }
        return response;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)
    public Response.Status registerMileageListener(MileageListener mileageListener) {
        // Add this client to listeners only if it has permission to access the odometer value by calling getCurrentMileage
        Response.Status status = getCurrentMileage().status;
        if (status == Response.Status.SUCCESS) {
            mMileageListeners.add(mileageListener);
        }
        return status;
    }

    @Override
    public Response.Status unregisterMileageListener(MileageListener mileageListener) {
        mMileageListeners.remove(mileageListener);
        removeCarDataListener();
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)
    public Response<VehicleSignalIndicator> getTurnSignalIndicator() {
        Response<VehicleSignalIndicator> response = new Response<>(Response.Status.VALUE_NOT_AVAILABLE, VehicleSignalIndicator.NONE);
        try {
            response.value = convertTurnSignalIndicator(mService.getTurnSignalIndicator());
            response.status = Response.Status.SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.status = Response.Status.INTERNAL_FAILURE;
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            response.status = Response.Status.PERMISSION_DENIED;
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            response.status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
        }
        return response;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)
    public Response.Status registerTurnSignalListener(TurnSignalListener turnSignalListener) {
        // Add this client to listeners only if it has permission to access the turn signal indicator value by calling getTurnSignalIndicator
        Response.Status status = getTurnSignalIndicator().status;
        if (status == Response.Status.SUCCESS) {
            mTurnSignalListener.add(turnSignalListener);
        }
        return status;
    }

    @Override
    public Response.Status unregisterTurnSignalListener(TurnSignalListener turnSignalListener) {
        mTurnSignalListener.remove(turnSignalListener);
        removeCarDataListener();
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_FOG_LIGHTS)
    public Response<VehicleLightState> getFogLightsState() {
        Response<VehicleLightState> response = new Response<>(Response.Status.VALUE_NOT_AVAILABLE, VehicleLightState.OFF);
        try {
            response.value = convertToVehicleLightState(mService.getFogLightsState());
            response.status = Response.Status.SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.status = Response.Status.INTERNAL_FAILURE;
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            response.status = Response.Status.PERMISSION_DENIED;
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            response.status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
        }
        return response;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_FOG_LIGHTS)
    public Response.Status registerFogLightStateListener(FogLightStateListener lightStateListener) {
        Response.Status status = getFogLightsState().status;
        // Add this client to listeners only if it has permission to access the fog light state value by calling getFogLightState
        if (status == Response.Status.SUCCESS) {
            mFogLightStateListener.add(lightStateListener);
        }
        return status;
    }

    @Override
    public Response.Status unregisterFogLightStateListener(FogLightStateListener lightStateListener) {
        mFogLightStateListener.remove(lightStateListener);
        removeCarDataListener();
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)
    public Response<Float> getSteeringAngle() {
        Response<Float> response = new Response<>(Response.Status.VALUE_NOT_AVAILABLE, 0.0f);
        try {
            response.value = mService.getSteeringAngle();
            response.status = Response.Status.SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.status = Response.Status.INTERNAL_FAILURE;
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            response.status = Response.Status.PERMISSION_DENIED;
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            response.status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
        }
        return response;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)
    public Response.Status registerSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        Response.Status status = getSteeringAngle().status;
        // Add this client to listeners only if it has permission to access the steering angle value by calling getSteeringAngle
        if (status == Response.Status.SUCCESS) {
            mSteeringAngleListener.add(steeringAngleListener);
        }
        return status;
    }
    @Override
    public Response.Status unregisterSteeringAngleListener(SteeringAngleListener steeringAngleListener) {
        mSteeringAngleListener.remove(steeringAngleListener);
        removeCarDataListener();
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_CAR_INFO_VIN)
    public Response<String> getVehicleIdentityNumber() {
        Response<String> response = new Response<>(Response.Status.VALUE_NOT_AVAILABLE, null);
        try {
            response.value = mService.getVehicleIdentityNumber();
            response.status = Response.Status.SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.status = Response.Status.INTERNAL_FAILURE;
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            response.status = Response.Status.PERMISSION_DENIED;
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            response.status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
        }
        Log.d(TAG,"VinNo: " + response.value);
        return response;
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