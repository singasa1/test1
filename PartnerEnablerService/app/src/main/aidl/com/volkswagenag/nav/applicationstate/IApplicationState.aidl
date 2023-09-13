// IApplicationState.aidl
package com.volkswagenag.nav.applicationstate;

// import the callbacks
import com.volkswagenag.nav.applicationstate.IApplicationStateCallback;

/**
 * Interface to request current state of navigation application and updates
 * to other friendly Android applications from CSO.
 * @deprecated use v2
*/
interface IApplicationState {

    /**
     * Minor interface version
     */
    const int VERSION = 1;

    /**
     * Request the minor version of the interface that is provided by the service.
     */
    int getIfcVersion() = 1;

    /**
     * Register to receive the navigation application state updates
     *
     * @param callback callback to get the current state of navigation application
     *                 when ever the state is changed
     */
    oneway void registerCallback(
        in IApplicationStateCallback callback) = 100;

    /**
     * Unregister to receive the navigation application state updates
     *
     * @param callback callback to get the current state of navigation application
     *                  when ever the state is changed
     */
    oneway void unregisterCallback(
        in IApplicationStateCallback callback) = 200;

}
