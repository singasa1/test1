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

package technology.cariad.partnerenablerservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.GuardedBy;

import technology.cariad.partnerenablerservice.INavigationService;
import technology.cariad.partnerenablerservice.INavAppStateListener;
import technology.cariad.partnerenablerservice.IActiveRouteUpdateListener;

import com.volkswagenag.nav.applicationstate.ApplicationStateType;
import com.volkswagenag.nav.applicationstate.IApplicationStateCallback;
import com.volkswagenag.nav.route.simplifier.IRouteSimplifier;

public class NavigationService extends INavigationService.Stub {
    private static final String TAG = "PartnerEnablerService.INavigationService";

    private static final String NAV_APPSTATE_ACTION_NAME = "com.volkswagenag.nav.service.ApplicationState.BIND";
    private static final String NAV_ROUTE_ACTION_NAME = "com.volkswagenag.nav.cloud.truffles.RouteSimplifierService.BIND";

    //    private static final String NAV_APP_PACKAGE_NAME = "technology.cariad.navi.oi.volkswagen";
    private static final String NAV_APP_PACKAGE_NAME = "technology.cariad.navi.audi";

    private Context mContext;
    /** List of clients listening to Navigation Application State */
    private final RemoteCallbackList<INavAppStateListener> mNavAppStateListeners =
            new RemoteCallbackList<>();

    /** List of clients listening to ActiveRoute updates */
    private final RemoteCallbackList<IActiveRouteUpdateListener> mActiveRouteUpdateListeners =
            new RemoteCallbackList<>();

    public boolean mIsNavAppStateServiceConnected = false;
    public boolean mIsRouteSimplifierServiceConnected = false;
    private com.volkswagenag.nav.applicationstate.IApplicationState mApplicationState;
    private NavAppStateServiceConnection mNavAppStateServiceConnection;
    private NavAppStateListener mNavAppStateListener;
    private int mApplicationStateType;

    private NavRouteServiceConnection mNavRouteServiceConnection;
    private IRouteSimplifier mRouteSimplifier;

    /**
     * This class represents the actual service connection. It casts the bound
     * stub implementation of the service to the AIDL interface.
     */
    class NavAppStateServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            Log.d(TAG, "onServiceConnected() connected");
//            mApplicationState =  com.volkswagenag.nav.applicationstate.v2.IApplicationState.Stub.asInterface((IBinder) boundService);
            if (boundService != null) {
                // Map to the correct interface version
                //if (mTargetVersion == 2) {
                //  mApplicationState =   com.volkswagenag.nav.applicationstate.v2.IApplicationState.Stub.asInterface((IBinder) boundService);
                //}
                mIsNavAppStateServiceConnected = true;
                mApplicationState = com.volkswagenag.nav.applicationstate.IApplicationState.Stub.asInterface((IBinder) boundService);
                try {
                    int version = mApplicationState.getIfcVersion();
                    Log.d(TAG, "IFCVersion from api : " + version + ", ifcversion from aidl const " + mApplicationState.VERSION);
                    //initRouteSimplifierConnection();
                    mApplicationState.registerCallback(mNavAppStateListener);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Log.e(TAG,"boundService is null");
            }

        }

