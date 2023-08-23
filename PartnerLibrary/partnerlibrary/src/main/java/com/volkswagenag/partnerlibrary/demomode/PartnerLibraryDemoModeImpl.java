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
    public void initialize() {
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
    }

    @Override
    public void release() {
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
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
        mCarDataManagerDemoMode.startScheduler();
        mNavigationManagerDemoMode.startScheduler();
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop");
        mCarDataManagerDemoMode.stopScheduler();
        mNavigationManagerDemoMode.stopScheduler();
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
    public CarDataManager getCarDataManager() {
        Log.d(TAG, "getCarDataManager");
        return mCarDataManagerDemoMode;
    }

    @Override
    public NavigationManager getNavigationManager() {
        return mNavigationManagerDemoMode;
    }
}
