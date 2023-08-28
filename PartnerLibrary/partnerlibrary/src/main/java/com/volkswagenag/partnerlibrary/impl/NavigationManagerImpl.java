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
package com.volkswagenag.partnerlibrary.impl;

import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.NavStateListener;
import com.volkswagenag.partnerlibrary.ActiveRouteUpdateListener;
import com.volkswagenag.partnerlibrary.PartnerLibrary;
import com.volkswagenag.partnerlibrary.Response;

import java.util.HashSet;

import technology.cariad.partnerenablerservice.IPartnerEnabler;

/**
 * <h1>Partner Library</h1>
 * Navigation Manager Implementation provides implementation to get the navigation related information from PartnerEnablerService.
 *
 * @author Sathya Singaravelu
 * @version 1.0
 * @since 2023-08-21
 */
public class NavigationManagerImpl implements NavigationManager {
    private static final String TAG = NavigationManagerImpl.class.getSimpleName();

    private IPartnerEnabler mService;

    //private INavigationChangeListener mNavigationListener = new Navigation();

    private HashSet<NavStateListener> mNavStateListener = new HashSet<NavStateListener>();
    private HashSet<ActiveRouteUpdateListener> mActiveRouteListener = new HashSet<ActiveRouteUpdateListener>();

    public NavigationManagerImpl(IPartnerEnabler service) {
        Log.d(TAG,"NavigationManagerImpl");
        mService = service;
        addNavStateListener();
    }

    private void addNavStateListener() {
//        try {
//            // TODO: real impl to register it on the PES.
//            //mService.addNavStateListener(mCarDataChangeListener);
//        } catch (RemoteException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void removeNavStateListener() {
//        try {
//            if (isClientListenerNotRegistered()) {
//                mService.removeNavStateListener(mCarDataChangeListener);
//            }
//        } catch (RemoteException e) {
//            throw new RuntimeException(e);
//        }
    }

    private boolean isClientListenerNotRegistered() {
        boolean retVal = false;
        if (mNavStateListener.isEmpty() && mActiveRouteListener.isEmpty()) {
            retVal = true;
        }
        return retVal;
    }

    /**
     * This method is to add the listener to get Navigation Core App status.
     * @param navStateListener NavStateListener object from client/app.
     */
    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response.Status registerNavStateListener(NavStateListener navStateListener ) {
        // Add this client to listeners only if it has permission to access the navigation app state
        // TODO: Need to do Real permission check based implementation and error communication
        mNavStateListener.add(navStateListener);
        return Response.Status.SUCCESS;
    }

    /**
     * This method is to remove the listener.
     */
    @Override
    public Response.Status unregisterNavStateListener(NavStateListener navStateListener) {
        mNavStateListener.remove(navStateListener);
        removeNavStateListener();
        return Response.Status.SUCCESS;
    }

    /**
     * This method is to get the Navigation Application state.
     * @return Returns true - if Navigation Application state is fully operable.
     *         Returns false - if Navigation Application state is Loading, NavDB Error, NoLicense, etc,.
     */
    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response<Boolean> isNavStarted() {
        return new Response<>(Response.Status.SUCCESS, new Boolean(true));
        // TODO: Add real implementation
    }

    /**
     * This method is to add the listener to get the active guided route from Navigation App.
     * @param activeRouteUpdateListener ActiveRouteUpdateListener object from client/app.
     */
    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response.Status registerActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        // Add this client to listeners only if it has permission to access the navigation simplified route
        // TODO: Need to do Real permission check based implementation and error communication
        mActiveRouteListener.add(activeRouteUpdateListener);
        return Response.Status.SUCCESS;
    }

    /**
     * This method is to remove the callback that is registered to get the active route from Navigation App.
     * @param activeRouteUpdateListener ActiveRouteUpdateListener object from client/app.
     */
    @Override
    public Response.Status unregisterActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        mActiveRouteListener.remove(activeRouteUpdateListener);
        return Response.Status.SUCCESS;
    }

    /**
     * This method is to get the route guidance of the current active route from Navigation Application.
     * @return Returns null - if there is no active route.
     *         Returns a JSON string with the current route encoded using flexible polyline encoding.
     *         ex: {"version": 1, "route": "<route encoded as polyline>"}
     */
    @Override
    @RequiresPermission(PartnerLibrary.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response<String> getActiveRoute() {
        return new Response<>(Response.Status.SUCCESS, null);
        // TODO: Real implementation need to be added to hook up with PartnerEnablerService.
    }
}