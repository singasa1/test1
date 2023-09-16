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

// Declare any non-default types here with import statements
import technology.cariad.partnerenablerservice.ICarDataChangeListener;

/**
 * Interface used to interact with the PartnerEnablerService.  Mostly this is used by the
 * PartnerLibrary class.
 * {@hide}
 */
interface IPartnerEnabler {

    /**
     * GetVehicleSignalIndicator Binder calls returns one of
     * the following signal indicator value.
    */
    const int VEHICLE_SIGNAL_INDICATOR_NONE = 0;
    const int VEHICLE_SIGNAL_INDICATOR_RIGHT = 1;
    const int VEHICLE_SIGNAL_INDICATOR_LEFT = 2;

    /**
     * GetFogLisghState Binder calls may returns one of
     * the following light state.
    */
    const int VEHICLE_LIGHT_STATE_OFF = 0;
    const int VEHICLE_LIGHT_STATE_ON = 1;
    const int VEHICLE_LIGHT_STATE_DAYTIME_RUNNING = 2;

    /**
     * This method initializes the required components in the PartnerEnablerService.
    */
    void initialize() = 0;

    /**
     * This method releases/destroy the components created in the PartnerEnablerService.
    */
    void release() = 1;

    /**
     * This method returns the Car current Odometer value. continuous change prop type
     * @return: It returns value ranges from 0 to 4294967293.
    */
    float getCurrentMileage() = 2;

    /**
     * This method returns the Car current signal indicator value. on_change prop type
     * 0 - None
     * 1 - Right
     * 2 - Left
    */
    int getTurnSignalIndicator() = 3;

    /**
     * This method returns the Car fog light state. on_change prop type
     * 0 - OFF
     * 1 - ON
     * 2 - DAYLIGHT_RUNNING
    */
    int getFogLightsState() = 4;
    /**
     * This method returns the Car steering angle in degrees. positive - right; negative - left. continuous change prop type.
     * @return: It returns value ranges from -1200 to 1200.
    */
    float getSteeringAngle() = 5;

    /**
     * This method returns the Car VIN Number.
    */
    String getVehicleIdentityNumber() = 6;

    /**
     * Registers a listener @link#ICarDataChangeListener to be called when the car data changes.
    */
    void addCarDataChangeListener(in ICarDataChangeListener listener) = 7;

    /**
    * Removes the provided listener from receiving the callbacks.
    */
    void removeCarDataChangeListener(in ICarDataChangeListener listener) = 8;

    /**
     * Returns the IBinder for the given service name
     * List of available ServiceName Strings are defined in @link#PartnerAPIConstants.
    */
    IBinder getAPIService(in String serviceName) = 9;
}