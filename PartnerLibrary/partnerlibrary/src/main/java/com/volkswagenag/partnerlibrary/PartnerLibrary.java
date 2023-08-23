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
 * <h1>Partner Library</h1>
 * Partner Library provides wrapper apis for different app developers.
 * It has apis for getting the Active Route, Interior/Exterior Light status.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-04-20
 */
public interface PartnerLibrary {
    /**
     * Permission necessary to get the car current odomoter/mileage value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_CAR_INFO_VIN =
            "com.volkswagenag.restricted.permission.READ_INFO_VIN";

    /**
     * Permission necessary to get the car current odomoter/mileage value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_CAR_MILEAGE_INFO =
            "com.volkswagenag.restricted.permission.CAR_MILEAGE";

    /**
     * Permission necessary to get the car current turn signal indicator value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR =
            "com.volkswagenag.restricted.permission.READ_SIGNAL_INDICATOR";

    /**
     * Permission necessary to get the car fog lights info through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_FOG_LIGHTS =
            "com.volkswagenag.restricted.permission.READ_FOG_LIGHTS";

    /**
     * Permission necessary to get the car current steering angle value through {@link CarDataManager}.
     */
    public static final String PERMISSION_RECEIVE_STEERING_ANGLE_INFO =
            "com.volkswagenag.restricted.permission.READ_STEERING_ANGLE";

    /**
     * Permission necessary to get the car active route value through {@link NavigationManager}.
     */
    public static final String PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE =
            "com.volkswagenag.restricted.permission.READ_NAV_ACTIVE_ROUTE";

    /**
     * Permission necessary to get the phone state.
     */
    public static final String PERMISSION_RECEIVE_PHONE_STATE =
            "com.volkswagenag.restricted.permission.READ_PRIVILEGED_PHONE_STATE";

    /**
     * Returns the Singleton instance of PartnerLibrary to access Partner APIs
     * @param context Application context
     * @return {@link PartnerLibrary} instance
     */
    static PartnerLibrary getInstance(Context context) {
        return PartnerLibraryFactory.getPartnerLibraryInstance(context);
    }
    /**
     * This method binds to the PartnerEnabler service.
     */
    Response.Error initialize();

    /**
     * This method unbinds the PartnerEnabler service
     */
    Response.Error release();

    /**
     * This method initializes the PartnerEnabler service components
     */
    Response.Error start();

    /**
     * This method uninitializes the PartnerEnabler service components
     */
    Response.Error stop();

    /**
     * This method is to add the listener to get PartnerEnablerServiceConnection status.
     * @param listener ILibStateChangeListener object from client/app.
     */
    void addListener(ILibStateChangeListener listener);

    /**
     * This method is to remove the listener.
     */
    void removeListener(ILibStateChangeListener listener);

    /**
     * Get {@link CarDataManager} instance to get car related data/information
     * @return {@link CarDataManager}
     */
    Response<CarDataManager> getCarDataManager();

    /**
     * Get {@link NavigationManager} instance to get current route
     * @return {@link NavigationManager}
     */
    Response<NavigationManager> getNavigationManager();

}
