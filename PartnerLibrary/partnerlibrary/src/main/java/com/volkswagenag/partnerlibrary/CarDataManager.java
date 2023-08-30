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
package com.volkswagenag.partnerlibrary;

import androidx.annotation.RequiresPermission;

/**
 * <h1>CarDataManager</h1>
 * Car Data Manager provides wrapper apis for Vehicle data like mileage, steering angle etc.
 * Note: {@link PartnerLibraryManager#initialize()} must be called, to bind to the PartnerEnablerService,
 * before calling any methods in this interface.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public interface CarDataManager {

    /**
     * Returns the current Odometer value.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_CAR_MILEAGE_INFO}</p>
     *
     * @return {@link Response<Float>} - with value corresponding to current odometer value in Kilometer.
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)
    Response<Float> getCurrentMileage();

    /**
     * Add the {@link MileageListener} listener, which is called when Odometer/Mileage value changes.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_CAR_MILEAGE_INFO}</p>
     *
     * @param mileageListerer MileageListener object from client/app.
     * @return {@link Response.Status}
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_MILEAGE_INFO)
    Response.Status registerMileageListener(MileageListener mileageListener);

    /**
     * Remove the registered {@link MileageListener} listener.
     *
     * @return {@link Response.Status}
     */
    Response.Status unregisterMileageListener(MileageListener mileageListener);

    /**
     * Returns the current turn signal indicator value.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR}</p>
     *
     * @return {@link Response<VehicleSignalIndicator>} with value corresponding to current signal indicator value.
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)
    Response<VehicleSignalIndicator> getTurnSignalIndicator();

    /**
     * Add the {@link TurnSignalListener} listener, which is called when Vehicle Turn Signal State value changes.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR}</p>
     *
     * @param turnSignalListener TurnSignalListener object from client/app.
     * @return {@link Response.Status}
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR)
    Response.Status registerTurnSignalListener(TurnSignalListener turnSignalListener);

    /**
     * Remove the registered {@link TurnSignalListener} listener.
     *
     * @return {@link Response.Status}
     */
    Response.Status unregisterTurnSignalListener(TurnSignalListener turnSignalListener);

    /**
     * Returns the current fog lights state value.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_FOG_LIGHTS}</p>
     *
     * @return {@link Response<VehicleLightState>} - with value corresponding to current fog light state value.
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_FOG_LIGHTS)
    Response<VehicleLightState> getFogLightsState();

    /**
     * Add the {@link FogLightStateListener} listener, which is called when Fog Lights State value changes.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_FOG_LIGHTS}</p>
     *
     * @param lightStateListener FogLightStateListener object from client/app.
     * @return {@link Response.Status}
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_FOG_LIGHTS)
    Response.Status registerFogLightStateListener(FogLightStateListener lightStateListener);

    /**
     * Remove the registered {@link FogLightStateListener} listener.
     *
     * @return {@link Response.Status}
     */
    Response.Status unregisterFogLightStateListener(FogLightStateListener lightStateListener);

    /**
     * Returns the Car steering angle in degrees. positive - right; negative - left.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_STEERING_ANGLE_INFO}</p>
     *
     * @return {@link Response<Float>} - with value corresponding to current steering angle value in degrees.
     * positive value - right
     * negative value - left
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)
    Response<Float> getSteeringAngle();

    /**
     * Add the {@link SteeringAngleListener} listener, which is called when steering angle value changes.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_STEERING_ANGLE_INFO}</p>
     *
     * @param steeringAngleListener SteeringAngleListener object from client/app.
     * @return {@link Response.Status}
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_STEERING_ANGLE_INFO)
    Response.Status registerSteeringAngleListener(SteeringAngleListener steeringAngleListener);

    /**
     * Remove the registered {@link SteeringAngleListener} listener.
     *
     * @return {@link Response.Status}
     */
    Response.Status unregisterSteeringAngleListener(SteeringAngleListener steeringAngleListener);

    /**
     * Returns the Car VIN Number.
     * <p>Requires Permission: {@link PartnerLibraryManager#PERMISSION_RECEIVE_CAR_INFO_VIN}</p>
     *
     *  @return {@link Response<String>} - with value corresponding to car VIN no
     */
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_CAR_INFO_VIN)
    Response<String> getVehicleIdentityNumber();
}