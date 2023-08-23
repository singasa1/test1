package com.volkswagenag.partnerlibrary.demomode;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.volkswagenag.partnerlibrary.CarDataManager;
import com.volkswagenag.partnerlibrary.NavigationManager;
import com.volkswagenag.partnerlibrary.PartnerLibrary;

import java.util.ArrayList;
import java.util.List;

import com.volkswagenag.partnerlibrary.ILibStateChangeListener;
import com.volkswagenag.partnerlibrary.Response;


public class PartnerLibraryDemoModeImpl implements PartnerLibrary {

    private static PartnerLibraryDemoModeImpl mPartnerLibraryDemoModeImplInstance;
    private static final String TAG = PartnerLibraryDemoModeImpl.class.getSimpleName();

    private Context mContext;
    private CarDataManagerDemoModeImpl mCarDataManagerDemoMode;
    private NavigationManagerDemoModeImpl mNavigationManagerDemoMode;
    private List<ILibStateChangeListener> mClientListeners = new ArrayList<>();

   public static PartnerLibraryDemoModeImpl getInstance(Context context) {
        if (mPartnerLibraryDemoModeImplInstance == null) {
            mPartnerLibraryDemoModeImplInstance = new PartnerLibraryDemoModeImpl(context);
        }
        return mPartnerLibraryDemoModeImplInstance;
    }

    private PartnerLibraryDemoModeImpl(Context context) {
       mContext = context;
    }

    @Override
    public Response.Error initialize() {
        Log.d(TAG, "initialize");
        mCarDataManagerDemoMode = new CarDataManagerDemoModeImpl(mContext);
        mNavigationManagerDemoMode = new NavigationManagerDemoModeImpl(mContext);

        if (!mClientListeners.isEmpty()) {
            for (ILibStateChangeListener listener : mClientListeners) {
                try {
                    listener.onStateChanged(true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        return Response.Error.NONE;
    }

    @Override
    public Response.Error release() {
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
        return Response.Error.NONE;
    }

    @Override
    public Response.Error start() {
        Log.d(TAG, "start");
        mCarDataManagerDemoMode.startScheduler();
        mNavigationManagerDemoMode.startScheduler();
        return Response.Error.NONE;
    }

    @Override
    public Response.Error stop() {
        Log.d(TAG, "stop");
        mCarDataManagerDemoMode.stopScheduler();
        mNavigationManagerDemoMode.stopScheduler();
        return Response.Error.NONE;

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
        return new Response<>(Response.Error.NONE, mCarDataManagerDemoMode);
   }

    @Override
    public Response<NavigationManager> getNavigationManager() {
        Log.d(TAG, "getNavigationManager");
        return new Response<>(Response.Error.NONE, mNavigationManagerDemoMode);
    }
}
