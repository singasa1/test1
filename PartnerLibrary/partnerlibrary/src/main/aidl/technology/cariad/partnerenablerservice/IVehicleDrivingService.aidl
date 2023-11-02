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
import technology.cariad.partnerenablerservice.ISteeringAngleChangeListener;
import technology.cariad.partnerenablerservice.IOdometerValueChangeListener;

/**
 * Interface used to interact with the VehicleDrivingService in PartnerEnablerService. This interface supports
 * APIs to get vehicle driving related information.
 * Mostly this is used by the PartnerLibrary class.
 * {@hide}
 */
interface IVehicleDrivingService {

    /**
     * Version number of the aidl interface
    */
    const int VERSION = 1;

    /**
     * Get the AIDL interface version number
    */
    int getIfcVersion();

    /**
     * This method returns the Car current Odometer value. continuous change prop type
     * @return: It returns value ranges from 0 to 4294967293.
    */
    float getCurrentMileage();

    /**
     * Registers a listener @link#IOdometerValueChangeListener to be called when the odometer value changes.
    */
    void addOdometerValueChangeListener(in IOdometerValueChangeListener listener);

    /**
     * Removes the provided listener from receiving the callbacks.
    */
    void removeOdometerValueChangeListener(in IOdometerValueChangeListener listener);

    /**
     * This method returns the Car steering angle in degrees. positive - right; negative - left. continuous change prop type.
     * @return: It returns value ranges from -1200 to 1200.
    */
    float getSteeringAngle();

    /**
     * Registers a listener {@link ISteeringAngleChangeListener} to be called when the steering angle changes.
    */
    void addSteeringAngleChangeListener(in ISteeringAngleChangeListener listener);

    /**
    * Removes the provided listener from receiving the callbacks.
    */
    void removeSteeringAngleChangeListener(in ISteeringAngleChangeListener listener);

}