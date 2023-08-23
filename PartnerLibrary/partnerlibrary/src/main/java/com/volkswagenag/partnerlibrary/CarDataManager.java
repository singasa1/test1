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

/**
 * <h1>Partner Library</h1>
 * Partner Library provides wrapper apis for different app developers.
 * It has signature verification apis and other apis for getting the Active Route, Interior/Exterior Light status.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public interface CarDataManager {

    /**
     * This method is to add the listener to get Odometer/Mileage value.
     * @param mileageListerer MileageListener object from client/app.
     * if client doesn't have permission to access odometer value, it doesn't add this listener
     *
     * @return {@link Response.Error}
     */
    Response.Error registerMileageListener(MileageListener mileageListener);

    /**
     * This method is to remove the listener.
     *
     * @return {@link Response.Error}
     */
    Response.Error unregisterMileageListener(MileageListener mileageListener);

    /**
     * This method gets the Car current Odometer value from PartnerEnablerService
     *
     * @return {@link Response<Float>} - with value corresponding to current odometer value in Kilometer.
     */
    Response<Float> getCurrentMileage();

    /**
     * This method is to add the listener to get Vehicle Turn Signal State value.
     * @param turnSignalListener TurnSignalListener object from client/app.
     * if client doesn't have permission to access the turn signal indicator value, it doesn't add this listener
     *
     * @return {@link Response.Error}
     */
    Response.Error registerTurnSignalListener(TurnSignalListener turnSignalListener);

    /**
     * This method is to remove the listener.
     *
     * @return {@link Response.Error}
     */
    Response.Error unregisterTurnSignalListener(TurnSignalListener turnSignalListener);

    /**
     * This method gets the current turn signal indicator value from PartnerEnablerService
     * @return {@link Response<VehicleSignalIndicator>} with value corresponding to current signal indicator value.
     *
     */
    Response<VehicleSignalIndicator> getTurnSignalIndicator();

    /**
     * This method is to add the listener to get Fog Lights State value.
     * @param lightStateListener FogLightStateListener object from client/app.
     * if client doesn't have permission to access the fog light state value, it doesn't add this listener
     *
     * @return {@link Response.Error}
     */
    Response.Error registerFogLightStateListener(FogLightStateListener lightStateListener);

    /**
     * This method is to remove the listener.
     *
     * @return {@link Response.Error}
     */
    Response.Error unregisterFogLightStateListener(FogLightStateListener lightStateListener);

    /**
     * This method gets the current fog lights state from PartnerEnablerService
     *
     * @return {@link Response<VehicleLightState>} - with value corresponding to current fog light state value.
     */
    Response<VehicleLightState> getFogLightsState();

    /**
     * This method is to add the listener to get steering angle value.
     * @param steeringAngleListener SteeringAngleListener object from client/app.
     * if client doesn't have permission to access the fog light state value, it doesn't add this listener.
     *
     * @return {@link Response.Error}
     */
    Response.Error registerSteeringAngleListener(SteeringAngleListener steeringAngleListener);

    /**
     * This method is to remove the listener.
     *
     * @return {@link Response.Error}
     */
    Response.Error unregisterSteeringAngleListener(SteeringAngleListener steeringAngleListener);

    /**
     * This method returns the Car steering angle in degrees. positive - right; negative - left.
     *
     * @return {@link Response<Float>} - with value corresponding to current steering angle value in degrees.
     * positive value - right
     * negative value - left
     */
    Response<Float> getSteeringAngle();

    /**
     * This method returns the Car VIN Number.
     *
     *  @return {@link Response<String>} - with value corresponding to car VIN no
     */
    Response<String> getVehicleIdentityNumber();
}