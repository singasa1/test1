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
import technology.cariad.partnerenablerservice.ITurnSignalStateListener;

/**
 * Interface used to interact with the ExteriorLightService in PartnerEnablerService.  Mostly this is used by the
 * PartnerLibrary class.
 * {@hide}
 */
interface IExteriorLightService {

    /**
     * GetVehicleSignalIndicator Binder calls returns one of
     * the following signal indicator value.
    */
    const int VEHICLE_SIGNAL_INDICATOR_NONE = 0;
    const int VEHICLE_SIGNAL_INDICATOR_RIGHT = 1;
    const int VEHICLE_SIGNAL_INDICATOR_LEFT = 2;

    /**
     * This method returns the Car current signal indicator value. on_change prop type
     * 0 - None
     * 1 - Right
     * 2 - Left
    */
    int getTurnSignalIndicator();

    /**
     * Registers a listener @link#ITurnSignalStateListener to be called when the turn signal state changes.
    */
    void addTurnSignalStateListener(in ITurnSignalStateListener listener);

    /**
     * Removes the provided listener from receiving the callbacks.
    */
    void removeTurnSignalStateListener(in ITurnSignalStateListener listener);
}