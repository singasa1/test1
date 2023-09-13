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

import android.content.Context;

import com.volkswagenag.partnerlibrary.impl.PartnerLibraryFactory;

/**
 * <h1>PartnerLibraryManager</h1>
 * PartnerLibraryManager provides APIs to initialize, get and use Partner APIs.
 * Use this interface to maintain the connection with PartnerEnabler Service and
 * to get other Partner API instances.
 *
 * @author CARIAD Inc
 * @version 1.0
 * @since 2023-04-20
 */
public interface PartnerLibraryManager {
    /**
     * Permission string to get the car current odomoter/mileage value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_CAR_INFO_VIN =
            "com.volkswagenag.restricted.permission.READ_INFO_VIN";

    /**
     * Permission string to get the car current odomoter/mileage value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_CAR_MILEAGE_INFO =
            "com.volkswagenag.restricted.permission.READ_CAR_MILEAGE";

    /**
     * Permission string to get the car current turn signal indicator value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR =
            "com.volkswagenag.restricted.permission.READ_SIGNAL_INDICATOR";

    /**
     * Permission string to get the car fog lights info through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_FOG_LIGHTS =
            "com.volkswagenag.restricted.permission.READ_FOG_LIGHTS";

    /**
     * Permission string to get the car current steering angle value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_STEERING_ANGLE_INFO =
            "com.volkswagenag.restricted.permission.READ_STEERING_ANGLE";

    /**
     * Permission string to get the car active route value through {@link NavigationManager}.
     */
    public static final String PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE =
            "com.volkswagenag.restricted.permission.READ_NAV_ACTIVE_ROUTE";

    /**
     * Permission string to get the phone state.
     */
    public static final String PERMISSION_RECEIVE_PHONE_STATE =
            "com.volkswagenag.restricted.permission.READ_PRIVILEGED_PHONE_STATE";

    public static final String EXTERIOR_LIGHT = "IExteriorLight";
    public static final String NAVIGATION = "INavigation";

    /**
     * Returns the Singleton instance of {@link PartnerLibraryManager} to access Partner APIs
     *
     * @param context Application context
     * @return {@link PartnerLibraryManager} instance
     */
    static PartnerLibraryManager getInstance(Context context) {
        return PartnerLibraryFactory.getPartnerLibraryInstance(context);
    }
    /**
     * Binds to the PartnerEnabler Service.
     *
     * @return {@link Response.Status}
     */
    Response.Status initialize();

    /**
     * Un-binds from the PartnerEnabler service
     *
     * @return {@link Response.Status}
     */
    Response.Status release();

    /**
     * Initializes the PartnerEnabler service components.
     * Note: {@link PartnerLibraryManager#initialize()} must be called, to bind to the
     * PartnerEnablerService, before calling this method.
     *
     * @return {@link Response.Status}
     */
    Response.Status start();

    /**
     * Uninitializes the PartnerEnabler service components.
     * Note: {@link PartnerLibraryManager#initialize()} must be called, to bind to the
     * PartnerEnablerService, before calling this method.
     *
     * @return {@link Response.Status}
     */
    Response.Status stop();

    /**
     * Add the {@link ILibStateChangeListener} which will be called when PartnerEnabler Service
     * Connection status changes.
     *
     * @param listener {@link ILibStateChangeListener} object from client/app.
     */
    void addListener(ILibStateChangeListener listener);

    /**
     * Remove the registered {@link ILibStateChangeListener}.
     *
     * @param listener {@link ILibStateChangeListener} object from client/app.
     */
    void removeListener(ILibStateChangeListener listener);

    /**
     * Get {@link CarDataManager} instance to get car related data/information.
     * Note: {@link PartnerLibraryManager#initialize()} must be called, to bind to the
     * PartnerEnablerService, before calling this method.
     *
     * @return {@link Response<CarDataManager>} instance
     */
    Response<CarDataManager> getCarDataManager();

    /**
     * Get {@link NavigationManager} instance to get current route.
     * Note: {@link PartnerLibraryManager#initialize()} must be called, to bind to the
     * PartnerEnablerService, before calling this method.
     *
     * @return {@link Response<NavigationManager>} instance
     */
    Response<NavigationManager> getNavigationManager();

}
