// IApplicationStateCallback.aidl
package com.volkswagenag.nav.applicationstate;

// import the type
import com.volkswagenag.nav.applicationstate.ApplicationStateType;

/**
 * Callback interface to provide current state of navigation application and updates
 * to other friendly Android applications from CSO.
 * @deprecated use v2
 */
interface IApplicationStateCallback {

    /**
     * Returns the current state of navigation application.
     *
     * @param applicationState The applicationState represents the state of the navigation
     *                         application. The navigation application is
     */
    oneway void onCurrentState(ApplicationStateType applicationState) = 100;

    /**
     * Returns the current loading state of navigation application.
     *
     * @param loadingState The oadingState represents the bargraph 
     *                     which indicates the state of initialization and 
     *                     loading of the navigation application. It is 
     *                     valid for the applicationState INIT_LOADING, 
     *                     USER_SWITCH and DATABASE_UPDATE. 
     *                     The value is 100 (%) in FULLY_OPERABLE state and
     *                     all other 0 (%).
     */
    oneway void onLoadingState(int loadingState) = 200;
}
