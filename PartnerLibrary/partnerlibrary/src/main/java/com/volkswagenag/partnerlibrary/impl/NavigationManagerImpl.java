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

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.NavAppStateListener;
import com.volkswagenag.partnerlibrary.ActiveRouteUpdateListener;
import com.volkswagenag.partnerlibrary.PartnerLibraryManager;
import com.volkswagenag.partnerlibrary.Response;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import technology.cariad.partnerenablerservice.INavAppStateListener;
import technology.cariad.partnerenablerservice.INavigationService;
import technology.cariad.partnerenablerservice.IPartnerEnabler;

/**
 * <h1>NavigationManagerImpl</h1>
 * Navigation Manager Impl provides implementation for NavigationManager wrapper apis for navigation related data such as navigation app state and route.
 * Note: {@link PartnerLibraryManager#initialize()} must be called, to bind to the PartnerEnablerService,
 * before calling any methods in this interface.
 *
 * @author CARIAD Inc
 * @version 1.0
 * @since 2023-08-21
 */
public class NavigationManagerImpl implements NavigationManager {
    private static final String TAG = NavigationManagerImpl.class.getSimpleName();

    private IPartnerEnabler mService;

    private static final String NAVIGATION_SERVICE = "NAVIGATION_SERVICE";

    private INavigationService mNavigationService;

    private final Set<NavAppStateListener> mNavAppStateListenersList = Collections.synchronizedSet(new HashSet<>());
    private final INavAppStateListener mNavAppStateListener = new NavApplicationStateListener();
    private HashSet<ActiveRouteUpdateListener> mActiveRouteListener = new HashSet<>();

    public NavigationManagerImpl(IPartnerEnabler service) {
        Log.d(TAG,"NavigationManagerImpl");
        mService = service;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response<Boolean> isNavAppStarted() {
        Response<Boolean> response = new Response<>(Response.Status.SUCCESS, new Boolean(false));
        try {
            if (mNavigationService == null) {
                initNavigationService();
            }

            int state = mNavigationService.getNavigationApplicationState();

            response.value = (state == INavigationService.NAV_APP_STATE_READY) ? true : false;
            response.status = Response.Status.SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.status = Response.Status.INTERNAL_FAILURE;
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            response.status = Response.Status.PERMISSION_DENIED;
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            response.status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
        }
        return response;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response.Status registerNavAppStateListener(NavAppStateListener navAppStateListener) {
        Response.Status status;
        try {
            if (mNavigationService == null) {
                initNavigationService();
            }

            mNavigationService.addNavAppStateListener(mNavAppStateListener);
            mNavAppStateListenersList.add(navAppStateListener);
            status = Response.Status.SUCCESS;
        } catch (RemoteException re) {
            status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
            Log.e(TAG, "getNavigationService: remoteException " + re);
        } catch (Throwable t) {
            status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
            Log.e(TAG, "getNavigationService: throwable " + t);
        }
        return status;
    }

    @Override
    public Response.Status unregisterNavAppStateListener(NavAppStateListener navAppStateListener) {
        mNavAppStateListenersList.remove(navAppStateListener);
        if (mNavAppStateListenersList.isEmpty()) {
            Response.Status status;
            try {
                if (mNavigationService == null) {
                    initNavigationService();
                }
                mNavigationService.removeNavAppStateListener(mNavAppStateListener);
                status = Response.Status.SUCCESS;
            } catch (IllegalStateException | IllegalArgumentException e) {
                e.printStackTrace();
                status = Response.Status.INTERNAL_FAILURE;
            } catch (RuntimeException | RemoteException re) {
                status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
                Log.e(TAG, "getNavigationService: remoteException " + re);
            }
            return status;
        }
        return Response.Status.SUCCESS;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response<String> getActiveRoute() {
        Response<String> response = new Response<>(Response.Status.SUCCESS, null);
        try {
            IBinder binder = mService.getAPIService(NAVIGATION_SERVICE);
            INavigationService navigationService = (INavigationService) INavigationService.Stub.asInterface(binder);
            Log.d(TAG,"Route: " + navigationService.getActiveRoute());
            response.value = navigationService.getActiveRoute();
            response.status = Response.Status.SUCCESS;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            response.status = Response.Status.INTERNAL_FAILURE;
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
            response.status = Response.Status.PERMISSION_DENIED;
        } catch (RuntimeException | RemoteException e) {
            e.printStackTrace();
            response.status = Response.Status.SERVICE_COMMUNICATION_FAILURE;
        }
        return response;
    }

    @Override
    @RequiresPermission(PartnerLibraryManager.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE)
    public Response.Status registerActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        // Add this client to listeners only if it has permission to access the navigation simplified route
        // TODO: Need to do Real permission check based implementation and error communication
        mActiveRouteListener.add(activeRouteUpdateListener);
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status unregisterActiveRouteUpdateListener(ActiveRouteUpdateListener activeRouteUpdateListener) {
        mActiveRouteListener.remove(activeRouteUpdateListener);
        return Response.Status.SUCCESS;
    }

    private void initNavigationService() throws RemoteException {
        IBinder binder = mService.getAPIService(NAVIGATION_SERVICE);
        Log.i(TAG, "getNavigationService binder=" + binder);
        mNavigationService = (INavigationService) INavigationService.Stub.asInterface(binder);
    }


    private class NavApplicationStateListener extends INavAppStateListener.Stub {
        @Override
        public void onNavAppStateChanged(int appState) throws RemoteException {
            Log.d(TAG, "calling listener onNavAppStateChanged with value: " + appState);
            synchronized (mNavAppStateListenersList) {
                for(NavAppStateListener listener: mNavAppStateListenersList) {
                    boolean started = (appState == INavigationService.NAV_APP_STATE_READY) ? true : false;
                    listener.onNavAppStateChanged(started);
                }
            }
        }
    }
}
