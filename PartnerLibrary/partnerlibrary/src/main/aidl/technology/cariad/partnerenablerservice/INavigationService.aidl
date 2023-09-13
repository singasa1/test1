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
import technology.cariad.partnerenablerservice.INavAppStateListener;
import technology.cariad.partnerenablerservice.IActiveRouteUpdateListener;

/**
 * Interface used to interact with the NavigationService in PartnerEnablerService.  Mostly this is used by the
 * PartnerLibrary class.
 * {@hide}
 */
interface INavigationService {

    /**
     * getNavigationApplicationState returns one of the following values
    */
    const int NAV_APP_STATE_NOT_READY = 0;
    const int NAV_APP_STATE_READY = 80;

    /**
     * This method returns the Navigation Application State
     * 0 - NOT_READY
     * 1 - READY
    */
    int getNavigationApplicationState();

    /**
     * Registers a listener @link#INavAppStateListener to be called when the turn signal state changes.
    */
    void addNavAppStateListener(in INavAppStateListener listener);

    /**
     * Removes the provided listener from receiving the callbacks.
    */
    void removeNavAppStateListener(in INavAppStateListener listener);

    /**
     * This method returns the JSON string with the current route encoded using flexible polyline encoding.
     * ex: {"version": 1, "route": "<route encoded as polyline>"} OR
     * null - if there is no active route.
    */
    String getActiveRoute();

    /**
     * Registers a listener @link#IActiveRouteUpdateListener to be called when the active route changes.
    */
    void addActiveRouteUpdateListener(in IActiveRouteUpdateListener listener);

    /**
    * Removes the provided listener from receiving the callbacks.
    */
    void removeActiveRouteUpdateListener(in IActiveRouteUpdateListener listener);
}