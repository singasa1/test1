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
 * <h1>Navigation Manager</h1>
 * Navigation Manager provides wrapper apis for navigation related data such as navigation app state and route.
 * Note: {@link PartnerLibrary#initialize()} must be called, to bind to the PartnerEnablerService,
 * before calling any methods in this interface.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-08-21
 */
public interface NavigationManager {

    /**
     * Returns the Navigation Application state.
     * <p>Requires Permission: {@link PartnerLibrary#PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE}</p>
     *
     * @return {@link Response<Boolean>} with value:
     *         true - if Navigation Application state is fully operable.
     *         false - if Navigation Application state is Loading, NavDB Error, NoLicense, etc,.
     */
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    Response<Boolean> isNavAppStarted();

    /**
     * Add the {@link NavAppStateListener} listener, which is called when Navigation Core App status changes.
     * <p>Requires Permission: {@link PartnerLibrary#PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE}</p>
     *
     * @param listener NavStateListener object from client/app.
     * @return {@link Response.Status}
     */
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    Response.Status registerNavStateListener(NavAppStateListener listener );

    /**
     * Remove the registered {@link NavAppStateListener} listener.
     *
     * @param listener NavStateListener object from client/app.
     * @return {@link Response.Status}
     */
    Response.Status unregisterNavStateListener(NavAppStateListener listener);

    /**
     * Returns the route guidance of the current active route from Navigation Application.
     * <p>Requires Permission: {@link PartnerLibrary#PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE}</p>
     *
     * @return {@link Response<String>} with value:
     *         A JSON string with the current route encoded using flexible polyline encoding.
     *         ex: {"version": 1, "route": "<route encoded as polyline>"} OR
     *         null - if there is no active route.
     */
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    Response<String> getActiveRoute();

    /**
     * Add the {@link ActiveRouteUpdateListener} listener to get the active guided route from Navigation App.
     * <p>Requires Permission: {@link PartnerLibrary#PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE}</p>
     *
     * @param activeRouteUpdateListener ActiveRouteUpdateListener object from client/app.
     * @return {@link Response.Status}
     */
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    Response.Status registerActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener);

    /**
     * Remove the registered {@link ActiveRouteUpdateListener} listener that is registered to get
     * the active route from Navigation App.
     *
     * @param activeRouteUpdateListener ActiveRouteUpdateListener object from client/app.
     * @return {@link Response.Status}
     */
    Response.Status unregisterActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener);
}