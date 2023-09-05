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
package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.PartnerLibraryManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.volkswagenag.partnerlibrary.ILibStateChangeListener;
import com.volkswagenag.partnerlibrary.Response;

import org.json.JSONException;


public class PartnerLibraryManagerDemoModeImpl implements PartnerLibraryManager {

    private static PartnerLibraryManagerDemoModeImpl mPartnerLibraryDemoModeImplInstance;
    private static final String TAG = PartnerLibraryManagerDemoModeImpl.class.getSimpleName();

    private Context mContext;
    private CarDataManagerDemoModeImpl mCarDataManagerDemoMode;
    private NavigationManagerDemoModeImpl mNavigationManagerDemoMode;
    private List<ILibStateChangeListener> mClientListeners = new ArrayList<>();

   public static PartnerLibraryManagerDemoModeImpl getInstance(Context context) {
        if (mPartnerLibraryDemoModeImplInstance == null) {
            mPartnerLibraryDemoModeImplInstance = new PartnerLibraryManagerDemoModeImpl(context);
        }
        return mPartnerLibraryDemoModeImplInstance;
    }

    private PartnerLibraryManagerDemoModeImpl(Context context) {
       mContext = context;
    }

    @Override
    public Response.Status initialize() {
        Log.d(TAG, "initialize");
        try {
            Set<String> permissionRequested = DemoModeUtils.getFilteredPermissionList(mContext);
            mCarDataManagerDemoMode = new CarDataManagerDemoModeImpl(mContext, permissionRequested);
            mNavigationManagerDemoMode = new NavigationManagerDemoModeImpl(mContext, permissionRequested);
        } catch (JSONException | IOException e) {
            return Response.Status.INITIALIZATION_FAILURE;
        }

        if (!mClientListeners.isEmpty()) {
            for (ILibStateChangeListener listener : mClientListeners) {
                try {
                    listener.onStateChanged(true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status release() {
        Log.d(TAG, "release");

        if (!mClientListeners.isEmpty()) {
            for (ILibStateChangeListener listener : mClientListeners) {
                try {
                    listener.onStateChanged(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status start() {
        Log.d(TAG, "start");
        mCarDataManagerDemoMode.startScheduler();
        mNavigationManagerDemoMode.startScheduler();
        return Response.Status.SUCCESS;
    }

    @Override
    public Response.Status stop() {
        Log.d(TAG, "stop");
        mCarDataManagerDemoMode.stopScheduler();
        mNavigationManagerDemoMode.stopScheduler();
        return Response.Status.SUCCESS;

    }

    @Override
    public void addListener(ILibStateChangeListener listener) {
        Log.d(TAG, "addListener");
        mClientListeners.add(listener);

    }

    @Override
    public void removeListener(ILibStateChangeListener listener) {
        Log.d(TAG, "removeListener");
        mClientListeners.remove(listener);
    }

    @Override
    public Response<CarDataManager> getCarDataManager() {
        Log.d(TAG, "getCarDataManager");
        return new Response<>(Response.Status.SUCCESS, mCarDataManagerDemoMode);
   }

    @Override
    public Response<NavigationManager> getNavigationManager() {
        Log.d(TAG, "getNavigationManager");
        return new Response<>(Response.Status.SUCCESS, mNavigationManagerDemoMode);
    }
}