        public void onServiceDisconnected(ComponentName name) {
            mApplicationState = null;
            mIsNavAppStateServiceConnected = false;
            Log.d(TAG, "onServiceDisconnected() disconnected");
        }
    }

    class NavRouteServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder boundService) {
            Log.d(TAG,"RouteSimplifier onServiceConnected");
            if (boundService != null) {
                // Map to the route simplifier
                mRouteSimplifier = com.volkswagenag.nav.route.simplifier.IRouteSimplifier.Stub.asInterface((IBinder) boundService);
                int version = 0;
                try {
                    version = mRouteSimplifier.getIfcVersion();
                    String route = mRouteSimplifier.getSimplifiedActiveRoute();
                    Log.d(TAG,"Route: " + route);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                mIsRouteSimplifierServiceConnected = true;
                Log.d(TAG, "IFCVersion from api : " + version + ", ifcversion from aidl const " + mApplicationState.VERSION);

            }
            Log.d(TAG, "NavRouteService Connection onServiceConnected() connected");
        }

        public void onServiceDisconnected(ComponentName name) {
            mRouteSimplifier = null;
            mIsRouteSimplifierServiceConnected = false;
            Log.d(TAG, "NavRouteService onServiceDisconnected() disconnected");
        }
    }

    class NavAppStateListener extends IApplicationStateCallback.Stub {
        //@Override
        public void onCurrentState(ApplicationStateType applicationStateType) {
            Log.d(TAG,"onCurrentState Callback: " + applicationStateType);
        }

        @Override
        public void onCurrentState(int applicationStateType) throws RemoteException {
            Log.d(TAG,"onCurrentState Callback: " + applicationStateType);
            if (applicationStateType == ApplicationStateType.OPERABLE) {
                Log.d(TAG, "Nav Application is fully operable. Initiate connection");
            }
            mApplicationStateType = applicationStateType;
            Log.d(TAG,"Dispatching NavApplicationState values changed to clients: " +  mApplicationStateType);
            int numClients = mNavAppStateListeners.beginBroadcast();
            for (int i = 0; i < numClients; i++) {
                INavAppStateListener callback = mNavAppStateListeners.getBroadcastItem(i);
                try {
                    int navAppState = (mApplicationStateType == ApplicationStateType.OPERABLE) ? NAV_APP_STATE_READY : NAV_APP_STATE_NOT_READY;
                    callback.onNavAppStateChanged(navAppState);
                } catch (RemoteException ignores) {
                    // ignore
                }
            }
            mNavAppStateListeners.finishBroadcast();
        }

        @Override
        public void onLoadingState(int loadingState) {
            Log.d(TAG,"onLoadingState Callback: " + loadingState);
        }
    }

    public NavigationService(Context context) {
        mContext = context;

        mNavAppStateServiceConnection = new NavAppStateServiceConnection();
        initApplicationStateConnection();

        mNavRouteServiceConnection = new NavRouteServiceConnection();
        initRouteSimplifierConnection();
    }

    @Override
    public int getNavigationApplicationState() {
        // Permission check
        Log.d(TAG,"getNavigationApplicationState");

        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.e(TAG,"VWAE permission not granted");
            throw new SecurityException("getNavigationApplicationState requires PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE permission");
        }
        if ((!mIsNavAppStateServiceConnected) || (mApplicationStateType != ApplicationStateType.OPERABLE)) {
            return NAV_APP_STATE_NOT_READY;
        }
        return NAV_APP_STATE_READY;
    }

    @Override
    public void addNavAppStateListener(INavAppStateListener listener) {
        Log.d(TAG,"addNavAppStateListener");

        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.e(TAG,"VWAE permission not granted");
            throw new SecurityException("getNavigationApplicationState requires PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE permission");
        }
        if (listener == null) {
            throw new IllegalArgumentException("INavAppStateListener is null");
        }
        mNavAppStateListeners.register(listener);
    }

    @Override
    public void removeNavAppStateListener(INavAppStateListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("INavAppStateListener is null");
        }
        mNavAppStateListeners.unregister(listener);
    }

    @Override
    public String getActiveRoute() {
        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.e(TAG,"VWAE permission not granted");
            throw new SecurityException("getNavigationApplicationState requires PERMISSION_RECEIVE_NAV_ACTIVE_ROUTE permission");
        }
        if ((!mIsRouteSimplifierServiceConnected) || (mApplicationStateType != ApplicationStateType.OPERABLE)) {
            return null;
        }
        try {
            String route = mRouteSimplifier.getSimplifiedActiveRoute();
            Log.d(TAG,"Route: " + route);
            return route;
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addActiveRouteUpdateListener(IActiveRouteUpdateListener listener) {}

    @Override
    public void removeActiveRouteUpdateListener(IActiveRouteUpdateListener listener) {}

    private void initApplicationStateConnection() {
        Intent intent = new Intent(NAV_APPSTATE_ACTION_NAME);

        // only one navigation service matches
        ResolveInfo ri = mContext.getPackageManager().resolveService(intent, PackageManager.GET_META_DATA);
        ServiceInfo si = ri.serviceInfo;

        // complete intent with package name
        intent.setPackage(si.packageName);

        /*int[] supportedVersions = Arrays.stream(si.metaData
                .getString("supportedVersions", "0, 0")
                .split(","))
                .mapToInt(s->Integer.parseInt(s.trim()))
                .toArray();

        // example: v2 is the highest version we can handle otherwise the default value of the variable is v1.
        if (Arrays.asList(supportedVersions).contains(2)) {
            mTargetVersion = 2;
        }

        // Build data with the pattern : service://<name>.BIND#<version>
        intent.setData(Uri.fromParts("service", NAV_APPSTATE_ACTION_NAME,
                                     String.format("%d", mTargetVersion)));*/
        boolean ret = false;
        ret = mContext.bindService(intent, mNavAppStateServiceConnection, Service.BIND_AUTO_CREATE);
        Log.d(TAG,"Return value of NavApplicationState service Start: " + ret);
    }

    private void initRouteSimplifierConnection() {
        boolean ret = false;
        Intent intent = new Intent(NAV_ROUTE_ACTION_NAME).setPackage(NAV_APP_PACKAGE_NAME);
        ret = mContext.bindService(intent, mNavRouteServiceConnection, Context.BIND_AUTO_CREATE);

        Log.d(TAG,"Return value of NavApp RouteSimplifier service Start: " + ret);
    }

    /*@Override
    public int getTurnSignalIndicator() throws RemoteException {
        // Permission check
        Log.d(TAG,"getTurnSignalIndicator");
//        if (PackageManager.PERMISSION_GRANTED != mContext.checkCallingOrSelfPermission(
//                VWAE_CAR_MILEAGE_PERMISSION)) {
        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.e(TAG,"VWAE permission not granted");
            throw new SecurityException("getTurnSignalIndicator requires TURN_SIGNAL_INDICATOR permission");
        }
        int turnSignalIndicator = (int)mCarPropertyManager.getProperty(TURN_SIGNAL_STATE, VEHICLE_AREA_TYPE_GLOBAL).getValue();
        Log.d(TAG,"TurnSignalState Value: " + turnSignalIndicator);
        return turnSignalIndicator;
    }

    @Override
    public void addTurnSignalStateListener(ITurnSignalStateListener listener) throws RemoteException {
        Log.d(TAG,"getTurnSignalIndicator");
        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_TURN_SIGNAL_INDICATOR, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.e(TAG,"VWAE permission not granted");
            throw new SecurityException("addTurnSignalStateListener requires TURN_SIGNAL_INDICATOR permission");
        }
        if (listener == null) {
            throw new IllegalArgumentException("ITurnSignalStateListener is null");
        }
        mTurnSignalStateListener.register(listener);
    }

    @Override
    public void removeTurnSignalStateListener(ITurnSignalStateListener listener) throws RemoteException {
        if (listener == null) {
            throw new IllegalArgumentException("ITurnSignalStateListener is null");
        }
        mTurnSignalStateListener.unregister(listener);
    }

    @Override
    public int getFogLightsState() throws RemoteException {
        // Permission check
        Log.d(TAG,"getFogLightsState");
        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_FOG_LIGHTS, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.d(TAG,"VWAE permission not granted");
            throw new SecurityException("getFogLightsState requires FOG_LIGHTS permission");
        }
        int fogLightState = (int)mCarPropertyManager.getProperty(FOG_LIGHTS_STATE, VEHICLE_AREA_TYPE_GLOBAL).getValue();
        Log.d(TAG,"FogLightsState Value: " + fogLightState);
        return fogLightState;
    }

    @Override
    public void addFogLightStateListener(IFogLightStateListener listener) throws RemoteException {
        Log.d(TAG,"addFogLightStateListener");
        if (PackageManager.PERMISSION_GRANTED != mContext.getPackageManager().checkPermission(
                PartnerAPI.PERMISSION_RECEIVE_FOG_LIGHTS, mContext.getPackageManager().getNameForUid(Binder.getCallingUid()))) {
            Log.d(TAG,"VWAE permission not granted");
            throw new SecurityException("addFogLightStateListener requires FOG_LIGHTS permission");
        }
        if (listener == null) {
            throw new IllegalArgumentException("IFogLightStateListener is null");
        }
        mFogLightStateListener.register(listener);
    }

    @Override
    public void removeFogLightStateListener(IFogLightStateListener listener) throws RemoteException {
        if (listener == null) {
            throw new IllegalArgumentException("IFogLightStateListener is null");
        }
        mFogLightStateListener.unregister(listener);
    }*/
}
