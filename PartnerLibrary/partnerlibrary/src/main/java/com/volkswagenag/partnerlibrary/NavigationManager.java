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
 * <h1>Navigation Manager</h1>
 * Navigation Manager provides wrapper apis for navigation related data such as navigation app state and route.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-08-21
 */
public interface NavigationManager {

    /**
     * This method is to add the listener to get Navigation Core App status.
     * @param listener NavStateListener object from client/app.
     */
    void registerNavStateListener(NavStateListener listener );

    /**
     * This method is to remove the navigation state listener.
     */
    void unregisterNavStateListener(NavStateListener listener);

    /**
     * This method is to get the Navigation Application state.
     * @return Returns true - if Navigation Application state is fully operable.
     *         Returns false - if Navigation Application state is Loading, NavDB Error, NoLicense, etc,.
     */
    boolean isNavStarted();

    /**
     * This method is to add the listener to get the active guided route from Navigation App.
     * @param activeRouteUpdateListener ActiveRouteUpdateListener object from client/app.
     */
    void registerActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener);

    /**
     * This method is to remove the callback that is registered to get the active route from Navigation App.
     * @param activeRouteUpdateListener ActiveRouteUpdateListener object from client/app.
     */
    void unregisterActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener);

    /**
     * This method is to get the route guidance of the current active route from Navigation Application.
     * @return Returns null - if there is no active route.
     *         Returns a JSON string with the current route encoded using flexible polyline encoding.
     *         ex: {"version": 1, "route": "<route encoded as polyline>"}
     */
    String getActiveRoute();
}