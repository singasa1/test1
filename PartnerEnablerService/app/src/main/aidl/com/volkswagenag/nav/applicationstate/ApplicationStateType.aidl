// ApplicationStateType.aidl
package com.volkswagenag.nav.applicationstate;

/**
 * Definition of the navigation application states including priority order.
 * @deprecated use v2
 */

// ATTENTION: Support for Enums with AIDL-Tool > Android SDK-Build-Tools 29
@Backing(type="int")
enum ApplicationStateType {

    /**
     * No priority - used as fallback when any other state doesn't work
     */
    UNKNOWN = 0,

    /**
     * Priority 1 (Highest) - Navigation isn't working as expected:
     *                        Navigation application can't be started completely. 
     *                        It supports PSD services only.
     */
    PSD_SERVICE_ONLY = 10,

    /**
     * Priority 1 (Highest) - Navigation isn't working as expected:
     *                        Navigation application is started without valid
     *                        license and wait for activation with valid one.
     */
    NO_LICENSE = 15,

    /**
     * Priority 2           - Navigation isn't working as expected:
     *                        Navigation database will be updated at the moment 
     *                        by customer update process so that it isn't available.
     */
    DATABASE_UPDATE = 20,

    /**
     * Priority 3           - Navigation isn't working as expected:
     *                        Current user doesn't have necessary authentication
     *                        level to have access to user data.
     */
    RESTRICTED_USER = 30,

    /**
     * Priority 4           - Navigation isn't working as expected:
     *                        Navigation isn't allowed to use current position
     *                        of the vehicle.
     */
    RESTRICTED_POSITION = 40,

    /**
     * Priority 5           - Navigation isn't working as expected:
     *                        Database of navigation application is blocked
     *                        because of problems inside database.
     */
    DATABASE_ERROR = 50,

    /**
     * Priority 6           - Navigation isn't working as expected:
     *                        Database of navigation application is blocked
     *                        because of an internal and unknown error state.
     *                        It should never happen.
     */
    ERROR = 60,

    /**
     * Priority 7           - Navigation isn't working as expected:
     *                        Navigation is in start-up phase.
     *                        Current process-state is provided by
     *                        initLoadingState attribute.
     */
    INIT_LOADING = 70,

    /**
     * Priority 8 (Lowest)  - Navigation working as expected.
    */
    OPERABLE = 80,
}